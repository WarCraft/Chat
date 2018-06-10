package gg.warcraft.chat.api.event;

import gg.warcraft.monolith.api.entity.player.event.PlayerEvent;

/**
 * This event is fired after a chat event is handled by the appropriate channel when its pre-variant wasn't cancelled.
 */
public interface AsyncPlayerChatEvent extends PlayerEvent {

    /**
     * Returns the chat text that caused this event to fire.
     *
     * @return The chat text that caused this event to fire.
     */
    String getText();
}
