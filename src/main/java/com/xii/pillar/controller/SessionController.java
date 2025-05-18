package com.xii.pillar.controller;

import com.xii.pillar.schema.PContext;
import com.xii.pillar.service.FlowSessionManager;
import com.xii.pillar.service.TaskDispatcher;
import com.xii.pillar.service.config.SessionContextService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;


@Slf4j
@Controller
public class SessionController {

    @Autowired
    private FlowSessionManager flowSessionManager;
    @Autowired
    private TaskDispatcher taskDispatcher;
    @Autowired
    private SessionContextService sessionContextService;

    @RequestMapping(value = "/session/test", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> test(HttpServletRequest request) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("timestamp", System.currentTimeMillis());
        return params;
    }

    @RequestMapping(value = "/session/schedule", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> schedule(HttpServletRequest request) {
        taskDispatcher.scanPath();
        flowSessionManager.scanSession();
        HashMap<String, Object> params = new HashMap<>();
        params.put("timestamp", System.currentTimeMillis());
        return params;
    }

    @RequestMapping(value = "/session/create", method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String, Object> createSession(HttpServletRequest request, @RequestBody HashMap<String, Object> params) {
        PContext context = new PContext();
        context.setSessionMap(params);
        String sessionId = (String) params.get("id");
        sessionContextService.setById(sessionId, context);

        if (params.containsKey("start")) {
            flowSessionManager.createSession(sessionId, (String) params.get("flowId"), true);
        }
        return params;
    }

}
