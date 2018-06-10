package gg.warcraft.chat.api.service;

import java.util.UUID;
import java.util.function.Predicate;

public interface ChatServerAdapter {

    void sendMessageToPlayer(String message, UUID playerId);

    void sendMessageToAllPlayersMatching(String message, Predicate<UUID> sendCondition);
}
