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

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.tuntuni.Core;
import org.tuntuni.models.MetaData;
import org.tuntuni.models.UserProfile;

/**
 *
 * @author Sudipto Chandra
 */
public class DatabaseTest {

    Database database;

    public DatabaseTest() {
    }

    @Before
    public void testNewDatabse() {
        database = new Database();
    }

    @Test
    public void testString() {
        System.out.println("testStringTransaction");
        String expResult = "this is a test data";
        database.putString("test", "test", expResult);
        String result = database.getString("test", "test");
        assertEquals(expResult, result);
    }

    @Test
    public void testObject() {
        System.out.println("testObjectTransaction");
        MetaData expResult = new MetaData();
        assertNotNull(expResult);
        database.putObject("test", "meta", expResult);
        MetaData result = database.getObject("test", "meta", MetaData.class);
        assertNotNull(result);
        assertEquals(expResult.hostName(), result.hostName());
        assertEquals(expResult.title(), result.title());
        assertEquals(expResult.version(), result.version());
    }

    @Test
    public void testMultiple() {
        System.out.println("testMultiple");
        testString();
        testString();
        testString();
        testObject();;
        testString();
        testObject();
        testString();
        testString();
        testObject();
        testString();
        testObject();
        testObject();
        testString();
        testString();
        testObject();
        testString();
        testString();
        testObject();
        testObject();
        testString();
        testObject();
    }

}
