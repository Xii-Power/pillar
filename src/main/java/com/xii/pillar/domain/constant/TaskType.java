package com.xii.pillar.domain.constant;

import java.util.Arrays;
import java.util.List;

public enum TaskType {
    issue_text_ask,
    issue_tts_ask,

    audio_parser,
    image_parser,

    image_file_generate,
    tts_file_generate,

    endpoint_message_push,
    endpoint_audio_play,

    db_query,

    request_rpc,
    request_http,

    callback_http,

    topic_message_parser,
    ;

    final static List<TaskType> syncTasks = Arrays.asList(db_query, request_rpc, request_http);
    public boolean isSync() {
        return syncTasks.contains(this);
    }
}
