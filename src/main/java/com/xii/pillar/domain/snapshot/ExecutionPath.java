package com.xii.pillar.domain.snapshot;

import com.xii.pillar.domain.constant.BaseState;
import com.xii.pillar.domain.constant.ExecutionMode;
import com.xii.pillar.domain.workflow.PredictionPath;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("execution_path")
public class ExecutionPath {
    private String id;
    private String pathId;
    private String nodeSnapshotId;
    private Long createAt;
    private Long updateAt;

    // IN_PROGRESS, FINISHED, CANCEL
    private BaseState state;
    private Integer priority;

    private ExecutionMode executionMode;
    private PredictionPath.PathType pathType;

    // path defined
    private String condition;
    private String exploratoryPluginName;

    public String getId() {
        return id;
    }

    public ExecutionPath setId(String id) {
        this.id = id;
        return this;
    }

    public String getPathId() {
        return pathId;
    }

    public ExecutionPath setPathId(String pathId) {
        this.pathId = pathId;
        return this;
    }

    public String getNodeSnapshotId() {
        return nodeSnapshotId;
    }

    public ExecutionPath setNodeSnapshotId(String nodeSnapshotId) {
        this.nodeSnapshotId = nodeSnapshotId;
        return this;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public ExecutionPath setCreateAt(Long createAt) {
        this.createAt = createAt;
        return this;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public ExecutionPath setUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
        return this;
    }

    public BaseState getState() {
        return state;
    }

    public ExecutionPath setState(BaseState state) {
        this.state = state;
        return this;
    }

    public Integer getPriority() {
        return priority;
    }

    public ExecutionPath setPriority(Integer priority) {
        this.priority = priority;
        return this;
    }

    public ExecutionMode getExecutionMode() {
        return executionMode;
    }

    public ExecutionPath setExecutionMode(ExecutionMode executionMode) {
        this.executionMode = executionMode;
        return this;
    }

    public PredictionPath.PathType getPathType() {
        return pathType;
    }

    public ExecutionPath setPathType(PredictionPath.PathType pathType) {
        this.pathType = pathType;
        return this;
    }

    public String getCondition() {
        return condition;
    }

    public ExecutionPath setCondition(String condition) {
        this.condition = condition;
        return this;
    }

    public String getExploratoryPluginName() {
        return exploratoryPluginName;
    }

    public ExecutionPath setExploratoryPluginName(String exploratoryPluginName) {
        this.exploratoryPluginName = exploratoryPluginName;
        return this;
    }
}
