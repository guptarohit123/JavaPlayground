package org.rgupta.concurrency.practice.threadsafeboundedqueue;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBlockingQueue<T> {

	private final Queue<T> queue;
	private final Semaphore availableSlots;
	private final Semaphore availableItems;
	private final ReentrantLock queueLock;

	public BoundedBlockingQueue(int capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException("Illegal capacity " + capacity);
		}
		this.queue = new ArrayDeque<>(capacity);
		this.availableSlots = new Semaphore(capacity);
		this.availableItems = new Semaphore(0);
		this.queueLock = new ReentrantLock();
	}

	public void put(T item) throws InterruptedException {
		Objects.requireNonNull(item, "item");
		boolean added = false;
		this.availableSlots.acquire();
		this.queueLock.lock(); // decided not to use lockInterruptibly for simplicity
		try {
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
		this.availableItems.acquire();
		T item;
		this.queueLock.lock();
		try {
			item = this.queue.remove();
		} finally {
			this.queueLock.unlock();
		}
		this.availableSlots.release();
		return item;
	}

	public int size() {
		this.queueLock.lock();
		try {
			return this.queue.size();
		} finally {
			this.queueLock.unlock();
		}
	}

}
