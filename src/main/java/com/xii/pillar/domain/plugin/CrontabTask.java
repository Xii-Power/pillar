package com.xii.pillar.domain.plugin;

import com.xii.pillar.domain.constant.BaseState;
import com.xii.pillar.utils.IdGenerator;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("p_crontab_task")
public class CrontabTask {

    public enum ExecType {
        ONE_TIME, REPEAT
    }

    private String id;
    // PENDING, IN_PROGRESS, FINISHED
    private BaseState state;
    private Integer perNum;
    private String unit;
    private ExecType execType;
    private Long execAt;
    private Long createAt;

    public CrontabTask() {
    }

    public CrontabTask(Integer perNum, String unit, Long execAt, ExecType execType) {
        this.id = IdGenerator.uuid();
        this.state = BaseState.PENDING;
        this.perNum = perNum;
        this.unit = unit;
        this.execAt = execAt;
        this.execType = execType;
        this.createAt = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public CrontabTask setId(String id) {
        this.id = id;
        return this;
    }

    public BaseState getState() {
        return state;
    }

    public CrontabTask setState(BaseState state) {
        this.state = state;
        return this;
    }

    public Integer getPerNum() {
        return perNum;
    }

    public CrontabTask setPerNum(Integer perNum) {
        this.perNum = perNum;
        return this;
    }

    public String getUnit() {
        return unit;
    }

    public CrontabTask setUnit(String unit) {
        this.unit = unit;
        return this;
    }

    public ExecType getExecType() {
        return execType;
    }

    public CrontabTask setExecType(ExecType execType) {
        this.execType = execType;
        return this;
    }

    public Long getExecAt() {
        return execAt;
    }

    public CrontabTask setExecAt(Long execAt) {
        this.execAt = execAt;
        return this;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public CrontabTask setCreateAt(Long createAt) {
        this.createAt = createAt;
        return this;
    }


}
