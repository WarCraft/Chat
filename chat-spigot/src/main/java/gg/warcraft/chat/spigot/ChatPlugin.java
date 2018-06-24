package gg.warcraft.chat.spigot;

import com.google.inject.Injector;
import gg.warcraft.chat.api.ChatRouter;
import gg.warcraft.chat.api.channel.service.ChannelCommandService;
import gg.warcraft.chat.api.config.ChatConfiguration;
import gg.warcraft.chat.app.profile.handler.ChatProfileInitializationHandler;
import gg.warcraft.monolith.api.Monolith;
import gg.warcraft.monolith.api.config.service.ConfigurationCommandService;
import gg.warcraft.monolith.api.config.service.ConfigurationQueryService;
import gg.warcraft.monolith.api.core.EventService;
import gg.warcraft.monolith.api.persistence.YamlMapper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class ChatPlugin extends JavaPlugin {

    ChatConfiguration loadChatConfiguration(FileConfiguration localConfig, Injector injector) {
        ConfigurationQueryService configQueryService = injector.getInstance(ConfigurationQueryService.class);
        ConfigurationCommandService configCommandService = injector.getInstance(ConfigurationCommandService.class);
        ChatConfiguration chatConfiguration = configQueryService.getConfiguration(ChatConfiguration.class);
        if (chatConfiguration == null) {
            try {
                String configFileName = localConfig.getString("configurationFileName");
                configCommandService.reloadConfiguration(configFileName, ChatConfiguration.class);
                chatConfiguration = configQueryService.getConfiguration(ChatConfiguration.class);
            } catch (IOException ex) {
                System.out.println("Failed to load Chat configuration: " + ex.getMessage());
            }

            if (chatConfiguration == null) {
                System.out.println("Using local Chat configuration.");
                YamlMapper yamlMapper = injector.getInstance(YamlMapper.class);
                chatConfiguration = yamlMapper.parse(localConfig.saveToString(), ChatConfiguration.class);
            }
        }
        return chatConfiguration;
    }

    void readChatConfiguration(ChatConfiguration configuration, Injector injector) {
        ChannelCommandService channelCommandService = injector.getInstance(ChannelCommandService.class);
        configuration.getGlobalChannels().forEach(channel -> {
            // TODO: integrate required permission from configuration
            channelCommandService.createGlobalChannel(channel.getName(), channel.getAliases(), channel.getShortcut(),
                    channel.getColor(), channel.getFormattingString(), uuid -> true);
        });
        configuration.getLocalChannels().forEach(channel -> {
            channelCommandService.createLocalChannel(channel.getName(), channel.getAliases(), channel.getShortcut(),
                    channel.getColor(), channel.getFormattingString(), channel.getRadius());
        });
        // TODO: initialize message logger depending on configuration
    }

    void initializeEventHandlers(Injector injector) {
        EventService eventService = injector.getInstance(EventService.class);
        ChatRouter chatRouter = injector.getInstance(ChatRouter.class);
        ChatProfileInitializationHandler profileInitializationHandler =
                injector.getInstance(ChatProfileInitializationHandler.class);
        eventService.subscribe(chatRouter);
        eventService.subscribe(profileInitializationHandler);
    }

    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        FileConfiguration localConfig = getConfig();
        Injector injector = Monolith.getInstance().getInjector();

        ChatConfiguration chatConfiguration = loadChatConfiguration(localConfig, injector);
        readChatConfiguration(chatConfiguration, injector);

        initializeEventHandlers(injector);
    }
}
