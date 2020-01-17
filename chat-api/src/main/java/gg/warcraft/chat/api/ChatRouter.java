package gg.warcraft.chat.api;

import gg.warcraft.monolith.api.core.event.EventHandler;

import java.util.UUID;

/**
 * This interface is injectable.
 * <p>
 * The ChatRouter serves as a point of entry into the chat module implementation. It allows you to register priority
 * listeners which can be used to intercept input from players.
 */
public interface ChatRouter extends EventHandler {

    /**
     * @param playerId The id of the player to listen to.
     * @param listener The priority listener to register.
     */
    void registerPriorityListener(UUID playerId, PriorityChatListener listener);
}
