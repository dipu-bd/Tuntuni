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

    public T pop() {
        return mData.pollFirst();
    }

    public void push(T data) {
        if (mStart == 0) {
            mStart = System.nanoTime();
        }
        mData.addLast(data);
    }    
}
