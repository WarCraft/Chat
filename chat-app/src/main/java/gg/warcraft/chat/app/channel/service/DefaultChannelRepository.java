package gg.warcraft.chat.app.channel.service;

import com.google.inject.Singleton;
import gg.warcraft.chat.api.channel.Channel;
import gg.warcraft.chat.api.channel.service.ChannelRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class DefaultChannelRepository implements ChannelRepository {
    final Map<String, Channel> channelsByName;
    final Map<String, Channel> channelsByAlias;
    final Map<String, Channel> channelsByShortcut;

    public DefaultChannelRepository() {
        this.channelsByName = new HashMap<>();
        this.channelsByAlias = new HashMap<>();
        this.channelsByShortcut = new HashMap<>();
    }

    @Override
    public List<Channel> getAll() {
        return new ArrayList<>(channelsByName.values());
    }

    @Override
    public Channel getByAlias(String alias) {
        return channelsByAlias.get(alias);
    }

    @Override
    public Channel getByShortcut(String shortcut) {
        return channelsByShortcut.get(shortcut);
    }

    @Override
    public void save(Channel channel) {
        var name = channel.getName();
        if (name != null && !name.isEmpty()) {
            channelsByName.put(name, channel);
            channelsByAlias.put(name, channel);
        }

        channel.getAliases().forEach(alias -> {
            if (alias != null && !alias.isEmpty()) {
                channelsByAlias.put(alias, channel);
            }
        });

        var shortcut = channel.getShortcut();
        if (shortcut != null && !shortcut.isEmpty()) {
            channelsByShortcut.put(shortcut, channel);
        }
    }
}
