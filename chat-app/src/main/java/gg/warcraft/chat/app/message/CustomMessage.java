package gg.warcraft.chat.app.message;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import gg.warcraft.chat.api.channel.Channel;
import gg.warcraft.chat.api.message.Message;
import gg.warcraft.monolith.api.core.command.CommandSender;
import gg.warcraft.monolith.api.core.command.ConsoleCommandSender$;

public class CustomMessage implements Message {
    private final CommandSender sender;
    private final String originalText;
    private final String formattedText;

    @Inject
    public CustomMessage(@Assisted("original") String originalText,
                         @Assisted("formatted") String formattedText) {
        this.sender = ConsoleCommandSender$.MODULE$;
        this.originalText = originalText;
        this.formattedText = formattedText;
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
