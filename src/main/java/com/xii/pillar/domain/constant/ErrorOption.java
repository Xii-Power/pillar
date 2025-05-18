package com.xii.pillar.domain.constant;

import java.util.Arrays;
import java.util.List;

public enum ErrorOption {
    IGNORE,
    RETRY,
    BREAK,
    RE_PLAN_NEXT, // Continue the pending tasks of current path, together plan next path by all task result.
    RE_PLAN_NOW, // Break pending tasks of current path, directly plan next path.
    ;

    final static List<ErrorOption> RE_PLAN_OPTIONS = Arrays.asList(RE_PLAN_NEXT, RE_PLAN_NOW);
    final static List<ErrorOption> CONTINUE_OPTIONS = Arrays.asList(RE_PLAN_NEXT, IGNORE);


    public static boolean isRePlan(ErrorOption option) {
        return RE_PLAN_OPTIONS.contains(option);
    }

    public static boolean isContinue(ErrorOption option) {
        return CONTINUE_OPTIONS.contains(option);
    }
}
