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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 *
 * @author Sudipto Chandra
 */
public class AudioFrame implements Externalizable, Comparable<AudioFrame> {

    @Override
    public int compareTo(AudioFrame t) {
        return 0;        
    }
        
    @Override
    public void writeExternal(ObjectOutput oo) throws IOException {
        
    }

    @Override
    public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {
        
    }

}
