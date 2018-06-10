package gg.warcraft.chat.app.profile.service;

import com.google.inject.Inject;
import gg.warcraft.chat.api.channel.Channel;
import gg.warcraft.chat.api.profile.service.ChatProfileCommandService;
import gg.warcraft.chat.api.profile.service.ChatProfileRepository;
import gg.warcraft.chat.app.event.SimplePlayerHomeChannelChangedEvent;
import gg.warcraft.chat.app.profile.PlayerChatProfile;
import gg.warcraft.chat.app.profile.PlayerChatTag;
import gg.warcraft.monolith.api.core.EventService;
import gg.warcraft.monolith.api.util.ColorCode;

import java.util.Collections;
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
        var newTag = new PlayerChatTag("Wayfarer", ColorCode.WHITE);
        var homeChannelName = homeChannel.getName();
        var newProfile = new PlayerChatProfile(playerId, name, newTag, homeChannelName, Collections.emptySet());
        repository.save(newProfile);
    }

    @Override
    public void setName(UUID playerId, String name) {
        var profile = repository.get(playerId);
        var newProfile = new PlayerChatProfile(playerId, name, profile.getTag(), profile.getHomeChannel(),
                profile.getOptedOut());
        repository.save(newProfile);
    }

    @Override
    public void setTag(UUID playerId, String name, ColorCode color) {
        var profile = repository.get(playerId);
        var newTag = new PlayerChatTag(name, color);
        var newProfile = new PlayerChatProfile(playerId, profile.getName(), newTag, profile.getHomeChannel(),
                profile.getOptedOut());
        repository.save(newProfile);
    }

    @Override
    public void setHomeChannel(UUID playerId, Channel channel) {
        var profile = repository.get(playerId);
        var newProfile = new PlayerChatProfile(playerId, profile.getName(), profile.getTag(), channel.getName(),
                profile.getOptedOut());
        repository.save(newProfile);

        var event = new SimplePlayerHomeChannelChangedEvent(playerId, channel);
        eventService.publish(event);
    }

    @Override
    public void optOut(UUID playerId, Channel channel) {
        var profile = repository.get(playerId);
        var newOptedOut = profile.getOptedOut();
        if (newOptedOut.add(channel.getName())) {
            var newProfile = new PlayerChatProfile(playerId, profile.getName(), profile.getTag(),
                    profile.getHomeChannel(), newOptedOut);
            repository.save(newProfile);
        }
    }

    @Override
    public void optIn(UUID playerId, Channel channel) {
        var profile = repository.get(playerId);
        var newOptedOut = profile.getOptedOut();
        if (newOptedOut.remove(channel.getName())) {
            var newProfile = new PlayerChatProfile(playerId, profile.getName(), profile.getTag(),
                    profile.getHomeChannel(), newOptedOut);
            repository.save(newProfile);
        }
    }
}
