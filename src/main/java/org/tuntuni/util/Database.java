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
    private final Preferences mDatabase;

    public Database() {
        // create gson parser instance
        mGson = new Gson();
        // open environment
        mDatabase = Preferences.userRoot().node(DEFAULT_NODE);
    }

    public Preferences store(String name) {
        return mDatabase.node(name.toLowerCase());
    }

    public String getData(String storeName, String key) {
        return store(storeName).get(key.toLowerCase(), "");
    }

    public <T extends Object> T getObject(String storeName, String key, Type typeOfT) {
        String data = getData(storeName, key);
        return mGson.fromJson(data, typeOfT);
    }

    public void putData(String storeName, String key, String value) {
        store(storeName).put(key.toLowerCase(), value);
    }

    public void putObject(String storeName, String key, Object data) {
        if (data != null) {
            String json = mGson.toJson(data);
            putData(storeName, key, json);
        }
    }

    public boolean hasField(String storeName, String key) {
        return store(storeName).get(key, "").length() > 0;
    }
}
