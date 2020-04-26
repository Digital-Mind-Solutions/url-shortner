package org.digitalmind.urlshortner.service;

import org.digitalmind.urlshortner.dto.Redirect;
import org.digitalmind.urlshortner.entity.UrlShortner;

public interface UrlShortnerService {

    Redirect redirectUrl(String shortUrl);

    UrlShortner getUrl(String shortUrl);

    public UrlShortner createUrl(String longUrl, int httpStatus, int ttlSeconds, int parts, int iterationMin, int iterationMax);

    void deleteExpiredUrls();
}
