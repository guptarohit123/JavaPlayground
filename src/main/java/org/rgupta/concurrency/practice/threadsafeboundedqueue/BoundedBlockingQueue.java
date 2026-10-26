package org.rgupta.concurrency.practice.threadsafeboundedqueue;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Requirements
 * 1. Bounded capacity: The queue cannot contain more than capacity elements.
 * 2. Blocking producers: If the queue is full, put() must wait until a consumer removes an element.
 * 3. Blocking consumers: If the queue is empty, take() must wait until a producer inserts an element.
 * 4. FIFO ordering: Items must be retrieved in insertion order.
 * 5. Thread safety: Multiple producers and consumers can operate concurrently without corrupting the queue or losing or duplicating items.
 * 6. Interruption: A blocked put() or take() must respond correctly to thread interruption.
 * 7. size(): Must return a thread-safe snapshot of the current number of elements.
 * 8. Input validation: Capacity must be positive; null elements are not permitted.
 * 9. No busy-waiting: Threads must not repeatedly poll the queue while waiting.
 * 10. Restrictions: You may use standard Java concurrency primitives and ordinary collections, but not an existing BlockingQueue implementation.
 */
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
		this.queueLock.lock();
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
		try {
			this.queueLock.lock();
			return this.queue.size();
		} finally {
			this.queueLock.unlock();
		}
	}

}
