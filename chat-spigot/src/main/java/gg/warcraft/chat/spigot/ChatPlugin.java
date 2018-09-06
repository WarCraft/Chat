package gg.warcraft.chat.spigot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.base.Strings;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import gg.warcraft.chat.api.ChatRouter;
import gg.warcraft.chat.api.channel.service.ChannelCommandService;
import gg.warcraft.chat.api.config.ChatConfiguration;
import gg.warcraft.chat.app.config.ChatMapperModule;
import gg.warcraft.chat.app.profile.handler.ChatProfileInitializationHandler;
import gg.warcraft.chat.spigot.event.SpigotChatEventMapper;
import gg.warcraft.monolith.api.Monolith;
import gg.warcraft.monolith.api.config.service.ConfigurationCommandService;
import gg.warcraft.monolith.api.config.service.ConfigurationQueryService;
import gg.warcraft.monolith.api.core.EventService;
import gg.warcraft.monolith.api.core.YamlMapper;
import gg.warcraft.monolith.api.entity.service.EntityQueryService;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class ChatPlugin extends JavaPlugin {

    ChatConfiguration loadLocalChatConfiguration(FileConfiguration localConfig, ObjectMapper yamlMapper) {
        try {
            return yamlMapper.readValue(localConfig.saveToString(), ChatConfiguration.class);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load local chat configuration: " + ex.getMessage());
        }
    }

    ChatConfiguration loadRemoteChatConfiguration(FileConfiguration localConfiguration, Injector injector) {
        String configurationFileName = localConfiguration.getString("configurationFileName");
        ConfigurationQueryService configQueryService = injector.getInstance(ConfigurationQueryService.class);
        ChatConfiguration chatConfiguration = configQueryService.getConfiguration(ChatConfiguration.class);
        if (chatConfiguration == null) {
            Logger logger = Bukkit.getLogger();
            logger.info("Remote Chat configuration missing from cache, attempting to load...");
            try {
                ConfigurationCommandService configCommandService = injector.getInstance(ConfigurationCommandService.class);
                configCommandService.reloadConfiguration(configurationFileName, ChatConfiguration.class);
                chatConfiguration = configQueryService.getConfiguration(ChatConfiguration.class);
                logger.info("Successfully loaded remote Chat configuration.");
            } catch (IOException ex) {
                logger.warning("Exception loading remote Chat configuration: " + ex.getMessage());
                return null;
            }
        }
        return chatConfiguration;
    }

    ChatConfiguration loadChatConfiguration(FileConfiguration localConfiguration, Injector injector) {
        ObjectMapper yamlMapper = injector.getInstance(Key.get(ObjectMapper.class, YamlMapper.class));
        SimpleModule chatMapperModule = new ChatMapperModule();
        yamlMapper.registerModule(chatMapperModule);

        String configurationType = localConfiguration.getString("configurationType");
        switch (configurationType) {
            case "REMOTE":
                ChatConfiguration chatConfiguration = loadRemoteChatConfiguration(localConfiguration, injector);
                if (chatConfiguration != null) {
                    return chatConfiguration;
                } else {
                    Logger logger = Bukkit.getLogger();
                    logger.warning("Failed to load remote Chat configuration.");
                    logger.warning("Falling back to LOCAL.");
                }
            case "LOCAL":
                return loadLocalChatConfiguration(localConfiguration, yamlMapper);
            default:
                Logger logger = Bukkit.getLogger();
                logger.warning("Illegal configurationType in Chat configuration: " + configurationType);
                logger.warning("Falling back to LOCAL.");
                return loadLocalChatConfiguration(localConfiguration, yamlMapper);
        }
    }

    void readChatConfiguration(ChatConfiguration configuration, Injector injector) {
        ChannelCommandService channelCommandService = injector.getInstance(ChannelCommandService.class);
        EntityQueryService entityQueryService = injector.getInstance(EntityQueryService.class);
        configuration.getGlobalChannels().forEach(channel -> {
            Predicate<UUID> permissionCheck = Strings.isNullOrEmpty(channel.getRequiredPermission())
                    ? uuid -> true
                    : uuid -> entityQueryService.getEntity(uuid).hasPermission(channel.getRequiredPermission());
            channelCommandService.createGlobalChannel(channel.getName(), channel.getAliases(), channel.getShortcut(),
                    channel.getColor(), channel.getFormattingString(), permissionCheck);
        });
        configuration.getLocalChannels().forEach(channel -> {
            channelCommandService.createLocalChannel(channel.getName(), channel.getAliases(), channel.getShortcut(),
                    channel.getColor(), channel.getFormattingString(), channel.getRadius());
        });
    }

    void initializeMonolithEventHandlers(Injector injector) {
        EventService eventService = injector.getInstance(EventService.class);
        ChatRouter chatRouter = injector.getInstance(ChatRouter.class);
        ChatProfileInitializationHandler profileInitializationHandler =
                injector.getInstance(ChatProfileInitializationHandler.class);
        eventService.subscribe(chatRouter);
        eventService.subscribe(profileInitializationHandler);
    }

    void initializeSpigotEventHandlers(Injector injector) {
        SpigotChatEventMapper chatEventMapper = injector.getInstance(SpigotChatEventMapper.class);
        getServer().getPluginManager().registerEvents(chatEventMapper, this);
    }

    @Override
    public void onLoad() {
        saveDefaultConfig();
        FileConfiguration localConfig = getConfig();
        String messageLoggerType = localConfig.getString("messageLogger");

        Module spigotChatModule = new SpigotChatModule(this, messageLoggerType);
        Monolith.registerModule(spigotChatModule);
    }

    @Override
    public void onEnable() {
        FileConfiguration localConfig = getConfig();
        Injector injector = Monolith.getInstance().getInjector();

        ChatConfiguration chatConfiguration = loadChatConfiguration(localConfig, injector);
        readChatConfiguration(chatConfiguration, injector);

        initializeMonolithEventHandlers(injector);
        initializeSpigotEventHandlers(injector);
    }
}
