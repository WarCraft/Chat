package gg.warcraft.chat.api.profile.service;

import gg.warcraft.chat.api.profile.ChatProfile;

import java.util.UUID;

/**
 * This repository is injectable.
 * <p>
 * The chat profile repository serves as a point of entry into the chat module implementation. Generally you want to go
 * through the chat profile query and command services to get and update chat profiles, but if the need arises for you
 * to forgo those services you can interact directly with the repository via this interface.
 */
public interface ChatProfileRepository {

    /**
     * Returns the chat profile of the specified player.
     *
     * @param playerId The id of the player.
     * @return The chat profile of the specified player. Can be null.
     */
    ChatProfile get(UUID playerId);

    /**
     * Saves the specified chat profile to this repository.
     * <p>
     * NOTE: This repository does not do any safety checks. By explicitly foregoing the ChatProfileCommandService and
     * using this interface directly it is up to you to make sure that there are no null or empty string values set on
     * the profile.
     *
     * @param profile The chat profile to save.
     */
    void save(ChatProfile profile);
}
