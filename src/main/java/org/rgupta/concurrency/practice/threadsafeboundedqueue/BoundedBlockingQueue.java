package org.rgupta.concurrency.practice.threadsafeboundedqueue;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBlockingQueue<T> {


    private final Queue<T> queue;
    private final Semaphore availableSlots;
    private final Semaphore availableItems;
    private final ReentrantLock queueLock;

    public BoundedBlockingQueue(int capacity) {
        this.queue = new ArrayDeque<>(capacity);
        this.availableSlots = new Semaphore(capacity);
        this.availableItems = new Semaphore(0);
        this.queueLock = new ReentrantLock();

    }

    public void put(T item) throws InterruptedException {
        boolean added = false;
        try {
            this.availableSlots.acquire();
            this.queueLock.lock();
            added = this.queue.offer(item);
        } finally {
            this.queueLock.unlock();
            if (!added) {
                this.availableSlots.release();
            } else {
                this.availableItems.release();
            }
        }
    }

    public T take() throws InterruptedException {
        try {
            this.availableItems.acquire();
            this.queueLock.lock();
            return this.queue.poll(); // this will not fail, so I do not need to release the acquired availableItems permit
        } finally {
            this.queueLock.unlock();
            this.availableSlots.release();
        }
    }

    public int size() {
        return availableItems.availablePermits();
    }

}
