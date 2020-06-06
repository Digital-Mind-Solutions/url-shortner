package org.digitalmind.urlshortner.util;

import com.google.common.hash.Hashing;
import org.springframework.util.Assert;

import java.nio.charset.Charset;

public class UrlShortnerUtil {

    public static String SHORT_URL_PREFIX = "0";
    public static int CHECK_SUM_LENGTH = 2;
    public static int SHORT_URL_PREFIX_LENGTH = SHORT_URL_PREFIX.length();

    public static int MIN_PARTS = 1;
    public static int MAX_PARTS = 5;

    protected UrlShortnerUtil() {
    }

    private static String getChecksum(String token) {
        String checkSum = ("00000000" + Long.toHexString(token.chars().asLongStream().sum()));
        return checkSum.substring(checkSum.length() - CHECK_SUM_LENGTH);
    }

    private static int currentTimeMillisAsInt() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }

    private static String getStringPart(String string, int part, int parts) {
        int segmentLength = string.length() / parts;
        return string.substring(part * segmentLength, (part + 1) * segmentLength);
    }

    public static String getUrlShortToken(String longUrl, int iteration) {
        //String token = Hashing.murmur3_32().hashString(longUrl + String.valueOf(iteration), Charset.defaultCharset()).toString();
        //return token + getChecksum(token);

        return getUrlShortToken(longUrl, iteration, 1);
    }


    public static String getUrlShortToken(String longUrl, int iteration, int parts) {
        Assert.isTrue(parts >= MIN_PARTS, "The parts must be greater or equal than " + MIN_PARTS);
        Assert.isTrue(parts <= MAX_PARTS, "The parts must be equal or less than " + MAX_PARTS);
        String token = null;
        //int currentTimeMillis = currentTimeMillisAsInt();
        StringBuilder stringBuilder = new StringBuilder();
        for (int part = 0; part < parts; part++) {
            String longUrlPart = getStringPart(longUrl, part, parts);
            stringBuilder.append(
                    Hashing.murmur3_32(/*currentTimeMillis*/).hashString(longUrlPart + String.valueOf(iteration), Charset.defaultCharset()).toString()
            );
        }
        token = stringBuilder.toString();
        return SHORT_URL_PREFIX + token + getChecksum(token);
    }


    public static boolean isValidUrlShortToken(String shortUrl) {
        if (shortUrl == null || shortUrl.length() < SHORT_URL_PREFIX_LENGTH + CHECK_SUM_LENGTH) {
            return false;
        }
        String token = shortUrl.substring(SHORT_URL_PREFIX_LENGTH, shortUrl.length() - SHORT_URL_PREFIX_LENGTH - CHECK_SUM_LENGTH);
        String checkSum = shortUrl.substring(shortUrl.length() - CHECK_SUM_LENGTH);
        return checkSum.equalsIgnoreCase(getChecksum(token));
    }

}
