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
package org.tuntuni.util;

import com.google.gson.Gson;
import java.util.prefs.BackingStoreException;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.tuntuni.models.MetaData;

/**
 *
 * @author Sudipto Chandra
 */
public class DatabaseTest {
    
    String name;
    Database database;
    
    public DatabaseTest() {
    }
    
    @Before
    public void testNewDatabse() {
        database = new Database("Test");
    }
    
    @After
    public void testAfters() throws BackingStoreException {
         database.deleteDatabase();
    }
    
    
    @Test
    public void testString() {
        System.out.println("testStringTransaction");
        
        String real = "this is a test data";
        database.put("test", real);
        
        assertEquals(real, database.get("test"));
        assertEquals(real, database.get("test", "non-real"));
        
        assertEquals(real, database.get(String.class, "test"));
        assertEquals(real, database.get(String.class, "test", null));
        
        assertEquals(real, database.get("imaginary", real));
    }
    
    @Test
    public void testObject() {
        System.out.println("testObjectTransaction");
        MetaData real, result;
        
        real = new MetaData();
        assertNotNull(real);
        database.put("meta", real);
        
        System.out.println("++meta = " + database.get("meta"));
        
        Gson gson = new Gson();
        assertEquals(database.get("meta"), gson.toJson(real));
        assertEquals(database.get("meta", "not teu"), gson.toJson(real));
        
        result = database.get(MetaData.class, "meta");
        assertNotNull(result);
        assertEquals(real.hostName(), result.hostName());
        assertEquals(real.title(), result.title());
        assertEquals(real.version(), result.version());
        
        result = database.get(MetaData.class, "meta", real);
        assertNotNull(result);
        assertEquals(real.hostName(), result.hostName());
        assertEquals(real.title(), result.title());
        assertEquals(real.version(), result.version());
    }
    
}
