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
import java.util.prefs.Preferences;

/**
 * A connector to JetBrains Xodus database system. Just to provide simple
 * key-value storage.
 */
public class Database {

    public static final String DEFAULT_NODE = "org/tuntuni";

    private String mName;
    private Preferences mPrefs;
    private final Gson mGson;

    // constructor. hidden from public.
    private Database() {
        mName = "Default";
        // create gson parser instance        
        mGson = new Gson();
        // open environment
        mPrefs = Preferences.userRoot().node(DEFAULT_NODE);
    } 
    
    /**
     * Gets an instance of the database.
     *
     * @return
     */
    public static Database instance() {
        return new Database();
    }

    /**
     * Gets an instance of the database of given store.
     *
     * @param storeName Name of the store.
     * @return
     */
    public static Database instance(String storeName) {
        return instance().store(storeName);
    }

    /**
     * Gets the sub database store by name.
     *
     * @param storeName Name of the sub store under current store.
     * @return
     */
    public final Database store(String storeName) {
        mName = storeName;
        mPrefs = mPrefs.node(storeName.trim().toLowerCase());
        return this;
    }

    /**
     * Gets the name of the current instance.
     *
     * @return
     */
    public String getName() {
        return mName;
    }

    /**
     * <b>CAUTION ADVICED!</b>. This method will delete the entire database.
     *
     * @throws java.util.prefs.BackingStoreException
     */
    @Deprecated
    public void deleteDatabase() throws BackingStoreException {
        mPrefs.removeNode();
    }

    /**
     * Check if the database has a key
     *
     * @param key Key name
     * @return True only if key has a non-empty value.
     */
    public boolean contains(String key) {
        return !mPrefs.get(key.toLowerCase(), "").isEmpty();
    }

    /**
     * Store an object in the database. The data must be serializable. It
     * actually converts the object into JSON string. And stores the string into
     * database.
     *
     * @param key Key name.
     * @param data Value to store.
     */
    public void set(String key, Object data) {
        String json = data == null ? "" : mGson.toJson(data);
        mPrefs.put(key.toLowerCase(), json);
    }

    /**
     * Gets a object stored in database by key
     *
     * @param <T> Type of stored.object
     * @param type Type of stored object
     * @param key Key name.
     * @param defaultValue The value to return if anything goes wrong
     * @return The data stored in the database or the {@code defaultValue} if
     * anything went wrong.
     */
    public <T> T get(Class<T> type, String key, T defaultValue) {
        String json = mPrefs.get(key.toLowerCase(), "");
        try {
            // convert from json. if empty return defaultValue
            return json.isEmpty() ? defaultValue : mGson.fromJson(json, type);
        } catch (Exception ex) {
            // if return type is string pass the json
            return (type == String.class) ? (T) json : defaultValue;
        }
    }

    /**
     * Deletes a key and erase its value from database.
     *
     * @param key
     */
    public void delete(String key) {
        mPrefs.remove(key);
    }

    ////////////////////////////////////////////////////////////////////////////    
    //// Implicit Methods that extends functionality of base methods
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Gets a object stored in database by key
     *
     * @param <T> Type of stored.object
     * @param type Type of stored object
     * @param key Key name.
     * @return The data stored in the database or {@code null} if anything went
     * wrong.
     */
    public <T> T get(Class<T> type, String key) {
        return get(type, key, null);
    }

    /**
     * Gets an string in database by key
     *
     * @param key Key name.
     * @param defaultValue The value to return if anything goes wrong
     * @return The data stored in the database or {@code null} if anything went
     * wrong.
     */
    public String get(String key, String defaultValue) {
        return get(String.class, key, defaultValue);
    }

    /**
     * Gets an string in database by key
     *
     * @param key Key name.
     * @return An empty string is returned if read fails
     */
    public String get(String key) {
        return get(key, "");
    }

}
