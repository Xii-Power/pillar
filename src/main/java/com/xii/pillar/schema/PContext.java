package com.xii.pillar.schema;

import java.io.Serializable;
import java.util.HashMap;

public class PContext implements Serializable {
    private String inputText;
    private String deviceId;

    private HashMap<String, Object> sessionMap = new HashMap<>();

    public String getInputText() {
        return inputText;
    }

    public PContext setInputText(String inputText) {
        this.inputText = inputText;
        return this;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public PContext setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public HashMap<String, Object> getSessionMap() {
        return sessionMap;
    }

    public PContext setSessionMap(HashMap<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
        return this;
    }
}
