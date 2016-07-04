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

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import org.tuntuni.util.Database;

/**
 * To persist the fields of any sub class to file.
 */
public abstract class Persistent {

    private final Database mDatabase;

    public Persistent(String name) {
        mDatabase = Database.instance(name);
    }

    // build a property. called from parent class
    <T> Property<T> buildProperty(String key, T initialValue) {
        return new ExtendedSimpleObjectProperty<>(key, initialValue);
    }

    private class ExtendedSimpleObjectProperty<T> extends SimpleObjectProperty<T> {

        private final T mValue;
        private final String mKey;

        public ExtendedSimpleObjectProperty(String key, T initialValue) {
            super(initialValue);
            mKey = key;
            mValue = initialValue;
        }

        @Override
        public T get() {
            return mDatabase.get((Class<T>) mValue.getClass(), mKey, mValue);
        }

        @Override
        public void set(T value) {
            mDatabase.set(mKey, value);
        }
    }

}
