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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

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
        database = Database.instance("Test");
    }

    @After
    public void testAfters() throws BackingStoreException {
        database.deleteDatabase();
    }

    @Test
    public void testString() {
        System.out.println("testStringTransaction");

        String real = "this is a test data";
        database.set("test", real);

        assertEquals(real, database.get("test"));
        assertEquals(real, database.get("test", "non-real"));

        assertEquals(real, database.get(String.class, "test"));
        assertEquals(real, database.get(String.class, "test", null));

        assertEquals(real, database.get("imaginary", real));
    }

    @Test
    public void testObject() {
        System.out.println("testObjectTransaction");

        String[] real, result;

        real = new String[]{"hi", "its me"};
        assertNotNull(real);
        database.set("user", real);

        System.out.println("++user = " + database.get("user"));

        Gson gson = new Gson();
        assertEquals(database.get("user"), gson.toJson(real));
        assertEquals(database.get("user", "not teu"), gson.toJson(real));

        result = database.get(String[].class, "user");
        assertNotNull(result);
        assertEquals(real[0], result[0]);
        assertEquals(real[1], result[1]);

        result = database.get(String[].class, "user", real);
        assertNotNull(result);
        assertEquals(real[0], result[0]);
        assertEquals(real[1], result[1]);
    }

}
