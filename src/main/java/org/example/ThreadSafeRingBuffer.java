package org.example;

import lombok.Getter;

/**1. FIFO
 2. Ring buffer, use all space in the buffer. Throw exception in corner cases:  no free space (on put) or data (on get)e.g.
 created empty buffer with size = 3   _,_,_
 get -> no data, buffer is empty (exception: non thread-save , block: thread-safe)
 put 1 ->     1,_,_
 put 2 ->     1,2,_
 put 3 ->     1,2,3
 put 4 -> buffer is full  (exception: non thread-save , block: thread-safe)
 get -> 1     _,2,3
 put 4 ->    4,2,3
 get -> 2     4,_,3
 3. blocking when no free place (on put) or data (on get)**/
public class ThreadSafeRingBuffer {

    private final Object[] data;

    private int countEl = 0;

    private int countPut = 0;

    private int countGet = 0;

    public ThreadSafeRingBuffer(int maxBufferSize) {
        if (maxBufferSize <= 0) {
            throw new IllegalArgumentException("Buffer size can't be 0 or less");
        }

        data = new Object[maxBufferSize];
    }

    public synchronized void put(Object item) throws InterruptedException {
        while (countEl == data.length) {
            wait();
        }

        data[countPut] = item;
        countPut = (countPut + 1) % data.length;
        countEl++;

        notifyAll();
    }

    public synchronized Object get() throws InterruptedException {
        while (countEl == 0) {
            wait();
        }

        Object result = data[countGet];
        countGet = (countGet + 1) % data.length;
        countEl--;

        notifyAll();

        return result;
    }
}
