package org.digitalmind.urlshortner.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.digitalmind.urlshortner.dto.Redirect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static org.digitalmind.urlshortner.config.UrlShortenerModuleConfig.ENABLED;
import static org.digitalmind.urlshortner.config.UrlShortenerModuleConfig.PREFIX;


@Configuration
@ConditionalOnProperty(name = ENABLED, havingValue = "true")
@ConfigurationProperties(prefix = PREFIX)
@EnableConfigurationProperties
@Getter
@Setter
public class UrlShortenerConfig {
    private boolean enabled;
    private String cacheSpecification;
    private int cleanupInterval;
    private AsyncDefinitions async;
    private UrlDefinition urls;

    @NoArgsConstructor
    @Getter
    @Setter
    public static class UrlDefinition {
        private Redirect notFound;
        private Redirect unavailable;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class AsyncDefinitions {
        private AsyncDefinition controllerNorthbound;
        private AsyncDefinition controllerSouthbound;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    public static class AsyncDefinition {
        private int corePoolSize;
        private int maxPoolSize;
        private int queueCapacity;
        private String threadNamePrefix;
    }

}
