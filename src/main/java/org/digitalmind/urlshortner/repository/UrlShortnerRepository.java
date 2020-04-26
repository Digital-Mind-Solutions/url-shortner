package org.digitalmind.urlshortner.repository;

import org.digitalmind.urlshortner.entity.UrlShortner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Date;

import static org.digitalmind.urlshortner.config.UrlShortenerModuleConfig.ENABLED;

@Repository
@ConditionalOnProperty(name = ENABLED, havingValue = "true")
public interface UrlShortnerRepository extends JpaRepository<UrlShortner, Long> {

    UrlShortner getByShortUrl(String shortUrl);

    boolean existsByShortUrl (String shortUrl);

    @Modifying
    void deleteByExpirationDateBefore(Date expirationDate);

}
