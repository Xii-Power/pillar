package com.xii.pillar.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xii.pillar.connect.ConnectorType;
import com.xii.pillar.connect.SessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
public class EndpointController {

    @RequestMapping(value = "/ep/test", method = RequestMethod.POST)
    @ResponseBody
    public ObjectNode debug(HttpServletRequest request, @RequestBody ObjectNode msg) {
        boolean isPushed = SessionHolder.sendTextMessage(ConnectorType.ENDPOINT, msg.get("deviceId").asText(), msg);
        msg.put("isPushed", isPushed);
        return msg;
    }
}
