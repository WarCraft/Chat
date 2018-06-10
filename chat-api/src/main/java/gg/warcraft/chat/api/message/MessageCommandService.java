package gg.warcraft.chat.api.message;

import java.util.UUID;

/**
 * This service is injectable.
 * <p>
 * The message command service serves as a point of entry into the chat module implementation. It allows you to send
 * messages to a messageable.
 */
public interface MessageCommandService {

    void sendMessageToPlayer(Message message, UUID playerId);

    /**
     * Sends the specified message to all online players.
     *
     * @param message The message to send.
     */
    void sendMessageToAllPlayers(Message message);

    /**
     * Sends the specified message to all online members of staff.
     *
     * @param message The message to send.
     */
    void sendMessageToAllStaff(Message message);

    void sendMessageToConsole(Message message);
}
