package gg.warcraft.chat.api.message;

import gg.warcraft.chat.api.channel.Channel;
import gg.warcraft.monolith.api.command.CommandSender;

/**
 * A message represents activity in a channel.
 */
public interface Message {

    /**
     * Returns the channel this message was sent to.
     *
     * @return The channel this message was sent to or null if sent straight to a command sender.
     */
    Channel getChannel();

    /**
     * Returns the sender of this message.
     *
     * @return The sender of this message or null if sent by the console.
     */
    CommandSender getSender();

    /**
     * Returns the original plain text of this message.
     *
     * @return The original plain text of this message. Never null or empty.
     */
    String getOriginal();

    /**
     * Returns the plain text formatted according to the implementation.
     *
     * @return The plain text formatted according to the implementation. Never null or empty.
     */
    String getFormatted();
}
