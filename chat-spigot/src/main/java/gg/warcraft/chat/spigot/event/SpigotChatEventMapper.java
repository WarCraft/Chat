package gg.warcraft.chat.spigot.event;

import com.google.inject.Inject;
import gg.warcraft.chat.app.event.NativeAsyncPlayerChatEvent;
import gg.warcraft.monolith.api.core.EventService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class SpigotChatEventMapper implements Listener {
    private final EventService eventService;

    @Inject
    public SpigotChatEventMapper(EventService eventService) {
        this.eventService = eventService;
    }

    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        NativeAsyncPlayerChatEvent nativeEvent = new NativeAsyncPlayerChatEvent(playerId, event.getMessage());
        eventService.publish(nativeEvent);
        event.setCancelled(true);
    }
}
