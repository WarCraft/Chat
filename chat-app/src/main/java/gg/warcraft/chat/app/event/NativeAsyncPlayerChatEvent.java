package gg.warcraft.chat.app.event;

import gg.warcraft.chat.api.event.AsyncPlayerChatEvent;

import java.util.UUID;

public class NativeAsyncPlayerChatEvent implements AsyncPlayerChatEvent {
    private final UUID playerId;
    private final String text;

    public NativeAsyncPlayerChatEvent(UUID playerId, String text) {
        this.playerId = playerId;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public UUID getPlayerId() {
        return playerId;
    }
}
