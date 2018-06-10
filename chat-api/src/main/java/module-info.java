module gg.warcraft.chat.api {
    requires transitive gg.warcraft.monolith.api;

    exports gg.warcraft.chat.api;
    exports gg.warcraft.chat.api.event;
    exports gg.warcraft.chat.api.service;

    exports gg.warcraft.chat.api.message;

    exports gg.warcraft.chat.api.channel;
    exports gg.warcraft.chat.api.channel.service;

    exports gg.warcraft.chat.api.profile;
    exports gg.warcraft.chat.api.profile.service;
}
