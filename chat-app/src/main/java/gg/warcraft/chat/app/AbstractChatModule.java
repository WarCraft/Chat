package gg.warcraft.chat.app;

import com.google.inject.PrivateModule;
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
import gg.warcraft.chat.app.logger.PluginMessageLogger;
import gg.warcraft.chat.app.message.CustomMessage;
import gg.warcraft.chat.app.message.FormattedMessage;
import gg.warcraft.chat.app.message.MuteMessage;
import gg.warcraft.chat.app.message.ServerMessage;
import gg.warcraft.chat.app.message.service.DefaultMessageCommandService;
import gg.warcraft.chat.app.profile.service.DefaultChatProfileCommandService;
import gg.warcraft.chat.app.profile.service.DefaultChatProfileQueryService;
import gg.warcraft.chat.app.profile.service.DefaultChatProfileRepository;
import gg.warcraft.monolith.api.command.CommandHandler;

public abstract class AbstractChatModule extends PrivateModule {
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
            case "PLUGIN":
                bind(MessageLogger.class).to(PluginMessageLogger.class);
                break;
            default:
                throw new IllegalArgumentException("Failed to configure message logger of illegal type: " + messageLoggerType);
        }
    }

    @Override
    protected void configure() {
        // Channel bindings
        bind(ChannelCommandService.class).to(DefaultChannelCommandService.class);
        expose(ChannelCommandService.class);

        bind(ChannelQueryService.class).to(DefaultChannelQueryService.class);
        expose(ChannelQueryService.class);

        bind(ChannelRepository.class).to(DefaultChannelRepository.class);
        expose(ChannelRepository.class);

        install(new FactoryModuleBuilder()
                .implement(CommandHandler.class, Names.named("local"), LocalChannelCommandHandler.class)
                .implement(CommandHandler.class, Names.named("global"), GlobalChannelCommandHandler.class)
                .build(ChannelCommandHandlerFactory.class));

        // Profile bindings
        bind(ChatProfileCommandService.class).to(DefaultChatProfileCommandService.class);
        expose(ChatProfileCommandService.class);

        bind(ChatProfileQueryService.class).to(DefaultChatProfileQueryService.class);
        expose(ChatProfileQueryService.class);

        bind(ChatProfileRepository.class).to(DefaultChatProfileRepository.class);
        expose(ChatProfileRepository.class);

        bind(MessageCommandService.class).to(DefaultMessageCommandService.class);
        expose(MessageCommandService.class);

        install(new FactoryModuleBuilder()
                .implement(Message.class, Names.named("formatted"), FormattedMessage.class)
                .implement(Message.class, Names.named("mute"), MuteMessage.class)
                .implement(Message.class, Names.named("server"), ServerMessage.class)
                .implement(Message.class, Names.named("custom"), CustomMessage.class)
                .build(MessageFactory.class));

        // Misc chat bindings
        bind(ChatRouter.class).to(DefaultChatRouter.class);
        // TODO moving all configuration code out of the plugin class into a configuration class allows for the
        // TODO removal of this expose
        expose(ChatRouter.class);

        configureMessageLogger();
    }
}
