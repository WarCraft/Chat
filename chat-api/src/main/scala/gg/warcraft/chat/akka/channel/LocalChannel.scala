package gg.warcraft.chat.akka.channel

import akka.actor.typed.{ ActorRef, Behavior }
import akka.actor.typed.scaladsl.Behaviors
import gg.warcraft.chat.akka.Player
import gg.warcraft.chat.message.Message
import gg.warcraft.monolith.api.util.ColorCode

object LocalChannel {
  sealed trait Command

  /** Requests the message to be sent to everyone in this channel. This will only be
    * accepted if the player is in this channel. Additionally, it will first require
    * the chat profile of the sender to be retrieved. */
  final case class SendMessage(
      player: ActorRef[Player.Command],
      message: String
  ) extends Command

  /** Requests the message to be sent to everyone in this channel. This will always
    * be accepted as this command is only sent after all required data was collected
    * to format the message. */
  private[chat] final case class SendFormattedMessage(
      player: ActorRef[Player.Command],
      message: String
  ) extends Command

  /** Creates a new local channel actor. Parent actors need to create and subscribe
    * an appropriate local channel handler to enable normal operation. */
  def apply(
      name: String,
      aliases: Set[String],
      shortcut: Option[String],
      color: ColorCode,
      formatString: String,
      radius: Float
  ): Behavior[Command] = Behaviors.receive { (context, message) =>
    message match {
      case SendMessage(player, message) =>
        // TODO query player profile
        // context.ask(?, ?) {
        //   case ? =>
        //     val formattedMessage = Message(?, profile, message).formatted
        //     SendFormattedMessage(player, formattedMessage)
        // }
        Behaviors.same

      case SendFormattedMessage(player, message) =>
        // TODO get nearby players
        players foreach (_ ! Player.SendMessage(message))
        if (players.size == 1) player ! Player.SendMessage(Message.mute)
        Behaviors.same
    }
  }
}
