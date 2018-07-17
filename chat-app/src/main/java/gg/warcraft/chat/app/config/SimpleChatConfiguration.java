package gg.warcraft.chat.app.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import gg.warcraft.chat.api.config.ChatConfiguration;
import gg.warcraft.chat.api.config.GlobalChannelConfiguration;
import gg.warcraft.chat.api.config.LocalChannelConfiguration;

import java.util.List;
import java.util.stream.Collectors;

public class SimpleChatConfiguration implements ChatConfiguration {
    private final List<SimpleGlobalChannelConfiguration> globalChannels;
    private final List<SimpleLocalChannelConfiguration> localChannels;

    @JsonCreator
    public SimpleChatConfiguration(@JsonProperty("globalChannels") List<SimpleGlobalChannelConfiguration> globalChannels,
                                   @JsonProperty("localChannels") List<SimpleLocalChannelConfiguration> localChannels) {
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
