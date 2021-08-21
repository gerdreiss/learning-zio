package pro.reiss.ziokafkastreamingtutorial

import zio.*
import json.*
import kafka.consumer.{ Consumer, ConsumerSettings, Subscription }
import kafka.serde.Serde
import zio.stream.ZSink
import scala.collection.immutable.Stream.cons

object ZioKafka extends App:

  val consumerSettings =
    ConsumerSettings(List("localhost:9092"))
      .withGroupId("updates-consumer")

  val managedConsumer =
    Consumer.make(consumerSettings)

  val consumer =
    ZLayer.fromManaged(managedConsumer)

  val footballMatchesStream =
    Consumer
      .subscribeAnd(Subscription.topics("updates"))
      .plainStream(Serde.string, Serde.string)

  case class MatchPlayer(name: String, score: Int):
    override def toString = s"$name : $score"
  object MatchPlayer:
    given JsonEncoder[MatchPlayer] = DeriveJsonEncoder.gen[MatchPlayer]
    given JsonDecoder[MatchPlayer] = DeriveJsonDecoder.gen[MatchPlayer]

  case class Match(players: Array[MatchPlayer]):
    def score: String = s"${players(0)} - ${players(1)}"
  object Match:
    given JsonEncoder[Match] = DeriveJsonEncoder.gen[Match]
    given JsonDecoder[Match] = DeriveJsonDecoder.gen[Match]

  // json strings -> kafka -> jsons -> Match instances

  val matchSerde = Serde
    .string
    .inmapM { string =>
      ZIO.fromEither {
        string.fromJson[Match].left.map(error => new RuntimeException(error))
      }
    } { theMatch =>
      ZIO.effect(theMatch.toJson)
    }

  val matchesStream =
    Consumer
      .subscribeAnd(Subscription.topics("updates"))
      .plainStream(Serde.string, matchSerde)

  val matchesPrintableStream =
    matchesStream
      .map(rec => (rec.value.score, rec.offset))
      .tap {
        case (score, _) => console.putStrLn(s"| $score |")
      }
      .map(_._2)
      .aggregateAsync(Consumer.offsetBatches)

  val streamEffect = matchesPrintableStream.run(ZSink.foreach(_.commit))

  override def run(args: List[String]) =
    streamEffect.provideSomeLayer(consumer ++ console.Console.live).exitCode
