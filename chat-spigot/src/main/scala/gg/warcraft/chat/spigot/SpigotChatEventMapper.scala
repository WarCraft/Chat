package gg.warcraft.chat.spigot

import gg.warcraft.chat.AsyncPlayerPreChatEvent
import gg.warcraft.monolith.api.core.event.EventService
import org.bukkit.event.{EventHandler, Listener}

class SpigotChatEventMapper(implicit eventService: EventService) extends Listener {
  @EventHandler
  def preChat(event: SpigotAsyncPlayerChatEvent): Unit = {
    val player = event.getPlayer; import player._
    val preEvent = AsyncPlayerPreChatEvent(getUniqueId, getName, event.getMessage)
    eventService publish preEvent
    event setCancelled true
  }
}
