package gg.warcraft.chat.spigot

import java.util.UUID

import gg.warcraft.chat.message.{Message, MessageAdapter}
import gg.warcraft.monolith.api.core.AuthorizationService
import org.bukkit.Server

class SpigotMessageAdapter(
    private implicit val server: Server,
    private implicit val authService: AuthorizationService
) extends MessageAdapter {
  override def broadcast(message: Message): Unit =
    server.getOnlinePlayers.forEach(_.sendMessage(message.formatted))

  override def broadcastStaff(message: Message): Unit =
    server.getOnlinePlayers.stream
      .filter(it => authService.isStaff(it.getUniqueId))
      .forEach(_.sendMessage(message.formatted))

  override def send(message: Message, playerId: UUID): Unit = {
    val player = server.getPlayer(playerId)
    if (player != null) player.sendMessage(message.formatted)
  }
}
