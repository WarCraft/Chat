package gg.warcraft.chat.api.profile.service;

import gg.warcraft.chat.api.channel.Channel;
import gg.warcraft.monolith.api.util.ColorCode;

import java.util.UUID;

/**
 * This service is injectable.
 * <p>
 * The ChatProfileCommandService serves as a point of entry into the chat module implementation. It provides methods to
 * create a new {@code ChatProfile} or update the values on an existing one.
 */
public interface ChatProfileCommandService {

    /**
     * @param playerId    The id of the player. Can not be null.
     * @param name        The Minecraft name of the player. Can not be null or empty.
     * @param homeChannel A default channel. Can not be null.
     */
    void createChatProfile(UUID playerId, String name, Channel homeChannel);

    /**
     * @param playerId The id of the player. Can not be null.
     * @param name     The new chat name of the player. Can not be null or empty.
     */
    void setName(UUID playerId, String name);

    /**
     * @param playerId The id of the player. Can not be null.
     * @param name     The new tag name. Can not be null or empty.
     * @param color    The new tag color. Can not be null.
     */
    void setTag(UUID playerId, String name, ColorCode color);

    /**
     * @param playerId The id of the player. Can not be null.
     * @param channel  The new home channel. Can not be null.
     */
    void setHomeChannel(UUID playerId, Channel channel);

    /**
     * Updates the channel list on the chat profile of the specified player to not include the specified channel.
     * <p>
     * Trying to opt out of a channel you have already left will fail silently.
     *
     * @param playerId The id of the player. Can not be null.
     * @param channel  The channel to opt out of. Can not be null.
     */
    void optOut(UUID playerId, Channel channel);

    /**
     * Updates the channel list on the chat profile of the specified player to include the specified channel.
     * <p>
     * Trying to opt in to a channel you have already join will fail silently.
     *
     * @param playerId The id of the player. Can not be null.
     * @param channel  The channel to opt in to. Can not be null.
     */
    void optIn(UUID playerId, Channel channel);
}
