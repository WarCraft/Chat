package gg.warcrat.chat.app.event;

import gg.warcraft.monolith.api.chat.channel.Channel;
import gg.warcraft.monolith.api.chat.event.PlayerHomeChannelChangedEvent;

import java.util.UUID;

public class SimplePlayerHomeChannelChangedEvent implements PlayerHomeChannelChangedEvent {
    private final UUID playerId;
    private final Channel channel;

    public SimplePlayerHomeChannelChangedEvent(UUID playerId, Channel channel) {
        this.playerId = playerId;
        this.channel = channel;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public UUID getPlayerId() {
        return playerId;
    }
}
