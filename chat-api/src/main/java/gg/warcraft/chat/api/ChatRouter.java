package gg.warcraft.chat.api;

import java.util.UUID;

/**
 * This interface is injectable.
 * <p>
 * The chat router serves as a point of entry into the chat module implementation. It allows you to register priority
 * listeners which can be used to intercept input from players.
 */
public interface ChatRouter {

    /**
     * Registers a new priority listener for the specified player id.
     *
     * @param playerId The id of the player.
     * @param listener The priority listener.
     */
    void registerPriorityListener(UUID playerId, PriorityChatListener listener);
}
