package com.xii.pillar.connect;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xii.pillar.utils.ParamUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@Slf4j
public class CommonHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("beforeHandshake >>>>>> {} {}", attributes, request.getURI().getQuery());
        ObjectNode params = ParamUtil.parse(request);
        attributes.put("deviceId", params.get("deviceId").textValue());
        attributes.put("userId", params.get("userId").textValue());
        attributes.put("connectTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        log.info("afterHandshake >>>>>>");
    }
}
