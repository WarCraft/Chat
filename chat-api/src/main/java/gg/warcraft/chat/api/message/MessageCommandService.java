package gg.warcraft.chat.api.message;

import java.util.UUID;

/**
 * This service is injectable.
 * <p>
 * The MessageCommandService serves as a point of entry into the chat module implementation. It provides methods to
 * send a {@code Message} to a specific player, all players, all members of staff as identified per {@code
 * AuthorizationService#isStaff}, and the console.
 */
public interface MessageCommandService {

    /**
     * @param message  The message to send. Can not be null.
     * @param playerId The id of the player. Can not be null.
     */
    void sendMessageToPlayer(Message message, UUID playerId);

    /**
     * @param message The message to send. Can not be null.
     */
    void sendMessageToAllPlayers(Message message);

    /**
     * @param message The message to send. Can not be null.
     */
    void sendMessageToAllStaff(Message message);

    /**
     * @param message The message to send. Can not be null.
     */
    void sendMessageToConsole(Message message);
}
