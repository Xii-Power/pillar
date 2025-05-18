package com.xii.pillar.domain.constant;

public interface GlobalConstant {

    String ONLINE = "online";
    String OFFLINE = "offline";

    String SCAN_MODE_IDLE = "idle";
    String SCAN_MODE_SELECTED = "selected";

    int MAX_RETRY_NUM = 3;
    long TIME_DELAY = 10 * 1000;

    String PRE_SNAPSHOT = "SNAPSHOT-";
    String PRE_TASK_CONDITION = "task_";

    String NODE_ERROR_NULL = "NE-00001";
    String NODE_ERROR_PARAMS = "NE-00002";

}
