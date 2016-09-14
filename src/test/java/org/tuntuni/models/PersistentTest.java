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
package org.tuntuni.models;

import java.util.UUID;
import java.util.prefs.BackingStoreException;
import javafx.beans.property.Property;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.tuntuni.util.Database;

/**
 *
 * @author Sudipto Chandra
 */
public class PersistentTest {

    final String dbname;

    public PersistentTest() {
        dbname = UUID.randomUUID().toString();
    }

    @After
    public void testAfters() throws BackingStoreException {
        Database.instance(dbname).deleteDatabase();
    }

    public class PersistentImpl extends Persistent {

        public final Property<String> Str;
        public final Property<Boolean> Bool;
        public final Property<Integer> Int;

        public PersistentImpl() {
            super(dbname);
            Str = buildProperty("Str", "Sample");
            Bool = buildProperty("Bool", true);
            Int = buildProperty("Int", 42);
        }

    }

    @Test
    public void testBuildProperty() {
        System.out.println("buildProperty");
        PersistentImpl instance = new PersistentImpl();

        System.out.println("+++before change");
        System.out.println("++str=" + instance.Str.getValue());
        System.out.println("++bool=" + instance.Bool.getValue());
        System.out.println("++int=" + instance.Int.getValue());

        assertTrue(instance.Bool.getValue());
        assertTrue(instance.Int.getValue() == 42);
        assertEquals(instance.Str.getValue(), "Sample");

        instance.Str.setValue("Hello world!");
        instance.Int.setValue(420);
        instance.Bool.setValue(Boolean.FALSE);

        System.out.println("+++after change");
        System.out.println("++str=" + instance.Str.getValue());
        System.out.println("++bool=" + instance.Bool.getValue());
        System.out.println("++int=" + instance.Int.getValue());

        assertFalse(instance.Bool.getValue());
        assertTrue(instance.Int.getValue() == 420);
        assertEquals(instance.Str.getValue(), "Hello world!");

        System.out.println();
    }

}
