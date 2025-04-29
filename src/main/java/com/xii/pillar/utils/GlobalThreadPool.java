package com.xii.pillar.utils;

import com.google.common.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 全局线程池
 */
@Slf4j
public class GlobalThreadPool {

    private static final ExecutorService POOL = new ThreadPoolExecutor(
            5, 30,
            5L, TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("GlobalThreadPool-#%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    private static final ListeningExecutorService service = MoreExecutors.listeningDecorator(POOL);
    private GlobalThreadPool() {
    }

    public static ExecutorService getPool() {
        return POOL;
    }

    public static void execute(Runnable runnable) {
        POOL.execute(runnable);
    }

    public static void submit(Runnable runnable) {
        POOL.submit(runnable);
    }

    /**
     * 批量处理并获取结果
     *
     * @param params
     * @param batchFuture
     * @param <P>
     * @param <R>
     * @return
     */
    public static <P, R> List<R> batchAndGet(List<P> params, BatchFuture<P, R> batchFuture) {
        try {
            return batch(params, batchFuture).get();
        } catch (InterruptedException e) {
            log.error("process fail", e);
        } catch (ExecutionException e) {
            log.error("exec fail", e);
        }
        return new ArrayList<>();

    }

    /**
     * 批量处理
     *
     * @param params
     * @param batchFuture
     * @param <P>
     * @param <R>
     * @return
     */
    public static <P, R> ListenableFuture<List<R>> batch(List<P> params, BatchFuture<P, R> batchFuture) {
        if (params.isEmpty()) {
            return null;
        }

        List<ListenableFuture<R>> futures = new ArrayList<>();
        for (P param : params) {
            ListenableFuture<R> explosion = service.submit(new SimpleTask<>(param, batchFuture));
            Futures.addCallback(explosion, new FutureCallback<R>() {
                @Override
                public void onSuccess(R result) {
                    log.info("# BATCH_TASK_SUCCESS. result:{}", result);
                }
                @Override
                public void onFailure(Throwable t) {
                    log.error("# BATCH_TASK_FAIL ", t);
                }
            }, service);

            futures.add(explosion);
        }

        return Futures.successfulAsList(futures);
    }

    public static <P, R, E> ListenableFuture<List<R>> batch(List<P> params, BatchFutureExtend<P, R, E> batchFuture, E extend) {
        if (params.isEmpty()) {
            return null;
        }

        List<ListenableFuture<R>> futures = new ArrayList<>();
        for (P param : params) {
            ListenableFuture<R> explosion = service.submit(new SimpleTask<>(param, batchFuture, extend));
            Futures.addCallback(explosion, new FutureCallback<R>() {
                @Override
                public void onSuccess(R result) {
                    log.info("# BATCH_TASK_SUCCESS. result:{}", result);
                }
                @Override
                public void onFailure(Throwable t) {
                    log.error("# BATCH_TASK_FAIL ", t);
                }
            }, service);

            futures.add(explosion);
        }

        return Futures.successfulAsList(futures);
    }

    private static class SimpleTask<P, R, E> implements Callable<R> {
        private P param;
        private BatchFuture<P, R> batchFuture;

        private BatchFutureExtend<P, R, E> batchFutureExtend;
        private E extend;

        public SimpleTask(P param, BatchFuture<P, R> batchFuture){
            this.param = param;
            this.batchFuture = batchFuture;
        }

        public SimpleTask(P param, BatchFutureExtend<P, R, E> batchFutureExtend, E extend){
            this.extend = extend;
            this.param = param;
            this.batchFutureExtend = batchFutureExtend;
        }

        public E getExtend() {
            return extend;
        }

        @Override
        public R call() throws Exception {
            return batchFuture == null ? batchFutureExtend.callback(param, extend) : batchFuture.callback(param);
        }
    }

    @FunctionalInterface
    public interface BatchFuture<P, R>{
        R callback(P param);
    }

    @FunctionalInterface
    public interface BatchFutureExtend<P, R, E>{
        R callback(P param, E extend);
    }

}
