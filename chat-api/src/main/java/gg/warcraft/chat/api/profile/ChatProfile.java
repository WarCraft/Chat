package gg.warcraft.chat.api.profile;

import gg.warcraft.chat.api.channel.Channel;

import java.util.Set;
import java.util.UUID;

/**
 * A chat profile holds additional information about a player in a chat context.
 */
public interface ChatProfile {

    /**
     * Returns the id of the player.
     *
     * @return The id of the player. Never null.
     */
    UUID getPlayerId();

    /**
     * Returns the name of the player in a chat context.
     *
     * @return The name of the player in a chat context. Never null or empty.
     */
    String getName();

    /**
     * Returns the chat tag of the player.
     *
     * @return The chat tag of the player. Never null.
     */
    ChatTag getTag();

    /**
     * Returns the home channel of the player.
     *
     * @return The home channel of the player. Never null or empty.
     */
    String getHomeChannel();

    /**
     * Returns all the names of the channels the player has specifically opted out of.
     *
     * @return All the names of the channels the player has specifically opted out of. Never null, but can be empty.
     */
    Set<String> getOptedOut();

    /**
     * Returns whether the player has opted out of the specified channel.
     * <p>
     * This is a convenience method with results identical to
     * getOptedOut().contains(channel.getName())
     *
     * @param channel The channel to check.
     * @return True if the player has opted out of the specified channel, false otherwise.
     */
    boolean hasOptedOut(Channel channel);
}
