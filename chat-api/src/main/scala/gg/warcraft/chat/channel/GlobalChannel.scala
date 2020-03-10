package gg.warcraft.chat.channel

import java.util.UUID
import java.util.logging.Logger

import gg.warcraft.chat.message.{Message, MessageAdapter}
import gg.warcraft.chat.profile.ChatProfileService
import gg.warcraft.monolith.api.core.command.{Command, CommandSender}
import gg.warcraft.monolith.api.core.event.{Event, EventHandler}
import gg.warcraft.monolith.api.entity.player.Player
import gg.warcraft.monolith.api.entity.player.service.PlayerQueryService
import gg.warcraft.monolith.api.player.{
  PlayerConnectEvent, PlayerDisconnectEvent, PlayerPermissionsChangedEvent
}
import gg.warcraft.monolith.api.util.ColorCode

case class GlobalChannel(
    name: String,
    aliases: Set[String],
    shortcut: Option[String],
    color: ColorCode,
    format: String,
    permission: Option[String]
)(
    implicit logger: Logger,
    playerService: PlayerQueryService,
    profileService: ChatProfileService,
    messageAdapter: MessageAdapter
) extends Channel
    with EventHandler {
  private final val missingPermissions =
    "You do not have the required permissions to join [%s]."
  private final val successfullyJoined =
    "You have joined [%s]."
  private final val homeChannelPlayersOnly =
    "Only players can set their home channel."

  private var recipients = Set[UUID]()

  override def handle(sender: CommandSender, cmd: Command): Boolean = sender match {
    case CommandSender(_, Some(playerId)) =>
      def fixMissing(): Unit = {
        logger warning s"$playerId was not in $name, but channel has no permission!"
        recipients += playerId
        val message = Message.server(successfullyJoined.format(name))
        messageAdapter.send(message, playerId)
      }

      val authenticated = if (!recipients.contains(playerId)) {
        permission match {
          case Some(permission) =>
            val player: Player = playerService.getPlayer(playerId)
            if (player.hasPermission(permission)) {
              fixMissing(); true
            } else {
              val message = Message.server(missingPermissions.format(name))
              messageAdapter.send(message, playerId); false
            }

          case _ => fixMissing(); true
        }
      } else true

      if (authenticated) {
        if (cmd.args.isEmpty) makeHome(playerId)
        else broadcast(sender, cmd.args.mkString(" "), recipients)
      }
      Command.success

    case _ =>
      if (cmd.args.isEmpty) logger info homeChannelPlayersOnly
      else broadcast(sender, cmd.args.mkString(" "), recipients)
      Command.success
  }

  override def handle(event: Event): Unit = event match {
    case it: PlayerConnectEvent            => handle(it)
    case it: PlayerPermissionsChangedEvent => handle(it)
    case it: PlayerDisconnectEvent         => handle(it)
    case _                                 =>
  }

  private def handle(event: PlayerConnectEvent): Unit = {
    import event.playerId
    permission match {
      case Some(permission) =>
        val player: Player = playerService.getPlayer(playerId)
        if (player.hasPermission(permission)) recipients += playerId
      case None => recipients += playerId
    }
  }

  private def handle(event: PlayerPermissionsChangedEvent): Unit = {
    import event.playerId
    permission match {
      case Some(permission) =>
        val player: Player = playerService.getPlayer(playerId)
        if (recipients.contains(playerId)) {
          if (!player.hasPermission(permission)) recipients -= playerId
        } else if (player.hasPermission(permission)) recipients += playerId
      case None =>
    }
  }

  private def handle(event: PlayerDisconnectEvent): Unit = {
    import event.playerId
    permission match {
      case Some(permission) =>
        val player: Player = playerService.getPlayer(playerId)
        if (player.hasPermission(permission)) recipients -= playerId
      case None => recipients -= playerId
    }
  }
}
