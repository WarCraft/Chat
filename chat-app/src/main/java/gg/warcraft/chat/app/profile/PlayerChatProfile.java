package gg.warcrat.chat.app.profile;

import gg.warcraft.monolith.api.chat.channel.Channel;
import gg.warcraft.monolith.api.chat.profile.ChatProfile;
import gg.warcraft.monolith.api.chat.profile.ChatTag;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerChatProfile implements ChatProfile {
    private final UUID playerId;
    private final String name;
    private final ChatTag tag;
    private final String homeChannel;
    private final Set<String> optedOut;

    public PlayerChatProfile(UUID playerId, String name, ChatTag tag, String homeChannel, Set<String> optedOut) {
        this.playerId = playerId;
        this.name = name;
        this.tag = tag;
        this.homeChannel = homeChannel;
        this.optedOut = optedOut;
    }

    @Override
    public UUID getPlayerId() {
        return playerId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ChatTag getTag() {
        return tag;
    }

    @Override
    public String getHomeChannel() {
        return homeChannel;
    }

    @Override
    public Set<String> getOptedOut() {
        return new HashSet<>(optedOut);
    }

    @Override
    public boolean hasOptedOut(Channel channel) {
        return optedOut.contains(channel.getName());
    }
}
