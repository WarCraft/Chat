package gg.warcraft.chat.spigot;

import com.google.inject.Inject;
import gg.warcraft.chat.api.service.ChatServerAdapter;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Predicate;

public class SpigotChatAdapter implements ChatServerAdapter {
    private final Server server;

    @Inject
    public SpigotChatAdapter(Server server) {
        this.server = server;
    }

    @Override
    public void sendMessageToPlayer(String message, UUID playerId) {
        Player player = server.getPlayer(playerId);
        if (player != null) {
            player.sendMessage(message);
        }
    }

    @Override
    public void sendMessageToAllPlayersMatching(String message, Predicate<UUID> sendCondition) {
        server.getOnlinePlayers().stream()
                .filter(player -> sendCondition.test(player.getUniqueId()))
                .forEach(player -> player.sendMessage(message));
    }
}
