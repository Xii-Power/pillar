package com.xii.pillar.domain.workflow;

import com.xii.pillar.domain.constant.NodeType;
import com.xii.pillar.utils.IdGenerator;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;

@Document("p_node")
public class PNode implements Serializable {
    private String id;
    private String flowId;
    private String name;
    private ArrayList<String> preNodeIds;
    private NodeType nodeType;

    private Long createAt;
    private Long updateAt;

    public PNode() {
    }

    public PNode(String flowId, String name, ArrayList<String> preNodeIds, NodeType nodeType) {
        this.id = IdGenerator.uuid();
        this.flowId = flowId;
        this.name = name;
        this.preNodeIds = preNodeIds;
        this.nodeType = nodeType;
        this.createAt = System.currentTimeMillis();
        this.updateAt = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public PNode setId(String id) {
        this.id = id;
        return this;
    }

    public String getFlowId() {
        return flowId;
    }

    public PNode setFlowId(String flowId) {
        this.flowId = flowId;
        return this;
    }

    public String getName() {
        return name;
    }

    public PNode setName(String name) {
        this.name = name;
        return this;
    }

    public ArrayList<String> getPreNodeIds() {
        return preNodeIds;
    }

    public PNode setPreNodeIds(ArrayList<String> preNodeIds) {
        this.preNodeIds = preNodeIds;
        return this;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public PNode setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
        return this;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public PNode setCreateAt(Long createAt) {
        this.createAt = createAt;
        return this;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public PNode setUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
        return this;
    }
}