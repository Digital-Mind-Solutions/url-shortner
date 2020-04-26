package org.digitalmind.urlshortner.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class UrlShortnerException extends RuntimeException {

    public UrlShortnerException() {
    }

    public UrlShortnerException(String message) {
        super(message);
    }

    public UrlShortnerException(String message, Throwable cause) {
        super(message, cause);
    }

    public UrlShortnerException(Throwable cause) {
        super(cause);
    }

    public UrlShortnerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
