package gg.warcraft.chat.app;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import gg.warcraft.chat.api.ChatRouter;
import gg.warcraft.chat.api.channel.service.ChannelCommandService;
import gg.warcraft.chat.api.channel.service.ChannelQueryService;
import gg.warcraft.chat.api.channel.service.ChannelRepository;
import gg.warcraft.chat.api.message.Message;
import gg.warcraft.chat.api.message.MessageCommandService;
import gg.warcraft.chat.api.message.MessageFactory;
import gg.warcraft.chat.api.profile.service.ChatProfileCommandService;
import gg.warcraft.chat.api.profile.service.ChatProfileQueryService;
import gg.warcraft.chat.api.profile.service.ChatProfileRepository;
import gg.warcraft.chat.app.channel.handler.GlobalChannelCommandHandler;
import gg.warcraft.chat.app.channel.handler.LocalChannelCommandHandler;
import gg.warcraft.chat.app.channel.service.ChannelCommandHandlerFactory;
import gg.warcraft.chat.app.channel.service.DefaultChannelCommandService;
import gg.warcraft.chat.app.channel.service.DefaultChannelQueryService;
import gg.warcraft.chat.app.channel.service.DefaultChannelRepository;
import gg.warcraft.chat.app.logger.ConsoleMessageLogger;
import gg.warcraft.chat.app.logger.MessageLogger;
import gg.warcraft.chat.app.logger.NoopMessageLogger;
import gg.warcraft.chat.app.message.CustomMessage;
import gg.warcraft.chat.app.message.FormattedMessage;
import gg.warcraft.chat.app.message.MuteMessage;
import gg.warcraft.chat.app.message.ServerMessage;
import gg.warcraft.chat.app.message.service.DefaultMessageCommandService;
import gg.warcraft.chat.app.profile.service.DefaultChatProfileCommandService;
import gg.warcraft.chat.app.profile.service.DefaultChatProfileQueryService;
import gg.warcraft.chat.app.profile.service.DefaultChatProfileRepository;
import gg.warcraft.monolith.api.command.CommandHandler;

public abstract class AbstractChatModule extends AbstractModule {
    private final String messageLoggerType;

    public AbstractChatModule(String messageLoggerType) {
        this.messageLoggerType = messageLoggerType;
    }

    void configureMessageLogger() {
        switch (messageLoggerType) {
            case "CONSOLE":
                bind(MessageLogger.class).to(ConsoleMessageLogger.class);
                break;
            case "NOOP":
                bind(MessageLogger.class).to(NoopMessageLogger.class);
                break;
            default:
                throw new IllegalArgumentException("Failed to configure message logger of illegal type: " + messageLoggerType);
        }
    }

    @Override
    protected void configure() {
        bind(ChatRouter.class).to(DefaultChatRouter.class);

        bind(ChannelCommandService.class).to(DefaultChannelCommandService.class);
        bind(ChannelQueryService.class).to(DefaultChannelQueryService.class);
        bind(ChannelRepository.class).to(DefaultChannelRepository.class);

        bind(ChatProfileCommandService.class).to(DefaultChatProfileCommandService.class);
        bind(ChatProfileQueryService.class).to(DefaultChatProfileQueryService.class);
        bind(ChatProfileRepository.class).to(DefaultChatProfileRepository.class);

        bind(MessageCommandService.class).to(DefaultMessageCommandService.class);

        install(new FactoryModuleBuilder()
                .implement(Message.class, Names.named("formatted"), FormattedMessage.class)
                .implement(Message.class, Names.named("mute"), MuteMessage.class)
                .implement(Message.class, Names.named("server"), ServerMessage.class)
                .implement(Message.class, Names.named("custom"), CustomMessage.class)
                .build(MessageFactory.class));

        install(new FactoryModuleBuilder()
                .implement(CommandHandler.class, Names.named("local"), LocalChannelCommandHandler.class)
                .implement(CommandHandler.class, Names.named("global"), GlobalChannelCommandHandler.class)
                .build(ChannelCommandHandlerFactory.class));

        configureMessageLogger();
    }
}
