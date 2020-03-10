package gg.warcraft.chat.spigot

import java.util.UUID

import gg.warcraft.chat.message.{Message, MessageAdapter}
import gg.warcraft.monolith.api.core.AuthorizationService
import org.bukkit.Server

import scala.jdk.CollectionConverters._

class SpigotMessageAdapter(
    implicit server: Server,
    authService: AuthorizationService
) extends MessageAdapter {
  override def broadcast(message: Message): Unit =
    server.getOnlinePlayers forEach { _ sendMessage message.formatted }

  override def broadcastStaff(message: Message): Unit =
    server.getOnlinePlayers.asScala
      .filter { authService isStaff _.getUniqueId }
      .foreach { _ sendMessage message.formatted }

  override def send(message: Message, playerId: UUID): Unit = {
    val player = server.getPlayer(playerId)
    if (player != null) player sendMessage message.formatted
  }
}
