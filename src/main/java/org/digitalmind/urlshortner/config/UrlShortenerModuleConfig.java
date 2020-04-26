package org.digitalmind.urlshortner.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({
        UrlShortenerModuleConfig.SERVICE_PACKAGE,
        UrlShortenerModuleConfig.API_PACKAGE
})
@EnableCaching

@ConditionalOnProperty(name = UrlShortenerModuleConfig.ENABLED, havingValue = "true")
@Slf4j
public class UrlShortenerModuleConfig {
    public static final String MODULE = "urlshortner";
    public static final String PREFIX = "application.modules.common." + MODULE;
    public static final String ENABLED = PREFIX + ".enabled";
    public static final String API_ENABLED = PREFIX + ".api.enabled";
    public static final String API_UI_ENABLED = PREFIX + ".api.ui-enabled";

    public static final String ROOT_PACKAGE = "corg.digitalmind." + MODULE;
    public static final String CONFIG_PACKAGE = ROOT_PACKAGE + ".config";
    public static final String ENTITY_PACKAGE = ROOT_PACKAGE + ".entity";
    public static final String REPOSITORY_PACKAGE = ROOT_PACKAGE + ".repository";
    public static final String SERVICE_PACKAGE = ROOT_PACKAGE + ".service";
    public static final String API_PACKAGE = ROOT_PACKAGE + ".api";

    public static final String ASYNC_CONTROLLER_NORTHBOUND = PREFIX + ".async.controller.northbound";
    public static final String ASYNC_CONTROLLER_SOUTHBOUND = PREFIX + ".async.controller.southbound";
    public static final String ASYNC_SERVICE = PREFIX + ".async.service";
    public static final String ASYNC_REPOSITORY = PREFIX + ".async.repository";

    public static final String SCHEDULED_EXECUTOR = PREFIX + ".scheduled.executor";

}
