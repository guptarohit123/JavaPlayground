package org.rgupta.concurrency.practice.threadsaferatelimiter;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class TestClock extends Clock {

    private final AtomicLong time;
    private final ZoneId zone;

    public TestClock() {
        this(new AtomicLong(), ZoneOffset.UTC);
    }

    private TestClock(AtomicLong time, ZoneId zone) {
        this.time = time;
        this.zone = Objects.requireNonNull(zone);
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId newZone) {
        return new TestClock(time, newZone);
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
