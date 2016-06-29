/*
 * Copyright 2016 Sudipto Chandra.
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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sudipto Chandra
 */
public class StatusTest {
    
    public StatusTest() {
    }

    /**
     * Test of values method, of class Status.
     */
    @Test
    public void testValues() {
        System.out.println("+++ values +++");        
        Status[] result = Status.values();
        assertNotNull(result);
        for(Status s : result) {
            assertNotNull(s);
            System.out.println(s.toString());
        }
        System.out.println();
    }
 
}
