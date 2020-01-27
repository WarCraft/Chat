package gg.warcraft.chat.channel

import java.util.UUID

import gg.warcraft.chat.profile.ChatProfileService
import gg.warcraft.chat.{Message, MessageAdapter}
import gg.warcraft.monolith.api.core.command.{Command, CommandSender}
import gg.warcraft.monolith.api.core.event.{Event, EventHandler}
import gg.warcraft.monolith.api.entity.player.Player
import gg.warcraft.monolith.api.entity.player.service.PlayerQueryService
import gg.warcraft.monolith.api.player.{
  PlayerConnectEvent, PlayerDisconnectEvent, PlayerPermissionsChangedEvent
}
import gg.warcraft.monolith.api.util.ColorCode

import scala.collection.mutable

case class GlobalChannel(
    name: String,
    aliases: Set[String],
    shortcut: Option[String],
    color: ColorCode,
    formatString: String,
    permission: Option[String]
)(
    private implicit val playerService: PlayerQueryService,
    override protected implicit val profileService: ChatProfileService,
    override protected implicit val messageAdapter: MessageAdapter
) extends Channel
    with EventHandler {
  private final val missingPermissions =
    "You do not have the required permissions to join [%s]."
  private final val successfullyJoined =
    "You have joined [%s]."
  private final val homeChannelPlayersOnly =
    "Only players can set their home channel."

  private val recipients = mutable.ListBuffer[UUID]()

  override def handle(sender: CommandSender, cmd: Command): Boolean = sender match {
    case CommandSender(_, Some(playerId)) =>
      val joined = if (!recipients.contains(playerId)) {
        permission match {
          case Some(permission) =>
            val player: Player = playerService.getPlayer(playerId)
            if (player.hasPermission(permission)) {
              recipients += playerId
              val message = Message.server(successfullyJoined.format(name))
              // messageCommandService.sendMessageToPlayer(joinedMessage, sender.playerId().get());
              true
            } else {
              val message = Message.server(missingPermissions.format(name))
              // messageCommandService.sendMessageToPlayer(missingPermissionsMessage, sender.playerId().get());
              false
            }
          case _ =>
            recipients += playerId
            val message = Message.server(successfullyJoined.format(name))
            // messageCommandService.sendMessageToPlayer(joinedMessage, sender.playerId().get());
            true
        }
      } else true

      if (joined) {
        if (cmd.args.isEmpty) makeHome(playerId)
        else broadcast(sender, cmd.args.mkString(" "), recipients)
      }

      true

    case _ =>
      if (cmd.args.isEmpty) println(homeChannelPlayersOnly)
      else broadcast(sender, cmd.args.mkString(" "), recipients)
      true
  }

  override def handle(event: Event): Unit = event match {
    case PlayerConnectEvent(playerId, _) =>
      permission match {
        case Some(permission) =>
          val player: Player = playerService.getPlayer(playerId)
          if (player.hasPermission(permission)) recipients += playerId
        case _ => recipients += playerId
      }

    case PlayerPermissionsChangedEvent(playerId, _) =>
      permission match {
        case Some(permission) =>
          val player: Player = playerService.getPlayer(playerId)
          if (recipients.contains(playerId)) {
            if (!player.hasPermission(permission)) recipients -= playerId
          } else {
            if (player.hasPermission(permission)) recipients += playerId
          }
        case _ => ()
      }

    case PlayerDisconnectEvent(playerId, _) =>
      permission match {
        case Some(permission) =>
          val player: Player = playerService.getPlayer(playerId)
          if (player.hasPermission(permission)) recipients -= playerId
        case _ => recipients -= playerId
      }

    case _ => ()
  }
}
