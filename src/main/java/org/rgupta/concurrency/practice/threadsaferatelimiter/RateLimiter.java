package org.rgupta.concurrency.practice.threadsaferatelimiter;

public interface RateLimiter {

    /**
     * Returns true if the request is allowed.
     * Returns false if the rate limit is exceeded.
     */
    boolean allowRequest(String clientId);
}
