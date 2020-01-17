package gg.warcraft.chat.api.event;

import gg.warcraft.chat.api.channel.Channel;
import gg.warcraft.monolith.api.core.event.Event;

/**
 * A PlayerHomeChannelChangedEvent will be fired whenever a {@code Player} has changed their home channel.
 */
public interface PlayerHomeChannelChangedEvent extends Event {

    /**
     * @return The new home channel.
     */
    Channel getChannel();
}
