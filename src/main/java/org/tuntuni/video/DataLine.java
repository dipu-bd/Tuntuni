/*
 * Copyright 2016 Tuntuni.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tuntuni.video;

import java.util.LinkedList;

/**
 * A push and pop based data line for audio or image frame.
 *
 * @param <T> Type of object to pass in this line
 */
public class DataLine<T extends DataFrame> {

    private long mStart;
    private final LinkedList<T> mData;

    public DataLine() {
        mStart = 0;
        mData = new LinkedList<>();
    }

    /**
     * Gets the start time of first push to this data line.
     *
     * @return
     */
    public long getStart() {
        return mStart;
    }

    /**
     * Gets the data list.
     *
     * @return
     */
    public LinkedList<T> getData() {
        return mData;
    }

    /**
     * Block until at least one input has received.
     *
     * @param time Time after which the data should be available
     * @return
     */
    public T pop(long time) {
        // block until one data is present
        int test = 0;
        while (getData().isEmpty() // data must exists
                // data must be newer than the given time
                || getData().peekFirst().getTime() < time) {
            // remove the expired data
            if (!getData().isEmpty()) {
                getData().removeFirst();
            }
            test++;
        }
        System.out.println("POP_TEST = " + test);        
        // send the data
        return mData.removeFirst();
    }

    /**
     * Add a data to the list
     *
     * @param data
     */
    public void push(T data, long time) {
        if (mStart == 0) {
            mStart = System.nanoTime();
        }
        data.setTime(data.getTime() - mStart);
        getData().addLast(data);
    }
}
