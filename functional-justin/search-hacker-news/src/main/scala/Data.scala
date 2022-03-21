import zio.json.*
import scala.compiletime.ops.boolean

object Data:

  type HNUserID = String
  type HNItemID = Int

  val HNMissingUserID: HNUserID = ""
  val HNMissingItemID: HNItemID = -1

  sealed trait HNData

  case class HNUser(
      id: HNUserID,  // the user's unique username. Case-sensitive. Required
      created: Int,  // Creation date of the user, in Unix time.
      karma: Int,    // The user's karma
      about: String, // The user's optional self-description, HTML
      submitted: List[HNItemID]
  ) extends HNData // List of the user's stories, polls, and comments

  object HNUser:
    given decoder: JsonDecoder[HNUser] = DeriveJsonDecoder.gen[HNUser]

  case class HNItem(
      id: HNItemID,                       // The item's unique id.
      deleted: Boolean = false,           // true if the item is deleted
      `type`: String,                     // The type of the item, On of "job", "story", "comment", "poll", or "pollopt"
      by: HNUserID = HNMissingUserID,     // The username of the item's author
      time: Int,                          // Creation date of the item, in Unix Time.
      text: String = "",                  // The comment, story or poll text. HTML.
      dead: Boolean = false,              // true if the item is dead.
      parent: HNItemID = HNMissingItemID, // the comment's parent: either another comment or the relevant story
      poll: HNItemID = HNMissingItemID,   // The pollopt's associated poll.
      kids: List[HNItemID] = List.empty,  // the ids of teh item's comments, in ranked display order.
      url: String = "",                   // The URL of the story.
      score: Int = -1,                    // The story's score, or the votes for a pollopt.
      title: String = "",                 // The title of the story, poll or job.
      parts: List[HNItemID] = List.empty, // A list of related pollopts, in display order.
      descendendants: Int = 0             // In the case of stories or polls, the total comment count.
  ) extends HNData

  object HNItem:
    given decoder: JsonDecoder[HNItem] = DeriveJsonDecoder.gen[HNItem]

  case class HNItemIDList(itemIDs: List[HNItemID]) extends HNData

  object HNItemIDList:
    given decoder: JsonDecoder[HNItemIDList] = JsonDecoder[List[HNItemID]].map(HNItemIDList(_))

  case class HNSingleItemID(itemId: HNItemID) extends HNData

  object HNSingleItemID:
    given decoder: JsonDecoder[HNSingleItemID] = JsonDecoder[HNItemID].map(HNSingleItemID(_))
