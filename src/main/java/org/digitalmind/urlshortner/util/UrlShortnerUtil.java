package org.digitalmind.urlshortner.util;

import com.google.common.hash.Hashing;
import org.springframework.util.Assert;

import java.nio.charset.Charset;

public class UrlShortnerUtil {

    public static int CHECK_SUM_LENGTH = 2;

    protected UrlShortnerUtil() {
    }

    private static String getChecksum(String value) {
        String checkSum = ("00000000" + Long.toHexString(value.chars().asLongStream().sum()));
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
        String token = Hashing.murmur3_32().hashString(longUrl + String.valueOf(iteration), Charset.defaultCharset()).toString();
        return token + getChecksum(token);
    }


    public static String getUrlShortToken(String longUrl, int parts, int iteration) {
        Assert.isTrue(parts > 0, "The parts must be greater than 0");
        Assert.isTrue(parts <= 10, "The parts must be less than 5");
        String token = null;
        int currentTimeMillis = currentTimeMillisAsInt();
        StringBuilder stringBuilder = new StringBuilder();
        for (int part = 0; part < parts; part++) {
            String longUrlPart = getStringPart(longUrl, part, parts);
            stringBuilder.append(
                    Hashing.murmur3_32(currentTimeMillis).hashString(longUrlPart + String.valueOf(iteration), Charset.defaultCharset()).toString()
            );
        }
        token = stringBuilder.toString();
        return token + getChecksum(token);
    }


    public static boolean isValidUrlShortToken(String shortUrl) {
        if (shortUrl == null || shortUrl.length() < CHECK_SUM_LENGTH) {
            return false;
        }
        String value = shortUrl.substring(0, shortUrl.length() - CHECK_SUM_LENGTH);
        String checkSum = shortUrl.substring(shortUrl.length() - CHECK_SUM_LENGTH);
        return checkSum.equalsIgnoreCase(getChecksum(value));
    }


}
