package gg.warcrat.chat.app.profile.handler;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import gg.warcraft.monolith.api.chat.channel.Channel;
import gg.warcraft.monolith.api.chat.channel.ChannelQueryService;
import gg.warcraft.monolith.api.chat.profile.ChatProfile;
import gg.warcraft.monolith.api.chat.profile.service.ChatProfileCommandService;
import gg.warcraft.monolith.api.chat.profile.service.ChatProfileQueryService;
import gg.warcraft.monolith.api.entity.player.Player;
import gg.warcraft.monolith.api.entity.player.event.PlayerConnectEvent;
import gg.warcraft.monolith.api.entity.player.service.PlayerQueryService;

import java.util.UUID;

public class ChatProfileInitializationHandler {
    private final ChatProfileCommandService profileCommandService;
    private final ChatProfileQueryService profileQueryService;
    private final ChannelQueryService channelQueryService;
    private final PlayerQueryService playerQueryService;

    private Channel defaultChannel;

    @Inject
    public ChatProfileInitializationHandler(ChatProfileCommandService profileCommandService,
                                            ChatProfileQueryService profileQueryService, ChannelQueryService channelQueryService,
                                            PlayerQueryService playerQueryService) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
        this.channelQueryService = channelQueryService;
        this.playerQueryService = playerQueryService;
    }

    public void setDefaultChannel(Channel defaultChannel) {
        this.defaultChannel = defaultChannel;
    }

    @Subscribe
    public void onPlayerConnect(PlayerConnectEvent event) {
        UUID playerId = event.getPlayerId();
        ChatProfile profile = profileQueryService.getChatProfile(playerId);
        if (profile == null) {
            Player player = playerQueryService.getPlayer(playerId);
            profileCommandService.createChatProfile(playerId, player.getDisplayName(), defaultChannel);
        } else {
            Channel homeChannel = channelQueryService.getChannelByAlias(profile.getHomeChannel());
            if (homeChannel == null) {
                profileCommandService.setHomeChannel(playerId, defaultChannel);
            }
        }
    }
}
