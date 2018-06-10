package gg.warcraft.chat.api.profile.service;

import gg.warcraft.chat.api.profile.ChatProfile;

import java.util.UUID;

/**
 * This service is injectable.
 * <p>
 * The chat profile query service serves as a point of entry into the chat module implementation. It allows you to
 * query chat profiles which contain additional information regarding a player in a chat context.
 */
public interface ChatProfileQueryService {

    /**
     * Returns the chat profile of the specified player.
     * <p>
     * This data is only present if the player has logged in to the server at least once.
     *
     * @param playerId The id of the player.
     * @return The chat profile of the specified player. Can be null.
     */
    ChatProfile getChatProfile(UUID playerId);

    /**
     * Returns the chat profile of the console.
     *
     * @return The chat profile of the console. Never null.
     */
    ChatProfile getConsoleChatProfile();
}
