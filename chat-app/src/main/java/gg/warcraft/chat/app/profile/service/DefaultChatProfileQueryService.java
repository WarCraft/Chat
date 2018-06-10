package gg.warcraft.chat.app.profile.service;

import com.google.inject.Inject;
import gg.warcraft.chat.api.profile.ChatProfile;
import gg.warcraft.chat.api.profile.service.ChatProfileQueryService;
import gg.warcraft.chat.api.profile.service.ChatProfileRepository;
import gg.warcraft.chat.app.profile.ConsoleChatProfile;

import java.util.UUID;

public class DefaultChatProfileQueryService implements ChatProfileQueryService {
    private static final ChatProfile CONSOLE_PROFILE = new ConsoleChatProfile();

    private final ChatProfileRepository repository;

    @Inject
    public DefaultChatProfileQueryService(ChatProfileRepository repository) {
        this.repository = repository;
    }

    @Override
    public ChatProfile getChatProfile(UUID playerId) {
        return repository.get(playerId);
    }

    @Override
    public ChatProfile getConsoleChatProfile() {
        return CONSOLE_PROFILE;
    }
}
