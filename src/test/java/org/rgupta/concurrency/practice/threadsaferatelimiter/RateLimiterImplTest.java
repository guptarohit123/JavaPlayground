package org.rgupta.concurrency.practice.threadsaferatelimiter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class RateLimiterImplTest {

    private RateLimiter rateLimiter;
    private TestClock clock;

    @BeforeEach
    public void setup() {
        clock = new TestClock();
        rateLimiter = new RateLimiterImpl(clock);
    }

    @Test
    void requestsAreAccepted() {
        clock.setTime(10);
        assertTrue(rateLimiter.allowRequest("clientId1"));
        assertTrue(rateLimiter.allowRequest("clientId2"));
    }

    @Test
    void requestsRejectedWhenLimitBreached() {
        long time = 10;
        for (int i = 0; i < 5; i++) {
            clock.setTime(time++);
            assertTrue(rateLimiter.allowRequest("clientId1"));
        }
        Assertions.assertFalse(rateLimiter.allowRequest("clientId1"), "limit is exceeded");
    }

    @Test
    void expiredTimeStampsRemoved() {
        long time = 10;
        for (int i = 0; i < 5; i++) {
            clock.setTime(time++);
            assertTrue(rateLimiter.allowRequest("clientId1"));
        }
        //10 11 12 13 14 15
        //70
        clock.setTime(TimeUnit.SECONDS.toMillis(60) + 10);
        assertTrue(rateLimiter.allowRequest("clientId1"), "first attempt expired");
        Assertions.assertFalse(rateLimiter.allowRequest("clientId1"));

    }

    @Test
    void concurrentUserRequests() throws InterruptedException, ExecutionException, TimeoutException {
        int numThreads = 20;
        CountDownLatch readyLatch = new CountDownLatch(numThreads);
        CountDownLatch startLatch = new CountDownLatch(1);

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        try {

            List<Future<Boolean>> rateLimiterResults = new ArrayList<>();
            for (int i = 0; i < numThreads; i++) {
                Future<Boolean> future = executorService.submit(() ->
                {
                    readyLatch.countDown();
                    startLatch.await();
                    return rateLimiter.allowRequest("client1");
                });
                rateLimiterResults.add(future);
            }
            assertTrue(readyLatch.await(5, TimeUnit.SECONDS), "workers did not became ready");
            startLatch.countDown();
            int accepted = 0;
            for (Future<Boolean> res : rateLimiterResults) {
                if (res.get(1, TimeUnit.SECONDS)) {
                    accepted++;
                }
            }
            assertEquals(5, accepted);
        } finally {
            startLatch.countDown();
            executorService.shutdownNow();
        }
    }
}