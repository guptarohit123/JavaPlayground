
 Requirements
 1. Bounded capacity: The queue cannot contain more than capacity elements.
 2. Blocking producers: If the queue is full, put() must wait until a consumer removes an element.
 3. Blocking consumers: If the queue is empty, take() must wait until a producer inserts an element.
 4. FIFO ordering: Items must be retrieved in insertion order.
 5. Thread safety: Multiple producers and consumers can operate concurrently without corrupting the queue or losing or duplicating items.
 6. Interruption: A blocked put() or take() must respond correctly to thread interruption.
 7. size(): Must return a thread-safe snapshot of the current number of elements.
 8. Input validation: Capacity must be positive; null elements are not permitted.
 9. No busy-waiting: Threads must not repeatedly poll the queue while waiting.
 10. Restrictions: You may use standard Java concurrency primitives and ordinary collections, but not an existing BlockingQueue implementation.