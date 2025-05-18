package com.xii.pillar.service.config;

import com.xii.pillar.service.FlowSessionManager;
import com.xii.pillar.service.TaskDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
//@Configuration
public class SchedulerService {

    private static ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Autowired
    private TaskDispatcher taskDispatcher;
    @Autowired
    private FlowSessionManager flowSessionManager;

    @Bean
    public ScheduledExecutorService scheduleWorker() {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.scheduleAtFixedRate(() -> {

            dispatchNodePath();

        }, 3, 5, TimeUnit.SECONDS);
        return service;
    }

    private void dispatchNodePath() {
        executorService.submit(() -> {
            try {
                log.info("dispatchNodePath start");
                taskDispatcher.scanPath();

                log.info("scanSession start");
                flowSessionManager.scanSession();
            } catch (Throwable e) {
                log.error("dispatchNodePath error", e);
            }

        });
    }

}
