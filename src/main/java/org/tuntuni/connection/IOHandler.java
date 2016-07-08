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
package org.tuntuni.connection;

import de.ruedigermoeller.serialization.FSTConfiguration;
import de.ruedigermoeller.serialization.FSTObjectInput;
import de.ruedigermoeller.serialization.FSTObjectOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 */
public class IOHandler {

    static FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    /**
     * Reads an object from the input
     *
     * @param stream
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object readObject(InputStream stream)
            throws IOException, ClassNotFoundException {

        FSTObjectInput in = conf.getObjectInput(stream);
        Object result = in.readObject();
    // DON'T: in.close(); here prevents reuse and will result in an exception      
        //stream.close();
        return result;
    }

    /**
     * Reads an object from input stream
     *
     * @param <T>
     * @param stream
     * @param type
     * @return
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public static <T extends Object>
            T readObject(InputStream stream, Class<T> type)
            throws IOException, ClassNotFoundException {

        return type.cast(readObject(stream));
    }

    /**
     * Writes an object to output stream
     *
     * @param stream
     * @param toWrite
     * @throws IOException
     */
    public static
            void writeObject(OutputStream stream, Object toWrite)
            throws IOException {

        if (toWrite == null) {
            return;
        }
        FSTObjectOutput out = conf.getObjectOutput(stream);
        out.writeObject(toWrite);
        // DON'T out.close() when using factory method;
        out.flush();
        //stream.close();

    }
}
