package gg.warcraft.chat.spigot

import gg.warcraft.chat.AsyncPlayerPreChatEvent
import org.bukkit.event.{EventHandler, Listener}

class SpigotChatEventMapper extends Listener {
  @EventHandler
  def preChat(event: SpigotAsyncPlayerChatEvent): Unit = {
    val playerId = event.getPlayer.getUniqueId
    val name = event.getPlayer.getName
    val preEvent = AsyncPlayerPreChatEvent(playerId, name, event.getMessage)
    eventService.publish(preEvent);
    event.setCancelled(true);
  }
}
