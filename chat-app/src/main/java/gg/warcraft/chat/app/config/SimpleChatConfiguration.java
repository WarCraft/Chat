package gg.warcraft.chat.app.config;

import gg.warcraft.chat.api.config.ChatConfiguration;
import gg.warcraft.chat.api.config.GlobalChannelConfiguration;
import gg.warcraft.chat.api.config.LocalChannelConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleChatConfiguration implements ChatConfiguration {
    private final List<SimpleGlobalChannelConfiguration> globalChannels;
    private final List<SimpleLocalChannelConfiguration> localChannels;

    public SimpleChatConfiguration() {
        this.globalChannels = new ArrayList<>();
        this.localChannels = new ArrayList<>();
    }

    public SimpleChatConfiguration(List<SimpleGlobalChannelConfiguration> globalChannels,
                                   List<SimpleLocalChannelConfiguration> localChannels) {
        this.globalChannels = globalChannels;
        this.localChannels = localChannels;
    }

    @Override
    public List<GlobalChannelConfiguration> getGlobalChannels() {
        return globalChannels.stream()
                .map(configuration -> (GlobalChannelConfiguration) configuration)
                .collect(Collectors.toList());
    }

    @Override
    public List<LocalChannelConfiguration> getLocalChannels() {
        return localChannels.stream()
                .map(configuration -> (LocalChannelConfiguration) configuration)
                .collect(Collectors.toList());
    }
}
