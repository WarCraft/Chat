package gg.warcraft.chat.spigot

import java.util.UUID

import gg.warcraft.chat.message.MessageAdapter
import gg.warcraft.monolith.api.core.Message
import gg.warcraft.monolith.api.core.auth.AuthService
import gg.warcraft.monolith.api.player.PlayerService
import gg.warcraft.monolith.api.util.chaining._
import org.bukkit.Server

import scala.jdk.CollectionConverters._

class SpigotMessageAdapter(implicit
    server: Server,
    authService: AuthService,
    playerService: PlayerService
) extends MessageAdapter {
  override def broadcast(message: Message): Unit =
    server.getOnlinePlayers.forEach { _.sendMessage(message.formatted) }

  override def broadcastStaff(message: Message): Unit =
    server.getOnlinePlayers.asScala
      .map { _.getUniqueId |> playerService.getPlayer }
      .filter { authService.isStaff }
      .foreach { _.sendMessage(message) }

  override def send(message: Message, playerId: UUID): Unit =
    playerService.getPlayer(playerId) |> { _.sendMessage(message) }
}
