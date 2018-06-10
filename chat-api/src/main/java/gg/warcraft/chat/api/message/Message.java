package gg.warcraft.chat.api.message;

import gg.warcraft.chat.api.channel.Channel;
import gg.warcraft.monolith.api.command.CommandSender;

/**
 * A message represents activity in a channel.
 * <p>
 * TODO: clean up and expand upon javadoc
 */
public interface Message {

    /**
     * @return The channel this message was sent to. Can be null.
     */
    Channel getChannel();

    /**
     * @return The sender of this message. Never null.
     */
    CommandSender getSender();

    /**
     * @return The original plain text of this message. Never null or empty.
     */
    String getOriginal();

    /**
     * @return The formatted text as per the channel this message was sent to. Never null or empty.
     */
    String getFormatted();
}
