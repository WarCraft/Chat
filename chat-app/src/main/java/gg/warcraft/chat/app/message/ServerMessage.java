package gg.warcrat.chat.app.message;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import gg.warcraft.monolith.api.chat.channel.Channel;
import gg.warcraft.monolith.api.chat.message.Message;
import gg.warcraft.monolith.api.command.CommandSender;
import gg.warcraft.monolith.api.command.Console;
import gg.warcraft.monolith.api.util.ColorCode;

public class ServerMessage implements Message {
    private final CommandSender sender;
    private final String originalText;
    private final String formattedText;

    @Inject
    public ServerMessage(@Console CommandSender console, @Assisted String text) {
        this.sender = console;
        this.originalText = text;
        this.formattedText = String.format("%s[SERVER] %s", ColorCode.YELLOW, text);
    }

    @Override
    public Channel getChannel() {
        return null;
    }

    @Override
    public CommandSender getSender() {
        return sender;
    }

    @Override
    public String getOriginal() {
        return originalText;
    }

    @Override
    public String getFormatted() {
        return formattedText;
    }
}
