package gg.warcraft.chat.channel

import java.util.logging.Logger

import gg.warcraft.chat.message.MessageAdapter
import gg.warcraft.chat.profile.ProfileService
import gg.warcraft.monolith.api.core.auth.Principal
import gg.warcraft.monolith.api.core.command.Command
import gg.warcraft.monolith.api.core.ColorCode
import gg.warcraft.monolith.api.entity.EntityService
import gg.warcraft.monolith.api.player.{Player, PlayerService}

case class LocalChannel(
    name: String,
    aliases: Set[String],
    shortcut: Option[String],
    color: ColorCode.Type,
    format: String,
    radius: Float
)(implicit
    logger: Logger,
    entityService: EntityService,
    playerService: PlayerService,
    profileService: ProfileService,
    messageAdapter: MessageAdapter
) extends Channel {
  override def handle(
      sender: Principal,
      command: Command,
      args: String*
  ): Command.Result = sender.principalId match {
    case Some(playerId) =>
      if (args.isEmpty) {
        makeHome(playerId)
        Command.success
      } else {
        val player = playerService.getPlayer(playerId)
        val recipients = entityService
          .getNearbyEntities(player.location, radius)
          .filter { _.isInstanceOf[Player] }
          .map { _.id }
        broadcast(sender, args.mkString(" "), recipients)
        Command.success
      }

    case _ => Command.playersOnly
  }
}
