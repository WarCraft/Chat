package gg.warcraft.chat.api;

import java.util.UUID;

/**
 * Interface for objects that require Player input. Whenever a PriorityChatListener is registered for a given Player via
 * ChatService#registerChatListener the next message that Player sends will be cancelled and instead send to the
 * listener.
 * <p>
 * PriorityChatListeners will be automatically unregistered after having received a message. If you require more input
 * you should register the PriorityChatListener again.
 * <p>
 * Registering a PriorityChatListener will unregister any previously registered PriorityChatListener, if present, that
 * has yet to receive input from the Player in question.
 */
public interface PriorityChatListener {

    /**
     * Callback which is executed when the player with the given player id has sent a chat message.
     *
     * @param playerId The id of the player that sent this chat message.
     * @param text     The plain text of the chat message.
     */
    void onChat(UUID playerId, String text);
}
