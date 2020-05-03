package gg.warcraft.chat.spigot

import gg.warcraft.chat.AsyncPlayerPreChatEvent
import gg.warcraft.monolith.api.core.event.EventService
import gg.warcraft.monolith.api.player.PlayerService
import org.bukkit.event.{EventHandler, Listener}

class SpigotChatEventMapper(implicit
    eventService: EventService,
    playerService: PlayerService
) extends Listener {
  @EventHandler
  def preChat(event: SpigotAsyncPlayerChatEvent): Unit = {
    val player = playerService.getPlayer(event.getPlayer.getUniqueId)
    val preEvent = AsyncPlayerPreChatEvent(player, event.getMessage)
    eventService.publish(preEvent)
    event.setCancelled(true)
  }
}
