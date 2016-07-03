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
import java.io.Closeable;
import java.io.File;
import java.lang.reflect.Type; 
import jetbrains.exodus.ByteIterable; 
import jetbrains.exodus.bindings.StringBinding;
import jetbrains.exodus.env.Environment;
import jetbrains.exodus.env.Environments;
import jetbrains.exodus.env.Store;
import jetbrains.exodus.env.StoreConfig;
import jetbrains.exodus.env.Transaction; 

/**
 * A connector to JetBrains Xodus database system. Just to provide simple
 * key-value storage.
 */
public class Database implements Closeable {

    public static File databaseFolder() {
        return new File(System.getProperty("user.home") + File.separator + ".tuntuni");
    }

    public static final String DEFAULT_STORE = "Default";

    private final Gson mGson;
    private final Environment mEnvironment;

    public Database() {
        // create gson parser instance
        mGson = new Gson();
        // open environment
        mEnvironment = Environments.newInstance(Database.databaseFolder()); 
    }

    @Override
    public void close() {        
        mEnvironment.close();
    }

    public String getData(String storeName, String key) {
        // begin readonly transaction
        final Transaction txn = mEnvironment.beginReadonlyTransaction();
        // open store by name
        final Store store = mEnvironment.openStore(storeName,
                StoreConfig.WITH_DUPLICATES_WITH_PREFIXING, txn);
        // get data by key
        ByteIterable bit = store.get(txn, StringBinding.stringToEntry(key));
        return bit == null ? "" : StringBinding.entryToString(bit);
    }

    public String getData(String key) {
        return getData(DEFAULT_STORE, key);
    }

    public <T extends Object> T getObject(String key, Type typeOfT) {
        String data = getData(key);
        return mGson.fromJson(data, typeOfT);
    }

    public void putData(String storeName, String key, String value) {
        // execute transaction to write
        mEnvironment.executeInTransaction((txn) -> {
            // open store by name
            final Store store = mEnvironment.openStore(storeName,
                    StoreConfig.WITH_DUPLICATES_WITH_PREFIXING, txn);
            // save key->value pair
            store.put(txn, StringBinding.stringToEntry(key),
                    StringBinding.stringToEntry(value));
        });
    }

    public void putData(String key, String value) {
        putData(DEFAULT_STORE, key, value);
    }

    public void putObject(String key, Object data) {
        String json = mGson.toJson(data);
        putData(key, json);
    }
}
