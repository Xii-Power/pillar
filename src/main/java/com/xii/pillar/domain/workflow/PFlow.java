package com.xii.pillar.domain.workflow;

import com.xii.pillar.domain.constant.BaseState;
import com.xii.pillar.utils.IdGenerator;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document("p_flow")
public class PFlow implements Serializable {
    private String id;
    private String name;
    private String groupId;
    private BaseState state;
    private Integer priority;

    private Long createAt;
    private Long updateAt;

    public PFlow() {
    }

    public PFlow(String name, String groupId, Integer priority) {
        this.id = IdGenerator.uuid();
        this.name = name;
        this.groupId = groupId;
        this.priority = priority;
        this.state = BaseState.EDITING;
        this.createAt = System.currentTimeMillis();
        this.updateAt = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public PFlow setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PFlow setName(String name) {
        this.name = name;
        return this;
    }

    public String getGroupId() {
        return groupId;
    }

    public PFlow setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public BaseState getState() {
        return state;
    }

    public PFlow setState(BaseState state) {
        this.state = state;
        return this;
    }

    public Integer getPriority() {
        return priority;
    }

    public PFlow setPriority(Integer priority) {
        this.priority = priority;
        return this;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public PFlow setCreateAt(Long createAt) {
        this.createAt = createAt;
        return this;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public PFlow setUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
        return this;
    }
}
