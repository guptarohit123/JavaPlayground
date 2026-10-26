package org.rgupta.concurrency.practice.threadsafeboundedqueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoundedBlockingQueueTest {

	private static final int CAPACITY = 5;
	private BoundedBlockingQueue<Integer> testQueue;

	@BeforeEach
	void setUp() {
		testQueue = new BoundedBlockingQueue<>(CAPACITY);
	}

	@Test
	void shouldAddItemsNonBlockingWhenWithinCapacity() throws InterruptedException {
		int item = 0;
		for (int i = 0; i < CAPACITY; i++) {
			testQueue.put(item++);
		}
		assertEquals(CAPACITY, testQueue.size(), "Test queue successfully populated");
	}

	@Test
	void shouldBlockOnTakeWhenQueueEmpty() throws InterruptedException, ExecutionException, TimeoutException {
		ExecutorService executorService = Executors.newFixedThreadPool(1);
		try {
			Future<Integer> takeItemFuture = executorService.submit(() -> {
				try {
					return testQueue.take();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			});
			assertFalse(takeItemFuture.isDone(), "Queue should block as it is empty");
			testQueue.put(12);
			assertEquals(12, takeItemFuture.get(5, TimeUnit.SECONDS), "Queue should unblock and return item");
		} finally {
			executorService.shutdownNow();
		}
	}

	@Test
	void shouldBlockOnPutWhenQueueFull() throws InterruptedException, ExecutionException, TimeoutException {
		int item = 0;
		for (int i = 0; i < CAPACITY; i++) {
			testQueue.put(item++);
		}
		ExecutorService executorService = Executors.newFixedThreadPool(1);
		try {
			Future<Boolean> future = executorService.submit(() -> {
				try {
					testQueue.put(10);
					return true;
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			});
			assertFalse(future.isDone(), "Queue should block as it is full");
			testQueue.take();
			assertTrue(future.get(5, TimeUnit.SECONDS), "Queue should unblock");
		} finally {
			executorService.shutdownNow();
		}
	}

	@Test
	void shouldPropagateInterruptionFromTake() throws Exception {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try {
			Future<Integer> future = executor.submit(() -> {
				Thread.currentThread().interrupt();
				return testQueue.take();
			});

			ExecutionException exception = assertThrows(
					ExecutionException.class,
					() -> future.get(5, TimeUnit.SECONDS));

			assertInstanceOf(
					InterruptedException.class,
					exception.getCause());
		} finally {
			executor.shutdownNow();
		}
	}

	@Test
	void putShouldRespondToInterrupts() throws InterruptedException, ExecutionException, TimeoutException {
		for (int i = 0; i < CAPACITY; i++) {
			testQueue.put(1);
		}
		AtomicReference<Thread> threadAtomicReference = new AtomicReference<>();
		CountDownLatch testReadyLatch = new CountDownLatch(1);
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		try {
			final Future<Boolean> putFuture = executorService.submit(() -> {
				threadAtomicReference.set(Thread.currentThread());
				testReadyLatch.countDown();
				try {
					testQueue.put(2);
					return false;
				} catch (InterruptedException e) {
					return true;
				}
			});
			assertTrue(testReadyLatch.await(5, TimeUnit.SECONDS));
			assertFalse(putFuture.isDone(), "Put should block as capacity is reached");
			assertThrows(TimeoutException.class, () -> putFuture.get(200, TimeUnit.MILLISECONDS));
			threadAtomicReference.get().interrupt();
			assertTrue(putFuture.get(5, TimeUnit.SECONDS));
			assertEquals(CAPACITY, testQueue.size());
		} finally {
			executorService.shutdownNow();
		}
	}

	@Test
	void takeShouldRespondToInterrupts() throws ExecutionException, InterruptedException, TimeoutException {
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		AtomicReference<Thread> threadAtomicReference = new AtomicReference<>();
		CountDownLatch testReadyLatch = new CountDownLatch(1);
		try {
			final Future<Boolean> takeFuture = executorService.submit(() -> {
				threadAtomicReference.set(Thread.currentThread());
				testReadyLatch.countDown();
				try {
					testQueue.take();
					return false;
				} catch (InterruptedException ex) {
					return true;
				}
			});
			assertTrue(testReadyLatch.await(5, TimeUnit.SECONDS));
			assertThrows(TimeoutException.class, () -> takeFuture.get(200, TimeUnit.MILLISECONDS));
			threadAtomicReference.get().interrupt();
			assertTrue(takeFuture.get(5, TimeUnit.SECONDS));
		} finally {
			executorService.shutdownNow();
		}

	}

	@Test
	void shouldHaveFifoBehavior() throws InterruptedException {
		testQueue.put(1);
		testQueue.put(2);
		testQueue.put(3);

		assertEquals(1, testQueue.take());
		assertEquals(2, testQueue.take());
		assertEquals(3, testQueue.take());
	}

	@Test
	void shouldBeCorrectDuringConcurrentPutsAndTakes() throws InterruptedException, ExecutionException, TimeoutException {
		ExecutorService executorService = Executors.newFixedThreadPool(20);
		try {
			AtomicInteger items = new AtomicInteger(0);
			List<Future<Boolean>> putFutures = new ArrayList<>();
			List<Future<Integer>> takeFutures = new ArrayList<>();
			CountDownLatch putLatch = new CountDownLatch(10);
			CountDownLatch takeLatch = new CountDownLatch(10);
			CountDownLatch readyLatch = new CountDownLatch(1);
			// put items
			for (int i = 0; i < 10; i++) {
				putFutures.add(executorService.submit(() -> {
					try {
						putLatch.countDown();
						readyLatch.await();
						testQueue.put(items.incrementAndGet());
						return true;
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}));

			}
			// take items
			for (int i = 0; i < 10; i++) {
				takeFutures.add(executorService.submit(() -> {
					try {
						takeLatch.countDown();
						readyLatch.await();
						return testQueue.take();
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}));
			}
			assertTrue(putLatch.await(5, TimeUnit.SECONDS));
			assertTrue(takeLatch.await(5, TimeUnit.SECONDS));
			readyLatch.countDown();
			for (Future<Boolean> putFuture : putFutures) {
				assertTrue(putFuture.get(5, TimeUnit.SECONDS));
			}
			Set<Integer> expectedItems = new HashSet<>();
			for (int i = 1; i <= items.get(); i++) {
				expectedItems.add(i);
			}
			for (Future<Integer> takeFuture : takeFutures) {
				final Integer item = takeFuture.get(1, TimeUnit.SECONDS);
				assertTrue(expectedItems.remove(item), "Expected ueue to contain item -" + item);
			}
		} finally {
			executorService.shutdownNow();
		}
	}

}