package gg.warcraft.chat.app.profile;

import gg.warcraft.chat.api.channel.Channel;
import gg.warcraft.chat.api.profile.ChatProfile;
import gg.warcraft.chat.api.profile.ChatTag;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class ConsoleChatProfile implements ChatProfile {
    private static final String NAME = "Console";
    private static final ChatTag TAG = new ConsoleChatTag();
    private static final Set<String> OPTED_OUT = Collections.emptySet();

    @Override
    public UUID getPlayerId() {
        return null;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public ChatTag getTag() {
        return TAG;
    }

    @Override
    public String getHomeChannel() {
        return null;
    }

    @Override
    public Set<String> getOptedOut() {
        return OPTED_OUT;
    }

    @Override
    public boolean hasOptedOut(Channel channel) {
        return false;
    }
}
