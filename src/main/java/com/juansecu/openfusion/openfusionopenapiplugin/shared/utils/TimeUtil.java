package com.juansecu.openfusion.openfusionopenapiplugin.shared.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public final class TimeUtil {
    private TimeUtil() {}

    public static long localDateTimeToSeconds(final LocalDateTime localDateTime) {
        return TimeUtil.millisecondsToSeconds(
            Date
                .from(
                    localDateTime
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                )
                .getTime()
        );
    }

    public static long millisecondsToSeconds(final long milliseconds) {
        return Duration.ofMillis(milliseconds).getSeconds();
    }

    public static long secondsToMilliseconds(final long seconds) {
        return Duration.ofSeconds(seconds).toMillis();
    }
}
