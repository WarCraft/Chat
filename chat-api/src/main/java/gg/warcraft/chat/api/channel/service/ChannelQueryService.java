package gg.warcraft.chat.api.channel.service;

import gg.warcraft.chat.api.channel.Channel;

/**
 * This service is injectable.
 * <p>
 * The ChannelQueryService serves as a point of entry into the chat implementation. It provides methods to query the
 * Monolith domain for a {@code Channel} by name, alias, or shortcut and to check a string for the presence of a
 * shortcut.
 */
public interface ChannelQueryService {

    /**
     * @param alias The name or alias of the channel. Can not be null or empty.
     * @return The channel belonging to the alias. Can be null.
     */
    Channel getChannelByAlias(String alias);

    /**
     * @param shortcut The shortcut of the channel. Can not be null or empty.
     * @return The channel belonging to the shortcut. Can be null.
     */
    Channel getChannelByShortcut(String shortcut);

    /**
     * @param text The text string to match the shortcut against. Can not be null or empty.
     * @return The channel with a shortcut matching the start of the text. Can be null.
     */
    Channel findChannelWithMatchingShortcut(String text);
}
