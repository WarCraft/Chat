package gg.warcraft.chat.spigot;

import gg.warcraft.chat.api.service.ChatServerAdapter;
import gg.warcraft.chat.app.AbstractChatModule;
import gg.warcraft.monolith.api.core.PluginLogger;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class SpigotChatModule extends AbstractChatModule {
    private final Plugin plugin;

    public SpigotChatModule(Plugin plugin, String messageLoggerType) {
        super(messageLoggerType);
        this.plugin = plugin;
    }

    @Override
    public void configure() {
        super.configure();
        bind(Plugin.class).toInstance(plugin);
        bind(Logger.class).annotatedWith(PluginLogger.class).toProvider(plugin::getLogger);

        bind(ChatServerAdapter.class).to(SpigotChatAdapter.class);
        expose(ChatServerAdapter.class);
    }
}
