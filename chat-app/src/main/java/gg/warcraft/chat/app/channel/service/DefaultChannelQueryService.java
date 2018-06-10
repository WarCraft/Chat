package gg.warcrat.chat.app.channel.service;

import com.google.inject.Inject;
import gg.warcraft.monolith.api.chat.channel.Channel;
import gg.warcraft.monolith.api.chat.channel.ChannelQueryService;
import gg.warcraft.monolith.api.chat.channel.ChannelRepository;

public class DefaultChannelQueryService implements ChannelQueryService {
    private final ChannelRepository repository;

    @Inject
    public DefaultChannelQueryService(ChannelRepository repository) {
        this.repository = repository;
    }

    @Override
    public Channel getChannelByAlias(String alias) {
        return repository.getByAlias(alias);
    }

    @Override
    public Channel getChannelByShortcut(String shortcut) {
        return repository.getByShortcut(shortcut);
    }

    @Override
    public Channel findChannelWithMatchingShortcut(String text) {
        return repository.getAll().stream()
                .filter(channel -> channel.getShortcut() != null)
                .filter(channel -> text.startsWith(channel.getShortcut()))
                .findFirst()
                .orElse(null);
    }
}
