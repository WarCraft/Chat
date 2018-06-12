package gg.warcraft.chat.api.channel.service;

import gg.warcraft.chat.api.channel.Channel;

import java.util.List;

/**
 * This repository is injectable, however you generally have no need for it. Use the command and query services instead.
 * <p>
 * If you feel you absolutely have to use this repository it can be used to forgo the command service and save a {@code
 * Channel} to the Monolith domain directly. This repository does no safety checks whatsoever.
 */
public interface ChannelRepository {

    /**
     * @return A new list containing all channels saved to this repository. Never null, but can be empty.
     */
    List<Channel> getAll();

    /**
     * @param alias The name or alias of the channel. Can not be null or empty.
     * @return The channel belonging to the alias. Can be null.
     */
    Channel getByAlias(String alias);

    /**
     * @param shortcut The shortcut of the channel. Can not be null or empty.
     * @return The channel belonging to the shortcut. Can be null.
     */
    Channel getByShortcut(String shortcut);

    /**
     * @return The default channel. Can be null.
     */
    Channel getDefaultChannel();

    /**
     * @param channel The channel. Can not be null.
     */
    void setDefaultChannel(Channel channel);

    /**
     * @param channel The channel to save.
     */
    void save(Channel channel);
}
