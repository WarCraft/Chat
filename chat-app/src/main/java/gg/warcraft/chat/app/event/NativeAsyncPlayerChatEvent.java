package gg.warcrat.chat.app.event;

import gg.warcraft.monolith.api.entity.player.event.PlayerEvent;

import java.util.UUID;

public class NativeAsyncPlayerChatEvent implements PlayerEvent {
    private final UUID playerId;
    private final String text;

    public NativeAsyncPlayerChatEvent(UUID playerId, String text) {
        this.playerId = playerId;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public UUID getPlayerId() {
        return playerId;
    }
}
