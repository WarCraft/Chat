package gg.warcraft.chat.api.event;

import gg.warcraft.monolith.api.entity.player.event.PlayerEvent;
import gg.warcraft.monolith.api.util.Cancellable;

/**
 * An AsyncPrePlayerChatEvent will be fired whenever a {@code Player} has sent a message to the server, but before it is
 * handled by any {@code Channel}.
 */
public interface AsyncPrePlayerChatEvent extends PlayerEvent, Cancellable {

    /**
     * @return The chat message.
     */
    String getText();
}
