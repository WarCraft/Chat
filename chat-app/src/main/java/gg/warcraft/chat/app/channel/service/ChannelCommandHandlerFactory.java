package gg.warcraft.chat.app.channel.service;

import com.google.inject.name.Named;
import gg.warcraft.chat.app.channel.GlobalChannel;
import gg.warcraft.chat.app.channel.LocalChannel;
import gg.warcraft.monolith.api.command.CommandHandler;

public interface ChannelCommandHandlerFactory {

    @Named("local")
    CommandHandler createLocalChannelCommandExecutor(LocalChannel channel);

    @Named("global")
    CommandHandler createGlobalChannelCommandExecutor(GlobalChannel channel);
}
