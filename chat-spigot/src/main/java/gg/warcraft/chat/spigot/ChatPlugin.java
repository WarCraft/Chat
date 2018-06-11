package gg.warcraft.chat.spigot;

import com.google.inject.Injector;
import gg.warcraft.chat.api.ChatRouter;
import gg.warcraft.chat.app.profile.handler.ChatProfileInitializationHandler;
import gg.warcraft.monolith.api.Monolith;
import gg.warcraft.monolith.api.core.EventService;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Injector injector = Monolith.getInstance().getInjector();

        EventService eventService = injector.getInstance(EventService.class);
        ChatRouter chatRouter = injector.getInstance(ChatRouter.class);
        ChatProfileInitializationHandler profileInitializationHandler =
                injector.getInstance(ChatProfileInitializationHandler.class);

        eventService.subscribe(chatRouter);
        eventService.subscribe(profileInitializationHandler);
    }
}
