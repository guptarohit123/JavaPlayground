package org.rgupta.concurrency.practice.threadsafeboundedqueue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

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
                    () -> future.get(5, TimeUnit.SECONDS)
            );

            assertInstanceOf(
                    InterruptedException.class,
                    exception.getCause()
            );
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void shouldBeCorrectDuringConcurrentPutsAndTakes() throws InterruptedException, ExecutionException, TimeoutException {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        Set<Integer> itemsOffered = ConcurrentHashMap.newKeySet();
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
                        int item = items.incrementAndGet();
                        testQueue.put(item);
                        itemsOffered.add(item);
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

            for (Future<Integer> takeFuture : takeFutures) {
                assertTrue(itemsOffered.remove(takeFuture.get(5, TimeUnit.SECONDS)));
            }
        } finally {
            executorService.shutdownNow();
        }
    }

}