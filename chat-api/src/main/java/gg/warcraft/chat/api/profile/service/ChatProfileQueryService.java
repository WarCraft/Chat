package gg.warcraft.chat.api.profile.service;

import gg.warcraft.chat.api.profile.ChatProfile;

import java.util.UUID;

/**
 * This service is injectable.
 * <p>
 * The ChatProfileQueryService serves as a point of entry into the chat implementation. It provides methods to query the
 * Monolith domain for a {@code ChatProfile} by player id or the console profile.
 */
public interface ChatProfileQueryService {

    /**
     * Returns the chat profile of the player.
     * <p>
     * This data is only present if the player has logged on to the server at least once.
     *
     * @param playerId The id of the player.
     * @return The chat profile of the player. Can be null.
     */
    ChatProfile getChatProfile(UUID playerId);

    /**
     * @return The chat profile of the console. Never null.
     */
    ChatProfile getConsoleChatProfile();
}
