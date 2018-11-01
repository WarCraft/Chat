package gg.warcraft.chat.api.config;

import java.util.List;

public interface ChatConfiguration {

    String getDefaultChannel();

    List<GlobalChannelConfiguration> getGlobalChannels();

    List<LocalChannelConfiguration> getLocalChannels();
}
