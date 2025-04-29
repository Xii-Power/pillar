package com.xii.pillar.connect;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xii.pillar.service.PillarApplicationContextHolder;
import com.xii.pillar.service.endpoint.MessageReportService;
import com.xii.pillar.utils.ParamUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@Slf4j
public class EndpointHandler extends AbstractWebSocketHandler implements ApplicationContextAware {

    private MessageReportService messageReportService;

    public EndpointHandler() {
        this.messageReportService = (MessageReportService) PillarApplicationContextHolder.getBean("endpointReportService");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("# EndpointHandler Context ...");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        ObjectNode param = ParamUtil.parse(session);
        SessionHolder.add(session, ConnectorType.ENDPOINT);
        log.info("# client_connected. params:{}", param);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("# handleTextMessage. message:{}", message.getPayload());
        messageReportService.handle((String) session.getAttributes().get("deviceId"), message.getPayload());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("# handleTransportError.", exception);
        SessionHolder.remove(session, ConnectorType.ENDPOINT);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.error("afterConnectionClosed. code:{}", status.getCode());
        SessionHolder.remove(session, ConnectorType.ENDPOINT);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        log.info("handleBinaryMessage. message length:{}", message.getPayloadLength());
    }
}
