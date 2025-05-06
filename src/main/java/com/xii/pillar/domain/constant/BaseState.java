package com.xii.pillar.domain.constant;

public enum BaseState {

    // template flow state
    PUBLISHED, EDITING, DELETED,

    // workflow state
    PENDING,
    IN_PROGRESS,
    FINISHED,
    FAIL,

    // session state
    CANCEL,
    BREAK,
}
