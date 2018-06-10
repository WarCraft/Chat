package gg.warcraft.monolith.spigot.chat;

import com.google.inject.Inject;
import gg.warcraft.monolith.api.chat.adapter.ChatServerAdapter;
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
