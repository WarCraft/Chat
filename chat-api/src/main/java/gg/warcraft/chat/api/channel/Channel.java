package gg.warcraft.chat.api.channel;

import gg.warcraft.monolith.api.util.ColorCode;

import java.util.List;

/**
 * A Channel allows players to communicate with each other. It keeps tabs on who can talk in the channel as well as who
 * should receive any messages sent to it. The {@code Channel} name and all optional aliases can be used as commands to
 * to talk in a {@code Channel}. A shortcut is a sequence of characters to be matched with the start of a message's
 * text. If matched the message will go straight to this channel without the need for the use of one of its commands.
 */
public interface Channel {

    /**
     * @return The name of this channel. Never null or empty.
     */
    String getName();

    /**
     * @return The set of aliases of this channel. Never null, but can be empty. Items are never null or empty.
     */
    List<String> getAliases();

    /**
     * @return The shortcut of this channel. Can be null, but never empty.
     */
    String getShortcut();

    /**
     * @return The default color code of this channel. Never null.
     */
    ColorCode getColor();

    /**
     * @return The formatting string of this channel. Never null or empty.
     */
    String getFormattingString();
}
