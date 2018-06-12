package gg.warcraft.chat.app.profile.handler;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import gg.warcraft.chat.api.channel.Channel;
import gg.warcraft.chat.api.channel.service.ChannelQueryService;
import gg.warcraft.chat.api.profile.ChatProfile;
import gg.warcraft.chat.api.profile.service.ChatProfileCommandService;
import gg.warcraft.chat.api.profile.service.ChatProfileQueryService;
import gg.warcraft.monolith.api.entity.player.Player;
import gg.warcraft.monolith.api.entity.player.event.PlayerConnectEvent;
import gg.warcraft.monolith.api.entity.player.service.PlayerQueryService;

import java.util.UUID;

public class ChatProfileInitializationHandler {
    private final ChatProfileCommandService profileCommandService;
    private final ChatProfileQueryService profileQueryService;
    private final ChannelQueryService channelQueryService;
    private final PlayerQueryService playerQueryService;

    @Inject
    public ChatProfileInitializationHandler(ChatProfileCommandService profileCommandService,
                                            ChatProfileQueryService profileQueryService, ChannelQueryService channelQueryService,
                                            PlayerQueryService playerQueryService) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
        this.channelQueryService = channelQueryService;
        this.playerQueryService = playerQueryService;
    }

    @Subscribe
    public void onPlayerConnect(PlayerConnectEvent event) {
        UUID playerId = event.getPlayerId();
        ChatProfile profile = profileQueryService.getChatProfile(playerId);
        if (profile == null) {
            Player player = playerQueryService.getPlayer(playerId);
            Channel defaultChannel = channelQueryService.getDefaultChannel();
            profileCommandService.createChatProfile(playerId, player.getDisplayName(), defaultChannel);
        } else {
            Channel homeChannel = channelQueryService.getChannelByAlias(profile.getHomeChannel());
            if (homeChannel == null) {
                Channel defaultChannel = channelQueryService.getDefaultChannel();
                profileCommandService.setHomeChannel(playerId, defaultChannel);
            }
        }
    }
}
