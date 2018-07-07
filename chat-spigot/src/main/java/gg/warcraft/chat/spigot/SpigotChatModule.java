package gg.warcraft.chat.spigot;

import gg.warcraft.chat.api.service.ChatServerAdapter;
import gg.warcraft.chat.app.AbstractChatModule;

public class SpigotChatModule extends AbstractChatModule {

    @Override
    public void configure() {
        super.configure();
        bind(ChatServerAdapter.class).to(SpigotChatAdapter.class);
    }
}
