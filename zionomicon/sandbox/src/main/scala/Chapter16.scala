import zio._

object Chapter16 extends App {

  trait MemoMap[-K, -R, +E, +V] {
    def get(k: K): ZManaged[R, E, V]
  }

  object MemoMap {

//    def make[K, R, E, V](lookup: K => ZManaged[R, E, V]): UIO[MemoMap[K, R, E, V]] =
//      Ref.make[Map[K, (ZIO[R, E, V], ZManaged.Finalizer)]](Map.empty).map { ref =>
//        new MemoMap[K, R, E, V] {
//          def get(k: K): ZManaged[R, E, V] =
//            ZManaged {
//              ref.modify { map =>
//                map.get(k) match {
//                  case Some((acquire, release)) =>
//                    ???
//                  case None =>
//                    for {
//                      observers    <- Ref.make(0)
//                      promise      <- Promise.make[Any, Any]
//                      finalizerRef <- Ref.make[ZManaged.Finalizer](ZManaged.Finalizer.noop)
//                      resource = ZIO.uninterruptibleMask { restore =>
//                        for {
//                          (r, outerReleaseMap) <- ZIO.environment[(R, ZManaged.ReleaseMap)]
//                          innerReleaseMap <- ZManaged.ReleaseMap.make
//                          result <- restore(lookup(k).zio.provide((r, innerReleaseMap))).run.flatMap {
//                            case e @ Exit.Failure(cause) =>
//                              promise.halt(cause) *>
//                                innerReleaseMap.releaseAll(e, ExecutionStrategy.Sequential) *>
//                                ZIO.halt(cause)
//                            case Exit.Success((_, v)) =>
//                              for {
//                                _ <- finalizerRef.set { (e: Exit[Any, Any]) =>
//                                  ZIO.whenM(observers.modify(n => (n == 1, n - 1)))(
//                                    innerReleaseMap.releaseAll(e, ExecutionStrategy.Sequential) ) }
//                                _ <- observers.update(_ + 1)
//                                outerFinalizer <- outerReleaseMap.add(e => finalizerRef.get.flatMap(_.apply(e)))
//                                _ <- promise.succeed(v)
//                              } yield (outerFinalizer, v)
//                          } } yield result
//                      }
//                      memoized = (
//                        promise.await.onExit {
//                          case Exit.Failure(_) => ZIO.unit
//                          case Exit.Success(_) => observers.update(_ + 1)
//                        },
//                        (exit: Exit[Any, Any]) => finalizerRef.get.flatMap(_(exit))
//                      ) } yield (resource, map + (k -> memoized))
//                }
//              }.flatten
//        }
//      }
//    }
  }


  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = ???
}
