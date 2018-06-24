package gg.warcraft.chat.app.config;

import gg.warcraft.chat.api.config.ChatConfiguration;
import gg.warcraft.chat.api.config.GlobalChannelConfiguration;
import gg.warcraft.chat.api.config.LocalChannelConfiguration;

import java.util.ArrayList;
import java.util.List;

public class SimpleChatConfiguration implements ChatConfiguration {
    private final List<GlobalChannelConfiguration> globalChannels;
    private final List<LocalChannelConfiguration> localChannels;
    private final String messageLogger;

    public SimpleChatConfiguration() {
        this.globalChannels = new ArrayList<>();
        this.localChannels = new ArrayList<>();
        this.messageLogger = "CONSOLE";
    }

    public SimpleChatConfiguration(List<GlobalChannelConfiguration> globalChannels,
                                   List<LocalChannelConfiguration> localChannels, String messageLogger) {
        this.globalChannels = globalChannels;
        this.localChannels = localChannels;
        this.messageLogger = messageLogger;
    }

    @Override
    public List<GlobalChannelConfiguration> getGlobalChannels() {
        return globalChannels;
    }

    @Override
    public List<LocalChannelConfiguration> getLocalChannels() {
        return localChannels;
    }

    @Override
    public String getMessageLogger() {
        return messageLogger;
    }
}
