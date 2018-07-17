package gg.warcraft.chat.app.config;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import gg.warcraft.chat.api.config.ChatConfiguration;
import gg.warcraft.chat.api.config.GlobalChannelConfiguration;
import gg.warcraft.chat.api.config.LocalChannelConfiguration;

public class ChatMapperModule extends SimpleModule {

    public ChatMapperModule() {
        super("ChatMapperModule", Version.unknownVersion());
        SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
        resolver.addMapping(GlobalChannelConfiguration.class, SimpleGlobalChannelConfiguration.class);
        resolver.addMapping(LocalChannelConfiguration.class, SimpleLocalChannelConfiguration.class);
        resolver.addMapping(ChatConfiguration.class, SimpleChatConfiguration.class);
        setAbstractTypes(resolver);
    }
}
