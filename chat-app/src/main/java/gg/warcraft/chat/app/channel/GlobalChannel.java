package gg.warcraft.chat.app.channel;

import com.google.common.base.MoreObjects;
import gg.warcraft.chat.api.channel.Channel;
import gg.warcraft.monolith.api.core.event.Event;
import gg.warcraft.monolith.api.core.event.EventHandler;
import gg.warcraft.monolith.api.player.PlayerConnectEvent;
import gg.warcraft.monolith.api.player.PlayerDisconnectEvent;
import gg.warcraft.monolith.api.player.PlayerPermissionsChangedEvent;
import gg.warcraft.monolith.api.util.ColorCode;

import java.util.*;
import java.util.function.Predicate;

public class GlobalChannel implements Channel, EventHandler {
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

    // TODO move event handling out of channel object?
    @Override
    public void handle(Event event) {
        if (event instanceof PlayerConnectEvent) {
            onPlayerConnect((PlayerConnectEvent) event);
        } else if (event instanceof PlayerDisconnectEvent) {
            onPlayerDisconnect((PlayerDisconnectEvent) event);
        } else if (event instanceof PlayerPermissionsChangedEvent) {
            onPlayerPermissionsChanged((PlayerPermissionsChangedEvent) event);
        }
    }

    public void onPlayerConnect(PlayerConnectEvent event) {
        if (joinCondition.test(event.playerId())) {
            recipients.add(event.playerId());
        }
    }

    public void onPlayerPermissionsChanged(PlayerPermissionsChangedEvent event) {
        if (recipients.remove(event.playerId())) {
            if (joinCondition.test(event.playerId())) {
                recipients.add(event.playerId());
            }
        }
    }

    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        recipients.remove(event.playerId());
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
