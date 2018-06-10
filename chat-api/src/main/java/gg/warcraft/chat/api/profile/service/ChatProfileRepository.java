package gg.warcraft.chat.api.profile.service;

import gg.warcraft.chat.api.profile.ChatProfile;

import java.util.UUID;

/**
 * This repository is injectable, however you generally have no need for it. Use the command and query services instead.
 * <p>
 * If you feel you absolutely have to use this repository it can be used to forgo the command service and save a {@code
 * ChatProfile} to the Monolith domain directly. This repository does no safety checks whatsoever.
 */
public interface ChatProfileRepository {

    /**
     * @param playerId The id of the player. Can not be null.
     * @return The chat profile of the player. Can be null.
     */
    ChatProfile get(UUID playerId);

    /**
     * @param profile The chat profile to save. Can not be null.
     */
    void save(ChatProfile profile);
}
