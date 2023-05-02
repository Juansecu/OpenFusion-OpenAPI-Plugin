package com.juansecu.openfusion.openfusionopenapiplugin.shared.utils;

import java.time.Duration;

public final class TimeUtil {
    private TimeUtil() {}

    public static long secondsToMilliseconds(final long seconds) {
        return Duration.ofSeconds(seconds).toMillis();
    }
}
