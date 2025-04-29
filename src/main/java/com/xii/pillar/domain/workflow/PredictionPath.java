package com.xii.pillar.domain.workflow;

import com.xii.pillar.domain.constant.ExecutionMode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;

@Document("prediction_path")
public class PredictionPath implements Serializable {
    public enum PathType {
        general, predict, explore,
    }

    private String id;
    private String nodeId;
    private Long createAt;
    private Long updateAt;
    private Integer priority;

    private ExecutionMode executionMode;
    private PathType pathType;

    // path defined
    private String condition;
    private ArrayList<String> taskIds = new ArrayList<>();
    private String exploratoryPluginName;

    public String getId() {
        return id;
    }

    public PredictionPath setId(String id) {
        this.id = id;
        return this;
    }

    public String getNodeId() {
        return nodeId;
    }

    public PredictionPath setNodeId(String nodeId) {
        this.nodeId = nodeId;
        return this;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public PredictionPath setCreateAt(Long createAt) {
        this.createAt = createAt;
        return this;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public PredictionPath setUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
        return this;
    }

    public Integer getPriority() {
        return priority;
    }

    public PredictionPath setPriority(Integer priority) {
        this.priority = priority;
        return this;
    }

    public ExecutionMode getExecutionMode() {
        return executionMode;
    }

    public PredictionPath setExecutionMode(ExecutionMode executionMode) {
        this.executionMode = executionMode;
        return this;
    }

    public PathType getPathType() {
        return pathType;
    }

    public PredictionPath setPathType(PathType pathType) {
        this.pathType = pathType;
        return this;
    }

    public String getCondition() {
        return condition;
    }

    public PredictionPath setCondition(String condition) {
        this.condition = condition;
        return this;
    }

    public ArrayList<String> getTaskIds() {
        return taskIds;
    }

    public PredictionPath setTaskIds(ArrayList<String> taskIds) {
        this.taskIds = taskIds;
        return this;
    }

    public String getExploratoryPluginName() {
        return exploratoryPluginName;
    }

    public PredictionPath setExploratoryPluginName(String exploratoryPluginName) {
        this.exploratoryPluginName = exploratoryPluginName;
        return this;
    }
}
