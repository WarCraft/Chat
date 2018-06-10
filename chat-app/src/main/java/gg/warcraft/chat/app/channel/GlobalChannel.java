package gg.warcraft.chat.app.channel;

import com.google.common.base.MoreObjects;
import com.google.common.eventbus.Subscribe;
import gg.warcraft.chat.api.channel.Channel;
import gg.warcraft.monolith.api.entity.player.event.PlayerConnectEvent;
import gg.warcraft.monolith.api.entity.player.event.PlayerDisconnectEvent;
import gg.warcraft.monolith.api.entity.player.event.PlayerPermissionsChangedEvent;
import gg.warcraft.monolith.api.util.ColorCode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public class GlobalChannel implements Channel {
    private final String name;
    private final List<String> aliases;
    private final String shortcut;
    private final ColorCode color;
    private final String formattingString;
    private final Predicate<UUID> joinCondition;

    final Set<UUID> recipients;

    public GlobalChannel(String name, List<String> aliases, String shortcut, ColorCode color, String formattingString,
                         Predicate<UUID> joinCondition) {
        this.name = name;
        this.aliases = aliases;
        this.shortcut = shortcut;
        this.color = color;
        this.formattingString = formattingString;
        this.joinCondition = joinCondition;
        this.recipients = new HashSet<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public String getShortcut() {
        return shortcut;
    }

    @Override
    public ColorCode getColor() {
        return color;
    }

    @Override
    public String getFormattingString() {
        return formattingString;
    }

    public List<UUID> getRecipients() {
        return new ArrayList<>(recipients);
    }

    public Predicate<UUID> getJoinCondition() {
        return joinCondition;
    }

    // TODO move these out of channel object?
    @Subscribe
    public void onPlayerConnect(PlayerConnectEvent event) {
        if (joinCondition.test(event.getPlayerId())) {
            recipients.add(event.getPlayerId());
        }
    }

    @Subscribe
    public void onPlayerPermissionsChanged(PlayerPermissionsChangedEvent event) {
        if (recipients.remove(event.getPlayerId())) {
            if (joinCondition.test(event.getPlayerId())) {
                recipients.add(event.getPlayerId());
            }
        }
    }

    @Subscribe
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        recipients.remove(event.getPlayerId());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass())
                .add("name", name)
                .add("aliases", aliases)
                .add("shortcut", shortcut)
                .add("color", color)
                .add("formattingString", formattingString)
                .toString();
    }
}
