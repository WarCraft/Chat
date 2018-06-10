package gg.warcraft.chat.api.service;

import java.util.UUID;
import java.util.function.Predicate;

/**
 * This adapter is injectable, however you generally have no need for it. Use the command and query services instead.
 * <p>
 * The ChatServerAdapter abstracts chat related server implementations from the Monolith domain. It can be used to send
 * raw text messages to players directly.
 */
public interface ChatServerAdapter {

    /**
     * @param message  The message to send. Can not be null or empty.
     * @param playerId The id of the player. Can not be null.
     */
    void sendMessageToPlayer(String message, UUID playerId);

    /**
     * @param message       The message to send. Can not be null or empty.
     * @param sendCondition The predicate players must match to receive the message. Can not be null.
     */
    void sendMessageToAllPlayersMatching(String message, Predicate<UUID> sendCondition);
}
