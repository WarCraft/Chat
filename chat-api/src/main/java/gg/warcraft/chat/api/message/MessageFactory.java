package gg.warcraft.chat.api.message;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import gg.warcraft.chat.api.channel.Channel;
import gg.warcraft.monolith.api.core.command.CommandSender;

/**
 * This factory is injectable.
 * <p>
 * The message factory serves as a point of entry into the chat module implementation. It allows for easy creation of
 * messages which can be sent to a command sender.
 */
public interface MessageFactory {

    @Named("formatted")
    Message createMessage(Channel channel, CommandSender sender, @Assisted("original") String originalText,
                          @Assisted("formatted") String formattedText);

    @Named("mute")
    Message createMuteMessage();

    @Named("server")
    Message createServerMessage(String text);

    @Named("custom")
    Message createCustomMessage(@Assisted("original") String originalText, @Assisted("formatted") String formattedText);
}
