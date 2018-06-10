package gg.warcrat.chat.app.channel.service;

import com.google.inject.name.Named;
import gg.warcraft.monolith.api.command.CommandHandler;
import gg.warcraft.monolith.app.chat.channel.GlobalChannel;
import gg.warcraft.monolith.app.chat.channel.LocalChannel;

public interface ChannelCommandHandlerFactory {

    @Named("local")
    CommandHandler createLocalChannelCommandExecutor(LocalChannel channel);

    @Named("global")
    CommandHandler createGlobalChannelCommandExecutor(GlobalChannel channel);
}
