package com.xii.pillar.utils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.util.ObjectUtils;
import org.springframework.web.socket.WebSocketSession;


@Slf4j
public class ParamUtil {

    public static ObjectNode parse(WebSocketSession session) {
        return getParams(session.getUri().getQuery());
    }

    public static ObjectNode parse(ServerHttpRequest request) {
        return getParams(request.getURI().getQuery());
    }

    private static ObjectNode getParams(String requestQuery) {
        ObjectNode jsonParam = JsonUtil.createObjectNode();
        if (ObjectUtils.isEmpty(requestQuery)) return jsonParam;

        String[] params = requestQuery.split("&");
        if (params.length > 0) {
            for (int paramLoop = 0; paramLoop < params.length; paramLoop++) {
                String keyValue[] = params[paramLoop].split("=");
                if (keyValue.length == 2) {
                    jsonParam.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return jsonParam;
    }



}
