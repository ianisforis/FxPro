package org.example;


import org.example.exception.EmptyBufferException;
import org.example.exception.NoSpaceException;

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

public class RingBuffer {

    private final Object[] data;

    private int countEl = 0;

    private int countput = 0;

    private int countGet = 0;

    public RingBuffer(int maxBufferSize) {
        if (maxBufferSize <= 0) {
            throw new UnsupportedOperationException("Buffer size can't be 0 or less");
        }

        data = new Object[maxBufferSize];
    }

    public void put(Object arg) {
        if (countEl == data.length) {
            throw new NoSpaceException("Buffer is full");
        }

        data[countput] = arg;

        countput = (countput + 1) % data.length;
        countEl++;
    }

    public Object get() {
        if (countEl == 0) {
            throw new EmptyBufferException("Buffer is empty");
        }

        Object result = data[countGet];

        countGet = (countGet + 1) % data.length;
        countEl--;

        return result;
    }
}
