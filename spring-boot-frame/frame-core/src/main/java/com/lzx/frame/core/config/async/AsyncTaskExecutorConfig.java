package com.lzx.frame.core.config.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 异步任务线程池配置
 */
@EnableAsync
@Configuration
@ConfigurationProperties(prefix = "async")
public class AsyncTaskExecutorConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncTaskExecutorConfig.class);

    private int corePoolSize;

    private int maxPoolSize;

    private int keepAliveSeconds;

    private int queueCapacity;

    private String threadNamePrefix;

    private boolean waitForTasksToCompleteOnShutdown;

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public String getThreadNamePrefix() {
        return threadNamePrefix;
    }

    public void setThreadNamePrefix(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }

    public boolean isWaitForTasksToCompleteOnShutdown() {
        return waitForTasksToCompleteOnShutdown;
    }

    public void setWaitForTasksToCompleteOnShutdown(boolean waitForTasksToCompleteOnShutdown) {
        this.waitForTasksToCompleteOnShutdown = waitForTasksToCompleteOnShutdown;
    }

    @Bean(value = "asyncTaskThreadPool")
    public Executor getAsyncExecutor() {
        //创建线程池对象
        ThreadPoolTaskExecutor asyncTaskThreadPool = new ThreadPoolTaskExecutor();
        //线程池维护线程的最少数量
        asyncTaskThreadPool.setCorePoolSize(corePoolSize);
        //线程池维护线程的最大数量
        asyncTaskThreadPool.setMaxPoolSize(maxPoolSize);
        //线程池维护线程所允许的空闲时间
        asyncTaskThreadPool.setKeepAliveSeconds(keepAliveSeconds);
        //线程池所使用的缓冲队列
        asyncTaskThreadPool.setQueueCapacity(queueCapacity);
        //配置线程池中的线程的名称前缀
        asyncTaskThreadPool.setThreadNamePrefix(threadNamePrefix);
        // 等待所有任务结束后再关闭线程池
        asyncTaskThreadPool.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
        //自定义任务处理策略,阻塞队列
        asyncTaskThreadPool.setRejectedExecutionHandler((runnable, executor) -> {
            if (!executor.isShutdown()) {
                try {
                    LOGGER.info("start get queue");
                    executor.getQueue().put(runnable);
                    LOGGER.info("end get queue");
                } catch (InterruptedException e) {
                    LOGGER.error(e.toString(), e);
                    Thread.currentThread().interrupt();
                }
            }
        });
        //初始化
        asyncTaskThreadPool.initialize();
        //返回异步线程池
        return asyncTaskThreadPool;
    }

}
