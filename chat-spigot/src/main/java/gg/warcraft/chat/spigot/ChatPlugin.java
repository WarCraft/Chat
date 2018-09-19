package gg.warcraft.chat.spigot;

import com.google.common.base.Strings;
import com.google.inject.Injector;
import com.google.inject.Module;
import gg.warcraft.chat.api.ChatRouter;
import gg.warcraft.chat.api.channel.service.ChannelCommandService;
import gg.warcraft.chat.api.config.ChatConfiguration;
import gg.warcraft.chat.app.config.ChatMapperModule;
import gg.warcraft.chat.app.profile.handler.ChatProfileInitializationHandler;
import gg.warcraft.chat.spigot.event.SpigotChatEventMapper;
import gg.warcraft.monolith.api.Monolith;
import gg.warcraft.monolith.api.MonolithPluginUtils;
import gg.warcraft.monolith.api.core.EventService;
import gg.warcraft.monolith.api.entity.service.EntityQueryService;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.function.Predicate;

public class ChatPlugin extends JavaPlugin {

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

        MonolithPluginUtils pluginUtils = injector.getInstance(MonolithPluginUtils.class);
        String configurationType = localConfig.getString("configurationType");
        String configurationFileName = localConfig.getString("configurationFileName");
        ChatConfiguration chatConfiguration = pluginUtils.loadConfiguration(configurationType, configurationFileName,
                localConfig.saveToString(), ChatConfiguration.class, new ChatMapperModule());
        readChatConfiguration(chatConfiguration, injector);

        initializeMonolithEventHandlers(injector);
        initializeSpigotEventHandlers(injector);
    }
}
