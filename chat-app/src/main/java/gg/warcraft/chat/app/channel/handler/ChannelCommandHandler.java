package gg.warcraft.chat.app.channel.handler;

import gg.warcraft.monolith.api.core.command.CommandHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChannelCommandHandler{
    private static final Map<String, CommandHandler> handlers = new HashMap<>();

    public CommandHandler getHandler(String command) {
        return handlers.get(command);
    }

    public void register(String command, List<String> aliases, CommandHandler handler) {
        handlers.put(command, handler);
        aliases.forEach(alias -> handlers.put(alias, handler));
    }
}
