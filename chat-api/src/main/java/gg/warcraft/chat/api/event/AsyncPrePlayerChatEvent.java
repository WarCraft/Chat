package gg.warcraft.chat.api.event;

import gg.warcraft.monolith.api.entity.player.event.PlayerEvent;
import gg.warcraft.monolith.api.util.Cancellable;

/**
 * This event is fired before the chat event is handled by any chat channel. If cancelled nothing will happen.
 */
public interface AsyncPrePlayerChatEvent extends PlayerEvent, Cancellable {

    /**
     * Returns the chat text that caused this event to fire.
     *
     * @return The chat text that caused this event to fire.
     */
    String getText();
}
