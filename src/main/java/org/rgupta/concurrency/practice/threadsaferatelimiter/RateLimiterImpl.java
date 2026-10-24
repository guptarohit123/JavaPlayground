package org.rgupta.concurrency.practice.threadsaferatelimiter;

import java.time.Clock;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RateLimiterImpl implements RateLimiter {

    public static final long INTERVAL = TimeUnit.SECONDS.toMillis(60);
    public static final int LIMIT = 5;
    private final Clock clock;
    private final Map<String, Deque<Long>> userRequests;

    public RateLimiterImpl(Clock clock) {
        this.clock = clock;
        this.userRequests = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allowRequest(String clientId) {
        boolean[] res = new boolean[1];
        userRequests.compute(clientId, (key, val) -> {
            if (val == null) {
                val = new ArrayDeque<>();
            }
            long now = clock.millis();
            while (!val.isEmpty() && now - val.peekFirst() >= INTERVAL) {
                val.pollFirst();
            }
            res[0] = val.size() < LIMIT;
            if(res[0]) {
                val.addLast(now);
            }
            return val;
        });
        return res[0];
    }
}
