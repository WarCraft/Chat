package gg.warcraft.chat.spigot;

import gg.warcraft.chat.app.AbstractChatModule;
import gg.warcraft.chat.app.logger.ConsoleMessageLogger;
import gg.warcraft.chat.app.logger.MessageLogger;
import gg.warcraft.chat.app.logger.NoopMessageLogger;
import org.bukkit.Bukkit;

import java.util.logging.Logger;

public class SpigotChatModule extends AbstractChatModule {
    private static String messageLoggerType;

    public static void setMessageLoggerType(String messageLoggerType) {
        SpigotChatModule.messageLoggerType = messageLoggerType;
    }

    @Override
    public void configure() {
        super.configure();

        switch (messageLoggerType) {
            case "CONSOLE":
                bind(MessageLogger.class).to(ConsoleMessageLogger.class);
                break;
            case "NOOP":
                bind(MessageLogger.class).to(NoopMessageLogger.class);
                break;
            default:
                Logger logger = Bukkit.getLogger();
                logger.warning("Illegal messageLogger in Chat configuration: " + messageLoggerType);
                logger.warning("Falling back to NOOP, no chat messages will be logged.");
                bind(MessageLogger.class).to(NoopMessageLogger.class);
                break;
        }
    }
}
