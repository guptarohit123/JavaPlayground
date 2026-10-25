package org.rgupta.concurrency.practice.threadsaferatelimiter;

import java.time.Clock;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RateLimiterImpl implements RateLimiter {

    public static final long WINDOW_MILLIS = TimeUnit.SECONDS.toMillis(60);
    public static final int MAX_REQUESTS_PER_WINDOW = 5;
    private final Clock clock;
    private final Map<String, Deque<Long>> requestTimestampsByClient;

    public RateLimiterImpl(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock");
        this.requestTimestampsByClient = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allowRequest(String clientId) {
        boolean[] allowed = new boolean[1];
        requestTimestampsByClient.compute(clientId, (ignoredClientId, timestamps) -> {
            if (timestamps == null) {
                timestamps = new ArrayDeque<>();
            }
            long nowMillis = clock.millis();
            while (!timestamps.isEmpty() && nowMillis - timestamps.peekFirst() >= WINDOW_MILLIS) {
                timestamps.pollFirst();
            }
            allowed[0] = timestamps.size() < MAX_REQUESTS_PER_WINDOW;
            if (allowed[0]) {
                timestamps.addLast(nowMillis);
            }
            return timestamps;
        });
        return allowed[0];
    }
}
