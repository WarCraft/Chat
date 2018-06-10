package gg.warcrat.chat.app.logger;

import gg.warcraft.monolith.api.chat.message.Message;

public interface MessageLogger {
    void log(Message message);

    void setGroup(String group);
}
