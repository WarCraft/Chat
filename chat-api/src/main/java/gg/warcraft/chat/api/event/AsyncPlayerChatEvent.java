package gg.warcraft.chat.api.event;

import gg.warcraft.monolith.api.entity.player.event.PlayerEvent;

/**
 * An AsyncPlayerChatEvent will be fired whenever a {@code Player} chat message has been handled by a {@code Channel}.
 */
public interface AsyncPlayerChatEvent extends PlayerEvent {

    /**
     * @return The formatted chat message.
     */
    String getText();
}
