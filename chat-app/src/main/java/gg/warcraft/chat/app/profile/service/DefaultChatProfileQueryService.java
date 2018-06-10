package gg.warcrat.chat.app.profile.service;

import com.google.inject.Inject;
import gg.warcraft.monolith.api.chat.profile.ChatProfile;
import gg.warcraft.monolith.api.chat.profile.service.ChatProfileQueryService;
import gg.warcraft.monolith.api.chat.profile.service.ChatProfileRepository;
import gg.warcraft.monolith.app.chat.profile.ConsoleChatProfile;

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
