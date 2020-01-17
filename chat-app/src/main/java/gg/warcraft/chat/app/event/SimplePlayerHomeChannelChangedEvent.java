package gg.warcraft.chat.app.event;

import gg.warcraft.chat.api.channel.Channel;
import gg.warcraft.chat.api.event.PlayerHomeChannelChangedEvent;

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

    public UUID getPlayerId() {
        return playerId;
    }
}
