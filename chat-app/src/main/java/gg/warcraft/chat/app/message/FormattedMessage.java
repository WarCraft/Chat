package gg.warcrat.chat.app.message;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import gg.warcraft.monolith.api.chat.channel.Channel;
import gg.warcraft.monolith.api.chat.message.Message;
import gg.warcraft.monolith.api.command.CommandSender;

public class FormattedMessage implements Message {
    private final Channel channel;
    private final CommandSender sender;
    private final String originalText;
    private final String formattedText;

    @Inject
    public FormattedMessage(@Assisted Channel channel, @Assisted CommandSender sender,
                            @Assisted("original") String originalText, @Assisted("formatted") String formattedText) {
        this.channel = channel;
        this.sender = sender;
        this.originalText = originalText;
        this.formattedText = formattedText;
    }

    @Override
    public Channel getChannel() {
        return channel;
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
