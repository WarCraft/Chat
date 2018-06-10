package gg.warcrat.chat.app.profile.service;

import com.google.inject.Inject;
import gg.warcraft.monolith.api.chat.channel.Channel;
import gg.warcraft.monolith.api.chat.event.PlayerHomeChannelChangedEvent;
import gg.warcraft.monolith.api.chat.profile.ChatProfile;
import gg.warcraft.monolith.api.chat.profile.ChatTag;
import gg.warcraft.monolith.api.chat.profile.service.ChatProfileCommandService;
import gg.warcraft.monolith.api.chat.profile.service.ChatProfileRepository;
import gg.warcraft.monolith.api.event.EventService;
import gg.warcraft.monolith.api.util.ColorCode;
import gg.warcraft.monolith.app.chat.event.SimplePlayerHomeChannelChangedEvent;
import gg.warcraft.monolith.app.chat.profile.PlayerChatProfile;
import gg.warcraft.monolith.app.chat.profile.PlayerChatTag;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class DefaultChatProfileCommandService implements ChatProfileCommandService {
    private final ChatProfileRepository repository;
    private final EventService eventService;

    @Inject
    public DefaultChatProfileCommandService(ChatProfileRepository repository, EventService eventService) {
        this.repository = repository;
        this.eventService = eventService;
    }

    @Override
    public void createChatProfile(UUID playerId, String name, Channel homeChannel) {
        ChatTag newTag = new PlayerChatTag("Wayfarer", ColorCode.WHITE);
        String homeChannelName = homeChannel.getName();
        ChatProfile newProfile = new PlayerChatProfile(playerId, name, newTag, homeChannelName, Collections.emptySet());
        repository.save(newProfile);
    }

    @Override
    public void setName(UUID playerId, String name) {
        ChatProfile profile = repository.get(playerId);
        ChatProfile newProfile = new PlayerChatProfile(playerId, name, profile.getTag(), profile.getHomeChannel(),
                profile.getOptedOut());
        repository.save(newProfile);
    }

    @Override
    public void setTag(UUID playerId, String name, ColorCode color) {
        ChatProfile profile = repository.get(playerId);
        ChatTag newTag = new PlayerChatTag(name, color);
        ChatProfile newProfile = new PlayerChatProfile(playerId, profile.getName(), newTag, profile.getHomeChannel(),
                profile.getOptedOut());
        repository.save(newProfile);
    }

    @Override
    public void setHomeChannel(UUID playerId, Channel channel) {
        ChatProfile profile = repository.get(playerId);
        ChatProfile newProfile = new PlayerChatProfile(playerId, profile.getName(), profile.getTag(), channel.getName(),
                profile.getOptedOut());
        repository.save(newProfile);

        PlayerHomeChannelChangedEvent event = new SimplePlayerHomeChannelChangedEvent(playerId, channel);
        eventService.publish(event);
    }

    @Override
    public void optOut(UUID playerId, Channel channel) {
        ChatProfile profile = repository.get(playerId);
        Set<String> newOptedOut = profile.getOptedOut();
        if (newOptedOut.add(channel.getName())) {
            ChatProfile newProfile = new PlayerChatProfile(playerId, profile.getName(), profile.getTag(),
                    profile.getHomeChannel(), newOptedOut);
            repository.save(newProfile);
        }
    }

    @Override
    public void optIn(UUID playerId, Channel channel) {
        ChatProfile profile = repository.get(playerId);
        Set<String> newOptedOut = profile.getOptedOut();
        if (newOptedOut.remove(channel.getName())) {
            ChatProfile newProfile = new PlayerChatProfile(playerId, profile.getName(), profile.getTag(),
                    profile.getHomeChannel(), newOptedOut);
            repository.save(newProfile);
        }
    }
}
