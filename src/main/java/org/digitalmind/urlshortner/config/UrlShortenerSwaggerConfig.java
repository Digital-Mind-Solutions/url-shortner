package org.digitalmind.urlshortner.config;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.digitalmind.urlshortner.config.UrlShortenerModuleConfig.*;
import static springfox.documentation.builders.PathSelectors.regex;
import static springfox.documentation.schema.AlternateTypeRules.newRule;

@Configuration
@EnableSwagger2
@ConditionalOnProperty(name = API_ENABLED, havingValue = "true")
public class UrlShortenerSwaggerConfig {

    private final TypeResolver typeResolver;
    private final ServletContext servletContext;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class ApiSwaggerProperties {
        private ApiSwaggerDocketProperties docket;
        private ApiSwaggerInfoProperties info;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @ToString
        public static class ApiSwaggerDocketProperties {
            private String host;
            private String basePath;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @ToString
        public static class ApiSwaggerInfoProperties {
            private String groupName;
            private String title;
            private String description;
            private String version;
            private ApiSwaggerContactProperties contact;
            private String license;
            private String licenseUrl;

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            @ToString
            public static class ApiSwaggerContactProperties {
                private String name;
                private String url;
                private String email;
            }
        }
    }

    @Autowired
    public UrlShortenerSwaggerConfig(
            TypeResolver typeResolver, ServletContext servletContext
    ) {
        this.typeResolver = typeResolver;
        this.servletContext = servletContext;
    }

    @Bean(PREFIX + "ApiSwaggerProperties")
    @ConfigurationProperties(prefix = PREFIX + ".api")
    public ApiSwaggerProperties apiProperties() {
        return new ApiSwaggerProperties();
    }

    @Bean(PREFIX + "Docket")
    public Docket api(
            ApiSwaggerProperties apiSwaggerProperties
    ) {

        List<SecurityScheme> schemeList = new ArrayList<>();
        List<VendorExtension> vendorExtensions = new ArrayList<>();
        schemeList.add(new ApiKey("ApiKey", "x-api-key", "header"));

        return new Docket(DocumentationType.SWAGGER_2)
                .host(apiSwaggerProperties.getDocket().getHost())
                .pathProvider(new RelativePathProvider(servletContext) {
                    @Override
                    public String getApplicationBasePath() {
                        return apiSwaggerProperties.getDocket().getBasePath();
                    }
                })
                .groupName(apiSwaggerProperties.getInfo().getGroupName())
                .select()
                .apis(RequestHandlerSelectors.basePackage(API_PACKAGE))
                .paths(apiPaths(apiSwaggerProperties))
                .build()
                .genericModelSubstitutes(ResponseEntity.class)
                .alternateTypeRules(
                        newRule(
                                typeResolver.resolve(CompletableFuture.class,
                                        typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
                                typeResolver.resolve(WildcardType.class)
                        )
                )

                .apiInfo(apiInfo(apiSwaggerProperties))
                .securitySchemes(schemeList)
                .useDefaultResponseMessages(false)
                //.forCodeGeneration(true)
                ;


    }

    private Predicate<String> apiPaths(ApiSwaggerProperties apiSwaggerProperties) {
        return (Predicate<String>) regex(".*" + apiSwaggerProperties.getDocket().getBasePath() + ".*");
    }

    private ApiInfo apiInfo(ApiSwaggerProperties apiSwaggerProperties) {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title(apiSwaggerProperties.getInfo().getTitle())
                .description(apiSwaggerProperties.getInfo().getDescription())
                .version(apiSwaggerProperties.getInfo().getVersion())
                .contact(new Contact(apiSwaggerProperties.getInfo().getContact().getName(), apiSwaggerProperties.getInfo().getContact().getUrl(), apiSwaggerProperties.getInfo().getContact().getEmail()))
                .license(apiSwaggerProperties.getInfo().license)
                .licenseUrl(apiSwaggerProperties.getInfo().licenseUrl)
                .build();
        return apiInfo;
    }

    @Bean(PREFIX + "UiConfiguration")
    @ConditionalOnProperty(name = API_UI_ENABLED, havingValue = "true")
    UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .deepLinking(true)
                .displayOperationId(false)
                .defaultModelsExpandDepth(1)
                .defaultModelExpandDepth(1)
                .defaultModelRendering(ModelRendering.EXAMPLE)
                .displayRequestDuration(false)
                .docExpansion(DocExpansion.NONE)
                .filter(false)
                .maxDisplayedTags(null)
                .operationsSorter(OperationsSorter.ALPHA)
                .showExtensions(true)
                .tagsSorter(TagsSorter.ALPHA)
                .supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS)
                .validatorUrl(null)
                .build();
    }

}

