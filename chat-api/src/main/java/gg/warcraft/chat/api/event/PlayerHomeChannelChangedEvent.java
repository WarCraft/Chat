package gg.warcraft.chat.api.event;

import gg.warcraft.chat.api.channel.Channel;
import gg.warcraft.monolith.api.entity.player.event.PlayerEvent;

public interface PlayerHomeChannelChangedEvent extends PlayerEvent {

    Channel getChannel();
}
