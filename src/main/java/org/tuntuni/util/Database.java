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
import java.lang.reflect.Type;
import java.util.prefs.Preferences;

/**
 * A connector to JetBrains Xodus database system. Just to provide simple
 * key-value storage.
 */
public class Database {

    public static final String DEFAULT_NODE = "org/tuntuni";

    private final Gson mGson;
    private final Preferences mPrefs;

    public Database() {
        // create gson parser instance
        mGson = new Gson();
        // open environment
        mPrefs = Preferences.userRoot().node(DEFAULT_NODE);
    }

    /**
     * Gets the database storage by name
     *
     * @param name Storage name
     * @return Database storage
     */
    public Preferences store(String name) {
        return mPrefs.node(name.toLowerCase());
    }

    /**
     * Gets the value from a database store by key
     *
     * @param storeName Name of the store
     * @param key Name of the key.
     * @return
     */
    public String getString(String storeName, String key) {
        return store(storeName).get(key.toLowerCase(), "");
    }

    /**
     * Gets stored array of raw bytes.
     *
     * @param storeName Name of the store
     * @param key Name of the key.
     * @return Array of bytes or {@code null}.
     */
    public byte[] getBytes(String storeName, String key) {
        return store(storeName).getByteArray(key.toLowerCase(), null);
    }

    /**
     * Gets a object stored in database by key
     *
     * @param <T> Type of stored.object
     * @param storeName Name of the store.
     * @param key Name of the key.
     * @param typeOfT Type of stored object
     * @return The data stored in the database or {@code null} if anything went
     * wrong.
     */
    public <T extends Object> T getObject(String storeName, String key, Type typeOfT) {
        String data = getString(storeName, key);
        return mGson.fromJson(data, typeOfT);
    }

    /**
     * Store a key-value pair in the database.
     *
     * @param storeName Name of the storage.
     * @param key Key name
     * @param value Value to store
     */
    public void putString(String storeName, String key, String value) {
        store(storeName).put(key.toLowerCase(), value);
    }

    /**
     * Stores an array of raw bytes.
     *
     * @param storeName Name of the store.
     * @param key Key name.
     * @param data Array of bytes to save.
     */
    public void putBytes(String storeName, String key, byte[] data) {
        if (data != null) {
            store(storeName).putByteArray(key.toLowerCase(), data);
        }
    }

    /**
     * Store an object in the database. The data must be serializable. It
     * actually converts the object into JSON string. And stores the string into
     * database.
     *
     * @param storeName Storage name.
     * @param key Key name.
     * @param data Value to store.
     */
    public void putObject(String storeName, String key, Object data) {
        if (data != null) {
            String json = mGson.toJson(data);
            putString(storeName, key, json);
        }
    }

    /**
     * Check if a key has non-empty value
     *
     * @param storeName Name of the storage
     * @param key Key name
     * @return True only if key has non-empty value.
     */
    public boolean hasField(String storeName, String key) {
        return getString(storeName, key).length() > 0;
    }
}
