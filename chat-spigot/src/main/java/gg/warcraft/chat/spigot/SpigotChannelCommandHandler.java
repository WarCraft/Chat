package gg.warcraft.chat.spigot;

import gg.warcraft.chat.app.channel.handler.ChannelCommandHandler;
import gg.warcraft.monolith.api.core.command.Command;
import gg.warcraft.monolith.api.core.command.CommandHandler;
import gg.warcraft.monolith.api.core.command.PlayerCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import scala.Option$;

import java.util.Arrays;

// TODO remove hack
public class SpigotChannelCommandHandler implements Listener {

    private final ChannelCommandHandler commandHandler = new ChannelCommandHandler();

    @EventHandler
    public void onPreCommand(PlayerCommandPreprocessEvent event) {
        String[] cmd = event.getMessage().split(" ");
        if (cmd.length == 0) return;
        CommandHandler handler = commandHandler.getHandler(cmd[0]);
        if (handler == null) return;
        else {
            handler.handle(
                    new PlayerCommandSender(
                            event.getPlayer().getName(),
                            Option$.MODULE$.apply(event.getPlayer().getUniqueId())
                    ),
                    new Command(cmd[0], cmd[0],
                            scala.collection.JavaConverters.collectionAsScalaIterableConverter(
                                    Arrays.asList(Arrays.copyOfRange(cmd, 1, cmd.length))
                            ).asScala().toSeq())
            );
            event.setCancelled(true);
        }
    }
}
