package org.example;

import org.example.exception.EmptyBufferException;
import org.example.exception.NoSpaceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RingBufferTest {

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    public void test_create_buffer_with_negative_or_zero_size(int size) {
        Exception exception = assertThrows(UnsupportedOperationException.class, () -> new RingBuffer(size));
        assertEquals("Buffer size can't be 0 or less", exception.getMessage());
    }

    @Test
    public void test_get_from_empty_buffer() {
        RingBuffer buffer = new RingBuffer(3);

        Exception exception = assertThrows(EmptyBufferException.class, buffer::get);
        assertEquals("Buffer is empty", exception.getMessage());
    }

    @Test
    public void test_put_one_element_and_get() {
        RingBuffer buffer = new RingBuffer(3);
        buffer.put(1);

        assertEquals(1, (Integer) buffer.get());
    }

    @Test
    public void test_buffer_overflow() {
        RingBuffer buffer = new RingBuffer(3);
        buffer.put(1);
        buffer.put(2);
        buffer.put(3);

        Exception exception = assertThrows(NoSpaceException.class, () -> buffer.put(4));
        assertEquals("Buffer is full", exception.getMessage());
    }

    @Test
    public void test_get_more_than_put() {
        RingBuffer buffer = new RingBuffer(3);
        buffer.put(1);
        buffer.put(2);
        buffer.put(3);

        buffer.get();
        buffer.get();
        buffer.get();

        Exception exception = assertThrows(EmptyBufferException.class, buffer::get);
        assertEquals("Buffer is empty", exception.getMessage());
    }

    @Test
    public void test_FIFO() {
        RingBuffer ringBuffer = new RingBuffer(3);
        ringBuffer.put(1);
        ringBuffer.put(2);
        ringBuffer.put(3);

        assertEquals(1, (Integer) ringBuffer.get());
        assertEquals(2, (Integer) ringBuffer.get());
        assertEquals(3, (Integer) ringBuffer.get());
    }

    @Test
    public void test_circular_behavior() {
        RingBuffer buffer = new RingBuffer(3);
        buffer.put(1);
        buffer.put(2);
        buffer.put(3);
        buffer.get();
        buffer.get();
        buffer.put(4);
        buffer.put(5);

        assertEquals(3, buffer.get());
        assertEquals(4, buffer.get());
        assertEquals(5, buffer.get());
    }

    @Test
    public void test_put_null() {
        RingBuffer ringBuffer = new RingBuffer(3);
        ringBuffer.put(null);
        ringBuffer.put(null);
        ringBuffer.put(null);

        assertNull(ringBuffer.get());
        assertNull(ringBuffer.get());
        assertNull(ringBuffer.get());
    }
}