package gg.warcraft.chat.akka.channel

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import gg.warcraft.chat.akka.Player
import gg.warcraft.chat.message.Message
import gg.warcraft.monolith.api.util.ColorCode

object GlobalChannel {
  sealed trait Command

  /** Requests the player to be added to this channel. If this channel operates on a
    * permission basis the player will first be authenticated before being added. If
    * the player is found to have insufficient permissions they will be notified. */
  final case class AddPlayer(player: ActorRef[Player.Command]) extends Command

  /** Requests the player to be removed from this channel. This will always be
    * accepted. If the player is not currently in this channel nothing happens. */
  final case class RemovePlayer(player: ActorRef[Player.Command]) extends Command

  /** Requests the player to be added to this channel. This will always be accepted
    * as this command is only sent after authenticating the player. */
  private[chat] final case class AddAuthenticatedPlayer(
      player: ActorRef[Player.Command]
  ) extends Command

  /** Requests the message to be sent to everyone in this channel. This will only be
    * accepted if the player is in this channel. Additionally, it will first require
    * the chat profile of the sender to be retrieved. */
  final case class SendMessage(
      player: ActorRef[Player.Command],
      message: String
  ) extends Command

  /** Requests the message to be broadcast to everyone in this channel. This will
    * always be accepted and should only be used for server messages. Messages will
    * be formatted as such. */
  final case class BroadcastMessage(message: String) extends Command

  /** Requests the message to be sent to everyone in this channel. This will always
    * be accepted as this command is only sent after all required data was collected
    * to format the message. */
  private[chat] final case class SendFormattedMessage(
      player: ActorRef[Player.Command],
      message: String
  ) extends Command

  /** Creates a new global channel actor. Parent actors need to create and subscribe
    * a global channel handler to enable normal operation. */
  def apply(
      name: String,
      aliases: Set[String],
      shortcut: Option[String],
      color: ColorCode,
      formatString: String,
      permission: Option[String]
  ): Behavior[Command] = {
    def GlobalChannel(
        players: Set[ActorRef[Player.Command]]
    ): Behavior[Command] = Behaviors.receive { (context, message) =>
      message match {
        case AddPlayer(player) =>
          permission match {
            case Some(it) =>
              // TODO query if player has permission
              // context.ask(?, ?) {
              //   case ? => AddAuthenticatedPlayer(player)
              // }
              Behaviors.same
            case None =>
              context.self ! AddAuthenticatedPlayer(player)
              Behaviors.same
          }

        case AddAuthenticatedPlayer(player) =>
          GlobalChannel(players + player)

        case RemovePlayer(player) =>
          GlobalChannel(players - player)

        case SendMessage(player, message) =>
          if (players contains player) {
            // TODO query player profile
            // context.ask(?, ?) {
            //   case ? =>
            //     val formattedMessage = Message(?, profile, message).formatted
            //     SendFormattedMessage(player, formattedMessage)
            // }
          } else player ! null
          // TODO send not part of this channel message, check for permissions, and advise if believe to be error please relog
          Behaviors.same

        case SendFormattedMessage(player, message) =>
          players foreach (_ ! Player.SendMessage(message))
          if (players.size == 1) player ! Player.SendMessage(Message.mute)
          Behaviors.same

        case BroadcastMessage(message) =>
          val formattedMessage = Message.server(message).formatted
          players foreach (_ ! Player.SendMessage(formattedMessage))
          Behaviors.same
      }
    }

    GlobalChannel(Set.empty)
  }
}
