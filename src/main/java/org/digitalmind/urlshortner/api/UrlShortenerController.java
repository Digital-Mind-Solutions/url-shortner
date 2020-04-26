package org.digitalmind.urlshortner.api;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.digitalmind.urlshortner.dto.Redirect;
import org.digitalmind.urlshortner.dto.UrlShortnerCreateDTO;
import org.digitalmind.urlshortner.entity.UrlShortner;
import org.digitalmind.urlshortner.service.UrlShortnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

import static org.digitalmind.urlshortner.config.UrlShortenerModuleConfig.*;

@Slf4j
@RestController
@ConditionalOnProperty(name = API_ENABLED, havingValue = "true")
@RequestMapping("${" + PREFIX + ".api.docket.base-path}/url-shortner")
@Api(value = "Template", description = "This resource is exposing the services for template support", tags = {"Template"})
public class UrlShortenerController {

    private final UrlShortnerService urlShortnerService;

    @Autowired
    public UrlShortenerController(UrlShortnerService urlShortnerService) {
        this.urlShortnerService = urlShortnerService;
    }

    //ACCESS SHORT URL
    @ApiOperation(
            value = "Access by short url",
            notes = "This API is used for accessing real url by short url.",
            response = Redirect.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 302, message = "Request executed with success"),
            @ApiResponse(code = 500, message = "Error encountered when executing request")
    })
    @GetMapping(path = "/redirect/{short-url}")
    @Async(ASYNC_CONTROLLER_NORTHBOUND)
    public CompletableFuture<ResponseEntity<Redirect>> accessUrl(
            @ApiParam(name = "short-url", value = "The short url", required = true, allowMultiple = false) @PathVariable(name = "short-url", required = true) String shortUrl,
            @ApiParam(name = "info", value = "Parameter indicate to provide redirect info", required = false, allowMultiple = false, defaultValue = "false") @RequestParam(name = "info", required = false, defaultValue = "false") Boolean info
    ) {
        CompletableFuture<Redirect> completableFutureUrl = CompletableFuture.completedFuture(urlShortnerService.redirectUrl(shortUrl));

        return completableFutureUrl
                .thenApply(redirect -> {
                            if (info) {
                                return ResponseEntity
                                        .status(HttpStatus.OK)
                                        .body(redirect);
                            } else {
                                return ResponseEntity
                                        .status(redirect.getHttpStatus())
                                        .header(HttpHeaders.LOCATION, redirect.getLocation())
                                        .build();
                            }
                        }
                );
    }

    //CREATE SHORT URL
    @ApiOperation(
            value = "Create short url",
            notes = "This API is used for creating a new short url.",
            response = UrlShortner.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Operation success"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 409, message = "Conflict"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 500, message = "Error encountered when processing request")
    })
    @PostMapping(path = "/", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    @Async(ASYNC_CONTROLLER_SOUTHBOUND)
    public CompletableFuture<ResponseEntity<UrlShortner>> createUrlShortner(
            @ApiParam(name = "request", value = "The url shortner request", required = true, allowMultiple = false) @Valid @RequestBody UrlShortnerCreateDTO request
    ) {

        CompletableFuture<UrlShortner> urlShortnerCompletableFuture =
                CompletableFuture
                        .supplyAsync(() -> urlShortnerService.createUrl(request.getLongUrl(), request.getHttpStatus(), request.getTtlSeconds(), request.getParts(), request.getIterationMin(), request.getIterationMax()));
        return urlShortnerCompletableFuture
                .thenApply(urlShortner ->
                        ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(urlShortner)
                );
    }

}
