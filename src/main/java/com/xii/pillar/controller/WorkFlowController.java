package com.xii.pillar.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xii.pillar.domain.constant.NodeType;
import com.xii.pillar.domain.workflow.PFlow;
import com.xii.pillar.domain.workflow.PNode;
import com.xii.pillar.domain.workflow.PTask;
import com.xii.pillar.repository.workflow.FlowRepo;
import com.xii.pillar.repository.workflow.NodeRepo;
import com.xii.pillar.repository.workflow.TaskRepo;
import com.xii.pillar.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;


@Slf4j
@Controller
public class WorkFlowController {

    @Autowired
    private FlowRepo flowRepo;
    @Autowired
    private NodeRepo nodeRepo;
    @Autowired
    private TaskRepo taskRepo;

    @RequestMapping(value = "/wf/flow", method = RequestMethod.POST)
    @ResponseBody
    public PFlow addOrUpdateFlow(HttpServletRequest request, @RequestBody PFlow flow) {
        if(ObjectUtils.isEmpty(flow.getId())) {
            PFlow newFlow = new PFlow(flow.getName(), flow.getGroupId(), flow.getPriority());
            flowRepo.save(newFlow);
            return newFlow;
        }

        flowRepo.updateState(flow.getId(), flow.getState());
        return flow;
    }

    @RequestMapping(value = "/wf/node", method = RequestMethod.POST)
    @ResponseBody
    public PNode addOrUpdateNode(HttpServletRequest request, @RequestBody PNode node) {
        if (ObjectUtils.isEmpty(node.getId())) {
            if (isExist(node)) return null;

            PNode newNode = new PNode(node.getFlowId(), node.getName(), node.getPreNodeIds(), node.getNodeType());
            nodeRepo.save(newNode);
            return newNode;
        }

        nodeRepo.updateNode(node.getId(), node.getPreNodeIds());
        return node;
    }

    private boolean isExist(PNode node) {
        if (node.getNodeType() != NodeType.start && node.getNodeType() != NodeType.end) {
            return false;
        }

        PNode pNode = nodeRepo.findNodeByType(node.getFlowId(), node.getNodeType());
        return pNode != null;
    }

    @RequestMapping(value = "/wf/task", method = RequestMethod.POST)
    public PTask addOrUpdateTask(HttpServletRequest request, @RequestBody ObjectNode objectNode) {
        if (!objectNode.has("id")) {
            PTask newTask = PTask.parse(objectNode);
            taskRepo.save(newTask);
            return newTask;
        }

        PTask task = taskRepo.getById(objectNode.get("id").asText(), PTask.class);
        if (task == null) return null;
        try {
            taskRepo.updateTask(task.getId(),
                    objectNode.has("contextParser") ? JsonUtil.read(objectNode.get("contextParser").asText(), HashMap.class) : null,
                    objectNode.has("params") ? JsonUtil.read(objectNode.get("params").asText(), HashMap.class) : null);
        } catch (Exception e) {
            log.error("# update task error. task:{}", task.getId(), e);
        }
        return task;
    }

}
