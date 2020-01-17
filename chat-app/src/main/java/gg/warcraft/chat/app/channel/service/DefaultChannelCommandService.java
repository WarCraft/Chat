package gg.warcraft.chat.app.channel.service;

import com.google.inject.Inject;
import gg.warcraft.chat.api.channel.Channel;
import gg.warcraft.chat.api.channel.service.ChannelCommandService;
import gg.warcraft.chat.api.channel.service.ChannelRepository;
import gg.warcraft.chat.app.channel.GlobalChannel;
import gg.warcraft.chat.app.channel.LocalChannel;
import gg.warcraft.chat.app.channel.handler.ChannelCommandHandler;
import gg.warcraft.monolith.api.core.command.CommandHandler;
import gg.warcraft.monolith.api.core.command.CommandService;
import gg.warcraft.monolith.api.core.event.EventService;
import gg.warcraft.monolith.api.util.ColorCode;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class DefaultChannelCommandService implements ChannelCommandService {
    private static final String NAME_NULL_OR_EMPTY = "Failed to create channel with null or empty name.";
    private static final String NAME_ALREADY_EXISTS = "Failed to create channel '%s', name already exists";
    private static final String ALIAS_NULL_OR_EMPTY = "Failed to create channel '%s' with null or empty alias.";
    private static final String ALIAS_ALREADY_EXISTS = "Failed to create channel '%s', alias '%s' already exists";
    private static final String SHORTCUT_EMPTY = "Failed to create channel '%s' with empty shortcut.";
    private static final String SHORTCUT_ALREADY_EXISTS = "Failed to create channel '%s', shortcut '%s' already exists";
    private static final String CHANNEL_NOT_FOUND = "Failed to set default channel '%s', channel not found.";

    private final ChannelRepository repository;
    private final ChannelCommandHandlerFactory commandHandlerFactory;
    private final CommandService commandService;
    private final EventService eventService;

    private final ChannelCommandHandler channelCommandHandler =
            new ChannelCommandHandler(); // TODO rework

    @Inject
    public DefaultChannelCommandService(ChannelRepository repository,
                                        ChannelCommandHandlerFactory commandHandlerFactory,
                                        CommandService commandService, EventService eventService) {
        this.repository = repository;
        this.commandHandlerFactory = commandHandlerFactory;
        this.commandService = commandService;
        this.eventService = eventService;
    }

    @Override
    public void setDefaultChannel(String alias) throws IllegalArgumentException {
        Channel channel = repository.getByAlias(alias);
        if (channel == null) {
            String channelNotFound = String.format(CHANNEL_NOT_FOUND, alias);
            throw new IllegalArgumentException(channelNotFound);
        }
        repository.setDefaultChannel(channel);
    }

    private void registerChannel(Channel channel, CommandHandler commandHandler) {
        String name = channel.getName();
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(NAME_NULL_OR_EMPTY);
        } else if (repository.getByAlias(name) != null) {
            String nameAlreadyExists = String.format(NAME_ALREADY_EXISTS, name);
            throw new IllegalArgumentException(nameAlreadyExists);
        }

        List<String> aliases = channel.getAliases();
        aliases.forEach(alias -> {
            if (alias == null || alias.isEmpty()) {
                String aliasNullOrEmpty = String.format(ALIAS_NULL_OR_EMPTY, name);
                throw new IllegalArgumentException(aliasNullOrEmpty);
            } else if (repository.getByAlias(alias) != null) {
                String aliasAlreadyExists = String.format(ALIAS_ALREADY_EXISTS, name, alias);
                throw new IllegalArgumentException(aliasAlreadyExists);
            }
        });

        String shortcut = channel.getShortcut();
        if (shortcut != null) {
            if (shortcut.isEmpty()) {
                String shortcutEmpty = String.format(SHORTCUT_EMPTY, name);
                throw new IllegalArgumentException(shortcutEmpty);
            } else if (repository.getByShortcut(shortcut) != null) {
                String shortcutAlreadyExists = String.format(SHORTCUT_ALREADY_EXISTS, name, shortcut);
                throw new IllegalArgumentException(shortcutAlreadyExists);
            }
        }

        channelCommandHandler.register(name, aliases, commandHandler);
        repository.save(channel);
    }

    @Override
    public void createGlobalChannel(String name, List<String> aliases, String shortcut, ColorCode color,
                                    String formattingString, Predicate<UUID> joinCondition) {
        GlobalChannel channel = new GlobalChannel(name, aliases, shortcut, color, formattingString, joinCondition);
        CommandHandler commandHandler = commandHandlerFactory.createGlobalChannelCommandExecutor(channel);
        registerChannel(channel, commandHandler);
        eventService.subscribe(channel);
    }

    @Override
    public void createLocalChannel(String name, List<String> aliases, String shortcut, ColorCode color,
                                   String formattingString, float radius) {
        LocalChannel channel = new LocalChannel(name, aliases, shortcut, color, formattingString, radius);
        CommandHandler commandHandler = commandHandlerFactory.createLocalChannelCommandExecutor(channel);
        registerChannel(channel, commandHandler);
    }

    @Override
    public void joinChannel(Channel channel, UUID playerId) {
        // TODO add GlobalChannel and LocalChannel interfaces where global exposes the joinCondition
        // TODO test for join condition
        // TODO if true create new global channel with added playerId and save
    }

    @Override
    public void leaveChannel(Channel channel, UUID playerId) {
        // TODO see above
    }
}
