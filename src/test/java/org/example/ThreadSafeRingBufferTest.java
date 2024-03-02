package org.example;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ThreadSafeRingBufferTest {

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    public void test_create_buffer_with_negative_or_zero_size(int size) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new ThreadSafeRingBuffer(size));
        assertEquals("Buffer size can't be 0 or less", exception.getMessage());
    }

    @Test
    public void test_one_thread_fifo() throws InterruptedException {
        ThreadSafeRingBuffer buffer = new ThreadSafeRingBuffer(3);

        buffer.put("test1");
        buffer.put("test2");
        buffer.put("test3");

        assertEquals("test1", buffer.get());
        assertEquals("test2", buffer.get());
        assertEquals("test3", buffer.get());

        assertEquals(0, buffer.getCountEl());
    }

    @RepeatedTest(20)
    public void test_concurrent_access() throws InterruptedException {
        ThreadSafeRingBuffer buffer = new ThreadSafeRingBuffer(3);

        Thread producer = new Thread(() -> {
            try {
                buffer.put("test1");
                buffer.put("test2");
                buffer.put("test3");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                buffer.get();
                buffer.get();
                buffer.get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();

        producer.join();
        consumer.join();

        assertEquals(0, buffer.getCountEl());
    }

    @RepeatedTest(20)
    public void test_concurrent_circular_behavior() throws InterruptedException {
        ThreadSafeRingBuffer buffer = new ThreadSafeRingBuffer(3);
        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);

        Thread producer1 = new Thread(() -> {
            try {
                buffer.put(1);
                buffer.put(2);
                buffer.put(3);
                latch1.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer1 = new Thread(() -> {
            try {
                latch1.await();
                buffer.get();
                buffer.get();
                latch2.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread producer2 = new Thread(() -> {
            try {
                latch2.await();
                buffer.put(4);
                buffer.put(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer1.start();
        consumer1.start();
        producer2.start();

        producer1.join();
        consumer1.join();
        producer2.join();

        assertEquals(3, buffer.get());
        assertEquals(4, buffer.get());
        assertEquals(5, buffer.get());
    }
}