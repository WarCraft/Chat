package gg.warcraft.chat.spigot

import gg.warcraft.chat.AsyncPlayerPreChatEvent
import gg.warcraft.monolith.api.core.event.EventService
import org.bukkit.event.{EventHandler, Listener}

class SpigotChatEventMapper(
    private implicit val eventService: EventService
) extends Listener {
  @EventHandler
  def preChat(event: SpigotAsyncPlayerChatEvent): Unit = {
    val playerId = event.getPlayer.getUniqueId
    val name = event.getPlayer.getName
    val preEvent = AsyncPlayerPreChatEvent(playerId, name, event.getMessage)
    eventService.publish(preEvent);
    event.setCancelled(true);
  }
}
