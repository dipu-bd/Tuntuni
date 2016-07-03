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

import java.io.File;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.tuntuni.connection.model.UserProfile;

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

    @After
    public void testClose() {        
        //database.close();
    }

    public void testDatabaseFolder() {
        System.out.println("testGetSavePath");
        File result = Database.databaseFolder();
        assertTrue(result.exists());
        System.out.println("Database path: " + result);
    }

    public void testStringGet() {
        System.out.println("testStringTransaction");
        String expResult = "this is a test data";
        String result = database.getData("Test");
        assertEquals(expResult, result);
    }

    public void testStringPut() {
        System.out.println("testStringTransaction");
        String expResult = "this is a test data";
        database.putData("Test", expResult);
    }

    public void testObjectGet() {
        System.out.println("testObjectTransaction");
        UserProfile expResult = new UserProfile();
        assertNotNull(expResult);
        UserProfile result = database.getObject("Profile", UserProfile.class);
        assertNotNull(result);
        assertEquals(expResult.name, result.name);
    }
 
    public void testObjectPut() {
        System.out.println("testObjectTransaction");
        database.putObject("Profile", new UserProfile());
    }

    @Test
    public void testMultiple() {
        System.out.println("testMultiple");
        testObjectPut();
        testStringPut();
        testObjectGet();
        testObjectGet();
        testStringGet();
        testObjectGet();
        testStringGet();
        testStringGet();
        testObjectPut();
        testObjectGet();
        testStringGet();
        testObjectGet();
        testStringPut();
        testStringGet();
        testObjectGet();
        testStringGet();
        testStringGet();
    }

}
