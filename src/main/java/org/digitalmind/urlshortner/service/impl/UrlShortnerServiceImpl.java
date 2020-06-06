package org.digitalmind.urlshortner.service.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.digitalmind.urlshortner.config.UrlShortenerConfig;
import org.digitalmind.urlshortner.dto.Redirect;
import org.digitalmind.urlshortner.entity.UrlShortner;
import org.digitalmind.urlshortner.exception.UrlShortnerException;
import org.digitalmind.urlshortner.repository.UrlShortnerRepository;
import org.digitalmind.urlshortner.service.UrlShortnerService;
import org.digitalmind.urlshortner.util.UrlShortnerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

import static org.digitalmind.urlshortner.config.UrlShortenerModuleConfig.ENABLED;

@Slf4j
@Service
@ConditionalOnProperty(name = ENABLED, havingValue = "true")
@Transactional
public class UrlShortnerServiceImpl implements UrlShortnerService {

    private final UrlShortnerRepository urlShortnerRepository;
    private final UrlShortenerConfig config;
    private final LoadingCache<String, UrlShortner> cacheUrl;
    private final CacheLoader<String, UrlShortner> loaderUrl;

    @Autowired
    public UrlShortnerServiceImpl(
            UrlShortnerRepository urlShortnerRepository,
            UrlShortenerConfig config
    ) {
        this.urlShortnerRepository = urlShortnerRepository;
        this.config = config;
        this.loaderUrl = new CacheLoader<String, UrlShortner>() {
            @Override
            public UrlShortner load(String urlShort) {
                return urlShortnerRepository.getByShortUrl(urlShort);
            }
        };
        this.cacheUrl = CacheBuilder.from(config.getCacheSpecification()).build(loaderUrl);
    }

    @Override
    public Redirect redirectUrl(String shortUrl) {
        Redirect redirect = config.getUrls().getNotFound();
        try {
            if (UrlShortnerUtil.isValidUrlShortToken(shortUrl)) {
                UrlShortner urlShortner = cacheUrl.get(shortUrl);
                if (urlShortner != null) {
                    redirect = Redirect.builder()
                            .location(urlShortner.getLongUrl())
                            .httpStatus(urlShortner.getHttpStatus())
                            .build();
                }
            }
        } catch (CacheLoader.InvalidCacheLoadException e) {
            redirect = config.getUrls().getNotFound();
        } catch (Exception e) {
            redirect = config.getUrls().getUnavailable();
        }
        return redirect;
    }

    @Override
    public UrlShortner getUrl(String shortUrl) {
        return null;
    }

    @Override
    public UrlShortner createUrl(String longUrl, int httpStatus, int ttlSeconds, int parts, int iterationMin, int iterationMax) {
        UrlShortner urlShortner = null;
        if (!(iterationMin <= iterationMax)) {
            throw new UrlShortnerException("IterationMin " + iterationMin + " must be lower than iterationMax " + iterationMax);
        }
        int iteration = iterationMin;
        String shortUrl;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, ttlSeconds);

        while (urlShortner == null && iteration <= iterationMax)
            try {
                shortUrl = UrlShortnerUtil.getUrlShortToken(longUrl, iteration, parts);
                if (!urlShortnerRepository.existsByShortUrl(shortUrl)) {
                    UrlShortner urlShortnerCreate =
                            UrlShortner.builder()
                                    .longUrl(longUrl)
                                    .shortUrl(shortUrl)
                                    .httpStatus(httpStatus)
                                    .iteration(iteration)
                                    .expirationDate(calendar.getTime())
                                    .build();
                    urlShortner = saveUrlShortner(urlShortnerCreate);
                }
            } catch (Exception e) {
            } finally {
                if (iteration >= iterationMax && urlShortner == null) {
                    throw new UrlShortnerException("Unable to shorten link using iterations between " + iterationMin + " and " + iterationMax);
                }
                iteration = iteration + 1;
            }
        return urlShortner;
    }

    @Override
    public void deleteExpiredUrls() {
        try {
            log.debug("Cleaning up shorturl table");
            urlShortnerRepository.deleteByExpirationDateBefore(new Date());
        } catch (Exception e) {
            log.error("Exception encountered while cleaning up the shorturl table", e);
        }
    }

    //@Transactional(dontRollbackOn = DataIntegrityViolationException.class)
    //@Transactional(Transactional.TxType.REQUIRES_NEW)
    public UrlShortner saveUrlShortner(UrlShortner urlShortnerCreate) {
        return urlShortnerRepository.save(urlShortnerCreate);
    }

}
