package org.zlycerqan.mirai.lizyn.core.executor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ExecutorManager {

    private final Executor pool;

    public ExecutorManager(int nThreads) {
        pool = Executors.newFixedThreadPool(nThreads);
    }

    public Executor getExecutor() {
        return pool;
    }

}
