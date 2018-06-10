package gg.warcraft.chat.api.channel.service;

import gg.warcraft.chat.api.channel.Channel;

import java.util.List;

/**
 * This repository is injectable.
 * <p>
 * The channel repository serves as a point of entry into the chat module implementation. Generally you want to go
 * through the ChannelCommandService to create a global or local channel, but if the need arises for you to make a fully
 * custom channel you can register it to the server via this repository.
 */
public interface ChannelRepository {

    /**
     * Returns all channels currently saved in this repository.
     *
     * @return A new list containing all channels currently saved in this repository. Never null, but can be empty.
     */
    List<Channel> getAll();

    /**
     * Returns the channel with the specified alias if found.
     * <p>
     * An alias is the name of a channel as well as all of its aliases.
     *
     * @param alias The alias of the channel.
     * @return The channel with the specified alias. Can be null.
     */
    Channel getByAlias(String alias);

    /**
     * Returns the channel with the specified shortcut if found.
     *
     * @param shortcut The shortcut of the channel.
     * @return The channel with the specified shortcut. Can be null.
     */
    Channel getByShortcut(String shortcut);

    /**
     * Saves the specified channel to this repository.
     * <p>
     * NOTE: This repository does not do any safety checks. By explicitly foregoing the ChannelCommandService and using
     * this interface directly it is up to you to make sure that any channels you save do not clash with already
     * existing channels by alias or shortcut. In addition the repository will silently ignore null or empty names,
     * aliases, and shortcuts.
     *
     * @param channel The channel to save.
     */
    void save(Channel channel);
}
