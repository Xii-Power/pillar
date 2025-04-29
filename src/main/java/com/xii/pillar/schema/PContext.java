package com.xii.pillar.schema;

import java.io.Serializable;
import java.util.HashMap;

public class PContext implements Serializable {
    private String inputText;

    private HashMap<String, Object> sessionMap;

    public String getInputText() {
        return inputText;
    }

    public PContext setInputText(String inputText) {
        this.inputText = inputText;
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
