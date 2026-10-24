package org.rgupta.concurrency.practice.threadsaferatelimiter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class RateLimiterImplTest {

    RateLimiter rateLimiter;
    TestClock clock;

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
        Assertions.assertFalse(rateLimiter.allowRequest("clientId1") , "limit is exceeded");
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
        assertTrue(rateLimiter.allowRequest("clientId1") , "first attempt expired");
        Assertions.assertFalse(rateLimiter.allowRequest("clientId1"));

    }

    @Test
    void concurrentUserRequestsAccepted() {
        Thread first = new Thread(createPopulationRunnable("client1" , 5 , false));
        Thread second = new Thread(createPopulationRunnable("client2" , 5 , false));

        first.start();
        try {
            second.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private Runnable createPopulationRunnable(String clientId, int numRequests , boolean rejectionCase) {

        return () -> {
            long time = 10*1000;
            for (int i = 0; i < numRequests; i++) {
                clock.setTime(time);
                time += 1000;
               assertTrue(rateLimiter.allowRequest(clientId));
            }
            if(rejectionCase) {
                time += 1000;
                assertFalse(rateLimiter.allowRequest(clientId), "tail should exceed limit");
            }
        };
    }

    @Test
    void concurrentUserRequestsRejected() {
        Thread first = new Thread(createPopulationRunnable("client1" , 5 , true));
        Thread second = new Thread(createPopulationRunnable("client2" , 5, true));

        first.start();
        try {
            second.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}