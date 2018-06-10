package gg.warcraft.chat.app.profile.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import gg.warcraft.chat.api.profile.ChatProfile;
import gg.warcraft.chat.api.profile.ChatTag;
import gg.warcraft.chat.api.profile.service.ChatProfileRepository;
import gg.warcraft.chat.app.profile.PlayerChatProfile;
import gg.warcraft.chat.app.profile.PlayerChatTag;
import gg.warcraft.monolith.api.data.DataService;
import gg.warcraft.monolith.api.util.ColorCode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Singleton
public class DefaultChatProfileRepository implements ChatProfileRepository {
    private static final String KEY_PREFIX = "chatprofile:";
    private static final String NAME = "name";
    private static final String TAG_NAME = "tagname";
    private static final String HOME_CHANNEL = "homechannel";
    private static final String TAG_COLOR = "tagcolor";
    private static final String OPTED_OUT = "optedout";

    private final DataService dataService;

    @Inject
    public DefaultChatProfileRepository(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public ChatProfile get(UUID playerId) {
        Map<String, String> data = dataService.getMap(getDataKey(playerId));
        if (data.isEmpty()) {
            return null;
        }
        String name = getNameFromData(data);
        ChatTag tag = getTagFromData(data);
        String homeChannel = getHomeChannelFromData(data);
        Set<String> optedOut = getOptedOutFromData(data);
        return new PlayerChatProfile(playerId, name, tag, homeChannel, optedOut);
    }

    @Override
    public void save(ChatProfile profile) {
        Map<String, String> data = new HashMap<>();
        mapNameToData(data, profile.getName());
        mapTagToData(data, profile.getTag());
        mapHomeChannelToData(data, profile.getHomeChannel());
        mapOptedOutToData(data, profile.getOptedOut());
        dataService.setMap(getDataKey(profile.getPlayerId()), data);
    }

    private String getDataKey(UUID playerId) {
        return KEY_PREFIX + playerId;
    }

    private String getNameFromData(Map<String, String> data) {
        return data.get(NAME);
    }

    private ChatTag getTagFromData(Map<String, String> data) {
        String tagName = data.get(TAG_NAME);
        ColorCode tagColor = ColorCode.valueOf(data.get(TAG_COLOR));
        return new PlayerChatTag(tagName, tagColor);
    }

    private String getHomeChannelFromData(Map<String, String> data) {
        return data.get(HOME_CHANNEL);
    }

    private Set<String> getOptedOutFromData(Map<String, String> data) {
        String optedOutCsv = data.get(OPTED_OUT);
        String[] optedOut = optedOutCsv.split(",");
        return new HashSet<>(Arrays.asList(optedOut));
    }

    private void mapNameToData(Map<String, String> data, String name) {
        data.put(NAME, name);
    }

    private void mapTagToData(Map<String, String> data, ChatTag tag) {
        data.put(TAG_NAME, tag.getName());
        data.put(TAG_COLOR, tag.getColor().name());
    }

    private void mapHomeChannelToData(Map<String, String> data, String homeChannel) {
        data.put(HOME_CHANNEL, homeChannel);
    }

    private void mapOptedOutToData(Map<String, String> data, Set<String> optedOut) {
        String optedOutCsv = String.join(",", optedOut.toArray(new String[0]));
        data.put(OPTED_OUT, optedOutCsv);
    }
}
