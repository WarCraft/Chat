package gg.warcraft.chat.api.profile.service;

import gg.warcraft.chat.api.channel.Channel;
import gg.warcraft.monolith.api.util.ColorCode;

import java.util.UUID;

/**
 * This service is injectable.
 * <p>
 * The chat profile command service serves as a point of entry into the chat module implementation. It allows you to
 * update chat profiles with additional information regarding a player in a chat context.
 */
public interface ChatProfileCommandService {

    /**
     * Creates a new chat profile for the specified player.
     *
     * @param playerId    The id of the player.
     * @param name        The Minecraft name of the player.
     * @param homeChannel A default channel.
     */
    void createChatProfile(UUID playerId, String name, Channel homeChannel);

    /**
     * Updates the name on the chat profile of the specified player.
     *
     * @param playerId The id of the player.
     * @param name     The new name.
     */
    void setName(UUID playerId, String name);

    /**
     * Updates the tag on the chat profile of the specified player.
     *
     * @param playerId The id of the player.
     * @param name     The new tag name.
     * @param color    The new tag color.
     */
    void setTag(UUID playerId, String name, ColorCode color);

    /**
     * Updates the home channel on the chat profile of the specified player.
     *
     * @param playerId The id of the player.
     * @param channel  The new home channel.
     */
    void setHomeChannel(UUID playerId, Channel channel);

    /**
     * Updates the channel list on the chat profile of the specified player to not include the specified channel.
     *
     * @param playerId The id of the player.
     * @param channel  The channel to opt out of.
     */
    void optOut(UUID playerId, Channel channel);

    /**
     * Updates the channel list on the chat profile of the specified player to include the specified channel.
     *
     * @param playerId The id of the player.
     * @param channel  The channel to opt in to.
     */
    void optIn(UUID playerId, Channel channel);
}
