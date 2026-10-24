package org.rgupta.concurrency.practice.threadsaferatelimiter;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicLong;

public class TestClock extends Clock {

    AtomicLong time = new AtomicLong();

    @Override
    public ZoneId getZone() {
        return null;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return null;
    }

    @Override
    public Instant instant() {
        return null;
    }

    @Override
    public long millis() {
        return time.get();
    }

    public void setTime(long time) {
        this.time.set(time);
    }
}
