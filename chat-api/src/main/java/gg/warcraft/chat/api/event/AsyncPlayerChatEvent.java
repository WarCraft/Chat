package gg.warcraft.chat.api.event;

import gg.warcraft.monolith.api.core.event.Event;

/**
 * An AsyncPlayerChatEvent will be fired whenever a {@code Player} chat message has been handled by a {@code Channel}.
 */
public interface AsyncPlayerChatEvent extends Event {

    /**
     * @return The formatted chat message.
     */
    String getText();
}
