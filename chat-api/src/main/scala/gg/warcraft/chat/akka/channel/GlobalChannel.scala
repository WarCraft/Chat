package gg.warcraft.chat.akka.channel

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout
import gg.warcraft.chat.akka.Player
import gg.warcraft.chat.akka.profile.ProfileService
import gg.warcraft.chat.message.Message
import gg.warcraft.monolith.api.core.command.CommandService
import gg.warcraft.monolith.api.core.event.EventService
import gg.warcraft.monolith.api.util.ColorCode

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object GlobalChannel {
  sealed trait Command

  /** Requests the player to be added to this channel. If this channel operates on a
    * permission basis the player will first be authenticated before being added. If
    * the player is found to have insufficient permissions they will be notified. */
  final case class AddPlayer(player: Player) extends Command

  /** Requests the player to be removed from this channel. This will always be
    * accepted. If the player is not currently in this channel nothing happens. */
  final case class RemovePlayer(player: Player) extends Command

  /** Requests the player to be added to this channel. This will always be accepted
    * as this command is only sent after authenticating the player. */
  private[chat] final case class AddAuthenticatedPlayer(
      player: Player
  ) extends Command

  /** Requests the message to be sent to everyone in this channel. This will only be
    * accepted if the player is in this channel. Additionally, it will first require
    * the chat profile of the sender to be retrieved. */
  final case class SendMessage(
      player: Player,
      text: String
  ) extends Command

  /** Requests the message to be broadcast to everyone in this channel. This will
    * always be accepted and should only be used for server messages. Messages will
    * be formatted as such. */
  final case class BroadcastMessage(text: String) extends Command

  /** Requests the message to be sent to everyone in this channel. This will always
    * be accepted as this command is only sent after all required data was collected
    * to format the message. */
  private[chat] final case class SendFormattedMessage(
      player: Player,
      text: String
  ) extends Command

  /** Requests the message to be sent to the player. This will always be accepted as
    * this command is only sent when this channel has reached a no-op branch or ran
    * into an exception. Messages will be formatted as server messages. */
  private[chat] final case class NotifyPlayer(
      player: Player,
      message: String,
      error: Boolean = false
  ) extends Command

  private final val WARN_INSUFFICIENT_PERMS =
    "You do not have the right permissions to interact with this channel."

  /** Creates a new global channel actor. This actor creates and subscribes its own
    * handlers to enable normal operation. */
  def apply(
      name: String,
      aliases: Set[String],
      shortcut: Option[String],
      color: ColorCode,
      format: String,
      permission: Option[String]
  )(
      implicit timeout: Timeout = 1.second,
      commandService: CommandService,
      eventService: EventService,
      profileService: ActorRef[ProfileService.Command]
  ): Behavior[Command] = Behaviors.setup { context =>
    context.spawn(GlobalChannelHandler(context.self), "handler")

    def GlobalChannel(
        players: Set[Player]
    ): Behavior[Command] = Behaviors.receive { (context, message) =>
      message match {
        case AddPlayer(player) =>
          permission match {
            case Some(it) =>
              val refFactory = Player.ConfirmPermissions(it :: Nil, _)
              context.ask(player, refFactory) {
                case Success(result) =>
                  if (result contains it) AddAuthenticatedPlayer(player)
                  else NotifyPlayer(player, WARN_INSUFFICIENT_PERMS)
                case Failure(ex) =>
                  context.log.error(s"[Error Code 210] ${ex.getMessage}", ex)
                  NotifyPlayer(player, "210", error = true)
              }
              Behaviors.same
            case None =>
              context.self ! AddAuthenticatedPlayer(player)
              Behaviors.same
          }

        case AddAuthenticatedPlayer(player) =>
          GlobalChannel(players + player)

        case RemovePlayer(player) =>
          GlobalChannel(players - player)

        case SendMessage(player, text) =>
          if (players contains player) {
            val refFactory = ProfileService.RequestProfile(player.id, _)
            context.ask(profileService, refFactory) {
              case Success(result) =>
                val profile = result.profile
                val message = Message(null, profile, message) // TODO what to do
                // with channel
                SendFormattedMessage(player, message.formatted)
              case Failure(ex) =>
                context.log.error(s"[Error Code 220] ${ex.getMessage}", ex)
                NotifyPlayer(player, "220", error = true)
            }
          } else player ! null
          // TODO send not part of this channel message, check for permissions, and advise if believe to be error please relog
          Behaviors.same

        case SendFormattedMessage(player, text) =>
          players foreach (_ ! Player.SendMessage(text))
          if (players.size == 1)
            player ! Player.SendMessage(Message.mute.formatted)
          Behaviors.same

        case BroadcastMessage(text) =>
          val formattedMessage = Message.server(text).formatted
          players foreach (_ ! Player.SendMessage(formattedMessage))
          Behaviors.same

        case NotifyPlayer(player, message, error) =>
          if (error) player ! Player.SendErrorMessage(message)
          else player ! Player.SendServerMessage(message)
          Behaviors.same
      }

      GlobalChannel(Set.empty)
    }
  }
}
