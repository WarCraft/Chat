package gg.warcrat.chat.app.logger;

import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.model.DescribeLogGroupsResult;
import com.amazonaws.services.logs.model.InputLogEvent;
import com.amazonaws.services.logs.model.PutLogEventsRequest;
import com.amazonaws.services.logs.model.PutLogEventsResult;
import com.google.inject.Inject;
import gg.warcraft.monolith.api.chat.message.Message;
import gg.warcraft.monolith.api.core.TaskService;
import gg.warcraft.monolith.api.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class CloudWatchMessageLogger implements MessageLogger {
    private final static String LOG_GROUP = "LegendsChat";

    private final TaskService taskService;
    private final TimeUtils timeUtils;
    private final AWSLogs cloudwatch;

    /* private */ String logStream;
    private String nextToken;
    /* private */ List<InputLogEvent> logEvents;
    /* private */ long lastPutTimestamp;

    @Inject
    public CloudWatchMessageLogger(TaskService taskService, TimeUtils timeUtils, AWSLogs cloudwatch) {
        this.taskService = taskService;
        this.timeUtils = timeUtils;
        this.cloudwatch = cloudwatch;
        this.logEvents = new ArrayList<>();
    }

    @Override
    public void setGroup(String group) {
        logStream = group;

        DescribeLogGroupsResult result = cloudwatch.describeLogGroups();
        nextToken = result.getNextToken();
    }

    @Override
    public void log(Message message) {
        String channelName = message.getChannel() != null ? message.getChannel().getName() : "DM";
        String log = String.format("[%s] %s: %s", channelName, message.getSender().getName(), message.getOriginal());
        InputLogEvent logEvent = new InputLogEvent()
                .withTimestamp(System.currentTimeMillis())
                .withMessage(log);
        logEvents.add(logEvent);

        if (System.currentTimeMillis() - lastPutTimestamp > 60 * TimeUtils.MILLIS_PER_SECOND) {
            lastPutTimestamp = System.currentTimeMillis();
            putLogEvents();
            logEvents = new ArrayList<>();
        }
    }

    private void putLogEvents() {
        if (logStream == null) {
            throw new IllegalStateException("Attempted to log message without specifying group.");
        }

        PutLogEventsRequest request = new PutLogEventsRequest()
                .withLogGroupName(LOG_GROUP)
                .withLogStreamName(logStream)
                .withSequenceToken(nextToken)
                .withLogEvents(logEvents);
        taskService.runNextTickAsync(() -> {
            PutLogEventsResult result = cloudwatch.putLogEvents(request);
            nextToken = result.getNextSequenceToken();
        });
    }
}
