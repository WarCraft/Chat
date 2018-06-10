package gg.warcraft.chat.app.message.service;

import com.google.inject.Inject;
import gg.warcraft.chat.api.message.Message;
import gg.warcraft.chat.api.message.MessageCommandService;
import gg.warcraft.chat.api.service.ChatServerAdapter;
import gg.warcraft.monolith.api.core.AuthorizationService;

import java.util.UUID;

public class DefaultMessageCommandService implements MessageCommandService {
    private final ChatServerAdapter adapter;
    private final AuthorizationService authorizationService;

    @Inject
    public DefaultMessageCommandService(ChatServerAdapter adapter, AuthorizationService authorizationService) {
        this.adapter = adapter;
        this.authorizationService = authorizationService;
    }

    @Override
    public void sendMessageToPlayer(Message message, UUID playerId) {
        adapter.sendMessageToPlayer(message.getFormatted(), playerId);
    }

    @Override
    public void sendMessageToAllPlayers(Message message) {
        adapter.sendMessageToAllPlayersMatching(message.getFormatted(), playerId -> true);
    }

    @Override
    public void sendMessageToAllStaff(Message message) {
        adapter.sendMessageToAllPlayersMatching(message.getFormatted(), authorizationService::isStaff);
    }

    @Override
    public void sendMessageToConsole(Message message) {
        System.out.println(message.getOriginal());
    }
}
