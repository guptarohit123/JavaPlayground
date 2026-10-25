package org.rgupta.concurrency.practice.threadsaferatelimiter;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicLong;

public class TestClock extends Clock {

    private final AtomicLong time = new AtomicLong();

    @Override
    public ZoneId getZone() {
        return ZoneId.systemDefault();
    }

    @Override
    public Clock withZone(ZoneId ignored) {
        return this;
    }

    @Override
    public Instant instant() {
        return Instant.ofEpochMilli(time.get());
    }

    @Override
    public long millis() {
        return time.get();
    }

    public void setTime(long time) {
        this.time.set(time);
    }
}
