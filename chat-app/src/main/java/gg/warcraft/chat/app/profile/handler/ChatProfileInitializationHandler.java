package gg.warcraft.chat.app.profile.handler;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import gg.warcraft.chat.api.channel.Channel;
import gg.warcraft.chat.api.channel.service.ChannelQueryService;
import gg.warcraft.chat.api.profile.service.ChatProfileCommandService;
import gg.warcraft.chat.api.profile.service.ChatProfileQueryService;
import gg.warcraft.monolith.api.entity.player.event.PlayerConnectEvent;
import gg.warcraft.monolith.api.entity.player.service.PlayerQueryService;

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
        var playerId = event.getPlayerId();
        var profile = profileQueryService.getChatProfile(playerId);
        if (profile == null) {
            var player = playerQueryService.getPlayer(playerId);
            profileCommandService.createChatProfile(playerId, player.getDisplayName(), defaultChannel);
        } else {
            var homeChannel = channelQueryService.getChannelByAlias(profile.getHomeChannel());
            if (homeChannel == null) {
                profileCommandService.setHomeChannel(playerId, defaultChannel);
            }
        }
    }
}
