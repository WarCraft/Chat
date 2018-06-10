package gg.warcraft.chat.api.channel.service;

import gg.warcraft.chat.api.channel.Channel;

/**
 * This service is injectable.
 * <p>
 * The channel query service serves as a point of entry into the chat module implementation. It allows you to query
 * registered channels by name or shortcut.
 */
public interface ChannelQueryService {

    /**
     * @param alias The name or alias of the channel. Can not be null or empty.
     * @return The channel belonging to the alias. Can be null.
     */
    Channel getChannelByAlias(String alias);

    /**
     * @param shortcut The shortcut of the channel.
     * @return The channel with the specified shortcut. Can be null.
     */
    Channel getChannelByShortcut(String shortcut);

    /**
     * Attempts to find a channel which shortcut matches the start of the text string.
     *
     * @param text The text string to match the shortcut against.
     * @return The channel with the matching shortcut. Can be null.
     */
    Channel findChannelWithMatchingShortcut(String text);
}
