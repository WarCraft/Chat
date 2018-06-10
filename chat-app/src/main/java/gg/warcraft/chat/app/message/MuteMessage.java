package gg.warcraft.chat.app.message;

import com.google.inject.Inject;
import gg.warcraft.chat.api.channel.Channel;
import gg.warcraft.chat.api.message.Message;
import gg.warcraft.monolith.api.command.CommandSender;
import gg.warcraft.monolith.api.command.Console;
import gg.warcraft.monolith.api.util.ColorCode;

public class MuteMessage implements Message {
    private static final String TEXT = "No one heard you!";

    private final CommandSender sender;
    private final String formattedText;

    @Inject
    public MuteMessage(@Console CommandSender console) {
        this.sender = console;
        this.formattedText = String.format("%s%s", ColorCode.GRAY, TEXT);
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
        return TEXT;
    }

    @Override
    public String getFormatted() {
        return formattedText;
    }
}
