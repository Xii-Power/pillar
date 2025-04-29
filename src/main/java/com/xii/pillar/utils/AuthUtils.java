package com.xii.pillar.utils;

import javax.servlet.http.HttpServletRequest;

public class AuthUtils {

    private final static String DEBUG_TOKEN = "";
    public static boolean isDebug(HttpServletRequest request) {
        return DEBUG_TOKEN.equals(request.getHeader("debugToken"));
    }
}
