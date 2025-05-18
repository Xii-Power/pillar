package com.xii.pillar.test;

import com.googlecode.aviator.AviatorEvaluator;
import com.xii.pillar.utils.JsonUtil;

import java.util.HashMap;
import java.util.Map;

public class ConditionTest {

    public final static String message = "{\"model\":\"ds\",\"prompt\":\"who are you?\",\"stream\":false}";
    public final static String conditionStr = "task_id.code == '1' && task_id.message.model == 'ds'";

    public static void testMatcher() throws Exception {
        Map<String, Object> abc = new HashMap<String, Object>();
        abc.put("code", "1");
        abc.put("message", JsonUtil.read(message, HashMap.class));

        String message = JsonUtil.write(abc);
        Map<String, Object> env = new HashMap<String, Object>();

        env.put("task_id", JsonUtil.read(message, HashMap.class));
        System.out.println(AviatorEvaluator.execute(conditionStr, env));
    }

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        testMatcher();
        System.out.println(System.currentTimeMillis() - start);
    }
}
