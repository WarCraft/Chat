package gg.warcraft.chat.api.channel.service;

import gg.warcraft.chat.api.channel.Channel;
import gg.warcraft.monolith.api.util.ColorCode;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * This service is injectable.
 * <p>
 * The ChannelCommandService serves as a point of entry into the chat module implementation. It provides methods to
 * create a new {@code Channel} and to join or leave one.
 * <p>
 * When creating new channels formatting string options include:
 * {channel.name}   The name of the channel
 * {channel.color}  The color code of the channel
 * {sender.name}    The name of the sender
 * {sender.tag}     The tag of the sender
 * {sender.color}   The color code of the sender's tag
 * {text}           The chat text
 * <p>
 * A message will always start with the color of the {@code Channel}. Examples:
 * string: "[{channel.name}] {sender.color}[{sender.tag}]{channel.color} {sender.name}: {text}"
 * output: GRAY[Global] RED[Admin]GRAY fishb6nes: testerino messagerino
 * <p>
 * string: "{sender.name} {text}"
 * output: GOLDfishb6nes flails his arms
 */
public interface ChannelCommandService {

    /**
     * @param name             The name of the channel.
     * @param aliases          An optional aliases of the channel. Can not be null, but can be empty. Items can not be
     *                         null or empty.
     * @param shortcut         The optional shortcut of the channel.
     * @param color            The color code of the channel.
     * @param formattingString The formatting string of the channel.
     * @param joinCondition    The join condition of the channel.
     * @throws IllegalArgumentException Thrown when any of the parameter constraints are not met or if the name, any of
     *                                  the aliases, or the shortcut is already in use by another channel.
     */
    void createGlobalChannel(String name, List<String> aliases, String shortcut, ColorCode color,
                             String formattingString, Predicate<UUID> joinCondition) throws IllegalArgumentException;

    /**
     * @param name             The name of the channel.
     * @param aliases          The optional aliases of the channel.
     * @param shortcut         The optional shortcut of the channel.
     * @param color            The color code of the channel.
     * @param formattingString The formatting string of the channel.
     * @param radius           The radius within messages are sent.
     * @throws IllegalArgumentException Thrown when any of the parameter constraints are not met or if the name, any of
     *                                  the aliases, or the shortcut is already in use by another channel.
     */
    void createLocalChannel(String name, List<String> aliases, String shortcut, ColorCode color,
                            String formattingString, float radius) throws IllegalArgumentException;

    /**
     * Attempts to add the specified player to the given channel.
     * <p>
     * This can fail if the player does not have the right permissions. A message will be sent to the player.
     *
     * @param channel  The channel to join.
     * @param playerId The id of the player.
     */
    void joinChannel(Channel channel, UUID playerId);

    /**
     * Attempts to remove the specified player from the given channel.
     * <p>
     * Does nothing if the player was not already in the channel.
     *
     * @param channel  The channel to leave.
     * @param playerId The id of the player.
     */
    void leaveChannel(Channel channel, UUID playerId);
}
