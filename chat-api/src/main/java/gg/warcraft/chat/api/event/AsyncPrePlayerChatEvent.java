package gg.warcraft.chat.api.event;

import gg.warcraft.monolith.api.core.event.CancellableEvent;

/**
 * An AsyncPrePlayerChatEvent will be fired whenever a {@code Player} has sent a message to the server, but before it is
 * handled by any {@code Channel}.
 */
public interface AsyncPrePlayerChatEvent extends CancellableEvent {

    /**
     * @return The chat message.
     */
    String getText();
}
