package gg.warcraft.chat.api.config;

import java.util.List;

public interface ChatConfiguration {

    List<GlobalChannelConfiguration> getGlobalChannels();

    List<LocalChannelConfiguration> getLocalChannels();

    String getMessageLogger();
}
