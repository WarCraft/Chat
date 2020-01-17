package gg.warcraft.chat.app.logger;

import com.google.inject.Inject;
import gg.warcraft.chat.api.message.Message;
import gg.warcraft.monolith.api.core.PluginLogger;

import java.util.logging.Logger;

public class PluginMessageLogger implements MessageLogger {
    private final Logger pluginLogger;

    @Inject
    public PluginMessageLogger(@PluginLogger Logger pluginLogger) {
        this.pluginLogger = pluginLogger;
    }

    @Override
    public void setGroup(String group) {
        // do nothing
    }

    @Override
    public void log(Message message) {
        String sender = message.getSender().name();
        String log = String.format("[%s] %s: %s", message.getChannel().getName(), sender, message.getOriginal());
        pluginLogger.info(log);
    }
}
