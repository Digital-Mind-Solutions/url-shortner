package org.digitalmind.urlshortner.config;

import org.digitalmind.urlshortner.service.UrlShortnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.digitalmind.urlshortner.config.UrlShortenerModuleConfig.*;

@Configuration
@EnableAsync
@EnableConfigurationProperties
@ConditionalOnProperty(name = UrlShortenerModuleConfig.ENABLED, havingValue = "true")
public class UrlShortnerAsyncConfig {

    private final UrlShortenerConfig config;

    @Autowired
    public UrlShortnerAsyncConfig(UrlShortenerConfig config) {
        this.config = config;
    }

    @Bean(SCHEDULED_EXECUTOR)
    public ScheduledExecutorService taskScheduledExecutorService(
            UrlShortnerService urlShortnerService
    ) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        Random rand = new Random();
        executorService.scheduleAtFixedRate(() -> {
            urlShortnerService.deleteExpiredUrls();
        }, rand.nextInt(config.getCleanupInterval()), config.getCleanupInterval(), TimeUnit.SECONDS);
        return executorService;
    }

    @Bean(ASYNC_CONTROLLER_NORTHBOUND)
    public Executor taskExecutorControllerNorthbound() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.getAsync().getControllerNorthbound().getCorePoolSize());
        executor.setMaxPoolSize(config.getAsync().getControllerNorthbound().getMaxPoolSize());
        executor.setQueueCapacity(config.getAsync().getControllerNorthbound().getQueueCapacity());
        executor.setThreadNamePrefix(config.getAsync().getControllerNorthbound().getThreadNamePrefix());
        executor.initialize();
        return executor;
    }

    @Bean(ASYNC_CONTROLLER_SOUTHBOUND)
    public Executor taskExecutorControllerSouthbound() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.getAsync().getControllerSouthbound().getCorePoolSize());
        executor.setMaxPoolSize(config.getAsync().getControllerSouthbound().getMaxPoolSize());
        executor.setQueueCapacity(config.getAsync().getControllerSouthbound().getQueueCapacity());
        executor.setThreadNamePrefix(config.getAsync().getControllerSouthbound().getThreadNamePrefix());
        executor.initialize();
        return executor;
    }

}
