package gg.warcraft.chat.api.channel;

import gg.warcraft.monolith.api.util.ColorCode;

import java.util.List;

/**
 * A channel allows players to communicate with each other. It keeps tabs on who can talk in the channel as well as who
 * should receive the messages.
 */
public interface Channel {

    /**
     * Returns the name of this channel that will be shown in-game.
     * <p>
     * This name is the primary command that can be used to talk in this channel.
     *
     * @return The name of this channel. Never null or empty.
     */
    String getName();

    /**
     * Returns the set of aliases of this channel, or an empty set.
     * <p>
     * All aliases can be used as secondary commands to talk in this channel.
     *
     * @return The set of aliases of this channel. Never null, but can be empty. Items are never null or empty.
     */
    List<String> getAliases();

    /**
     * Returns the optional shortcut of this channel.
     * <p>
     * A shortcut is a sequence of characters to be matched with the start of a message's text. If matched the message
     * will go straight to this channel without the need for the use of one of its commands.
     *
     * @return The shortcut of this channel. Can be null, but never empty.
     */
    String getShortcut();

    /**
     * Returns the color code of this channel.
     * <p>
     * All text in the Minecraft chatbox has a color. This will be the default color of this channel.
     *
     * @return The color code of this channel. Never null.
     */
    ColorCode getColor();

    /**
     * Returns the formatting string of this channel.
     *
     * @return The formatting string of this channel. Never null or empty.
     */
    String getFormattingString();
}
