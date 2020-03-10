package gg.warcraft.chat.channel

import java.util.logging.Logger

import gg.warcraft.chat.message.MessageAdapter
import gg.warcraft.chat.profile.ChatProfileService
import gg.warcraft.monolith.api.core.command.{Command, CommandSender}
import gg.warcraft.monolith.api.entity.player.Player
import gg.warcraft.monolith.api.entity.player.service.PlayerQueryService
import gg.warcraft.monolith.api.entity.service.EntityQueryService
import gg.warcraft.monolith.api.util.ColorCode

import scala.jdk.CollectionConverters._

case class LocalChannel(
    name: String,
    aliases: Set[String],
    shortcut: Option[String],
    color: ColorCode,
    format: String,
    radius: Float
)(
    implicit logger: Logger,
    entityService: EntityQueryService,
    playerService: PlayerQueryService,
    profileService: ChatProfileService,
    messageAdapter: MessageAdapter
) extends Channel {
  private final val localChannelPlayersOnly =
    "Only players can talk in local chat channels."

  override def handle(sender: CommandSender, cmd: Command): Boolean = sender match {
    case CommandSender(_, Some(playerId)) =>
      if (cmd.args.isEmpty) makeHome(playerId)
      else {
        val player = playerService.getPlayer(playerId)
        val recipients = entityService
          .getNearbyEntities(player.getLocation, radius)
          .asScala
          .filter { _.isInstanceOf[Player] }
          .map { _.getId }
        broadcast(sender, cmd.args.mkString(" "), recipients)
      }
      Command.success

    case _ =>
      logger warning localChannelPlayersOnly
      Command.success
  }
}
