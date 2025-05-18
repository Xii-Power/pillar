package com.xii.pillar.service.task.impl;

import com.google.common.base.Joiner;
import com.googlecode.aviator.AviatorEvaluator;
import com.xii.pillar.domain.snapshot.PTaskSnapshot;
import com.xii.pillar.schema.PContext;
import com.xii.pillar.schema.PException;
import com.xii.pillar.service.config.SessionContextService;
import com.xii.pillar.service.task.PTaskService;
import com.xii.pillar.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;

import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service("HttpTask")
public class HttpTaskService implements PTaskService {

    @Autowired
    private SessionContextService sessionContextService;

    @Override
    public boolean prepare(PTaskSnapshot taskSnapshot, String sessionId) throws PException {
        if(isEmpty(taskSnapshot.getUrl())) return false;

        String url = taskSnapshot.getUrl();
        HashMap<String, String> params = taskSnapshot.getParams();
        String method = params.get("method") == null ? HttpMethod.GET.toString() : params.remove("method");
        params.put("method", method);

        if(!isEmpty(taskSnapshot.getContextParser())) {
            PContext context = sessionContextService.getById(sessionId);
            taskSnapshot.getContextParser().forEach((key, parser) -> {
                params.put(key, (String) AviatorEvaluator.execute(parser, context.getSessionMap()));
            });
        }

        if(HttpMethod.GET.matches(method)) {
            taskSnapshot.setUrl(url + "?" + Joiner.on('&').withKeyValueSeparator("=").join(params));
        }

        taskSnapshot.setParams(params);
        log.info("# HTTP_TASK_PREPARE. id:{}, params:{}", taskSnapshot.getId(), params);
        return true;
    }

    @Override
    public boolean start(PTaskSnapshot taskSnapshot, String sessionId) throws PException {
        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            Response response = okHttpClient.newCall(buildRequest(taskSnapshot)).execute();
            taskSnapshot.setReturnCode(String.valueOf(response.code()));
            taskSnapshot.setMessage(response.body().string());
            log.info("# HTTP_TASK. id:{}, {}", taskSnapshot.getId(), taskSnapshot.getMessage());
        } catch (Exception e) {
            if (taskSnapshot.getRemainNum() > 0) {
                log.error("# RETRY_FAIL. id:{}", taskSnapshot.getId(), e);
            } else {
                log.error("# TASK_WILL_RETRY. id:{}", taskSnapshot.getId(), e);
            }
            return false;
        }

        return true;
    }

    @Override
    public boolean end(PTaskSnapshot taskSnapshot, String sessionId) throws PException {
        return true;
    }

    private Request buildRequest(PTaskSnapshot taskSnapshot) throws Exception {
        Request request;
        HashMap<String, String> params = taskSnapshot.getParams();
        if (HttpMethod.GET.matches(params.remove("method"))) {
            request = new Request.Builder().addHeader("content-type", "application/json")
                    .url(taskSnapshot.getUrl()).get().build();
            return request;
        }

        request = new Request.Builder()
                .addHeader("content-type", "application/json")
                .url(taskSnapshot.getUrl())
                .post(RequestBody.create(JsonUtil.write(params), MediaType.parse("application/json; charset=utf-8")))
                .build();
        return request;
    }

}
