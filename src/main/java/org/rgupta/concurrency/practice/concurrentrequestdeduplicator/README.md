If two or more callers overlap on the same key, only one execution of action is permitted; other callers joining that execution receive its result.



Requests with different keys must be able to execute concurrently.



If the action throws an exception, all callers sharing that execution must observe the failure.



After an operation has completed, a future request for the same key should be able to execute again. This is not a permanent cache.



No busy-waiting.



The implementation must be thread-safe.



Null keys and actions should be rejected.