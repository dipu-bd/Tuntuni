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

import java.lang.reflect.Type;
import javafx.scene.image.Image;
import org.tuntuni.Core;
import org.tuntuni.util.Commons;

/**
 * Profile information of current user. This class is directly linked to
 * {@code Database} and uses its APIs to save field values.
 */
public class UserProfile {

    public static final String STORE_NAME = "Profile";

    /**
     * Initializes a new User Profile
     */
    public UserProfile() {
    }

    /**
     * Gets the serializable instant UserData from this user profile.
     *
     * @return serializable user data
     */
    public UserData getData() {
        return new UserData(this);
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    //// DATBASE interactions
    ////////////////////////////////////////////////////////////////////////////    
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Checks if the user has any value set for the given field
     *
     * @param key Field username
     * @return True if has value.
     */
    boolean hasField(String key) {
        return Core.instance().database().hasField(STORE_NAME, key);
    }

    /**
     * Sets bytes into a field
     *
     * @param key Field username
     * @param value Object value
     */
    void setField(String key, String value) {
        Core.instance().database().putString(STORE_NAME, key, value);
    }

    /**
     * Sets bytes into a field
     *
     * @param key Field username
     * @param value Object value
     */
    void setByteField(String key, byte[] value) {
        Core.instance().database().putBytes(STORE_NAME, key, value);
    }

    /**
     * Sets any value into a field
     *
     * @param key Field username
     * @param value Object value
     */
    void setObjectField(String key, Object value) {
        Core.instance().database().putObject(STORE_NAME, key, value);
    }

    /**
     * Gets a string-type field value.
     *
     * @param key Field username.
     * @return Value of the field
     */
    String getField(String key) {
        return Core.instance().database().getString(STORE_NAME, key);
    }

    /**
     * Gets a string-type field value.
     *
     * @param key Field username.
     * @return Value of the field
     */
    byte[] getByteField(String key) {
        return Core.instance().database().getBytes(STORE_NAME, key);
    }

    /**
     * Gets any field value.
     *
     * @param <T> Type of field value
     * @param key Field username
     * @param typeOfT Type of field value
     * @return Value of the field
     */
    <T extends Object> T getObjectField(String key, Type typeOfT) {
        if (typeOfT.equals(byte[].class)) {
            return (T) Core.instance().database().getBytes(STORE_NAME, key);
        } else {
            return Core.instance().database().getObject(STORE_NAME, key, typeOfT);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    //// PROPERTIES 
    ////////////////////////////////////////////////////////////////////////////    
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Gets the display username of user.
     *
     * @return Value of the field
     */
    public String username() {
        if (!hasField("UserName")) {
            return System.getProperty("user.name");
        } else {
            return getField("UserName");
        }
    }

    /**
     * Sets the display username of user.
     *
     * @param value Value of the field
     */
    public void username(String value) {
        setField("UserName", value.trim());
    }

    /**
     * Gets the avatar image
     *
     * @return Value of the field
     */
    public Image avatar() {
        byte[] data = getByteField("Avatar");
        if (data == null) {
            return Core.instance().resource().getImage("avatar.png");
        } else {
            return Commons.bytesToImage(data);
        }
    }

    /**
     * Sets the avatar image
     *
     * @param value Value of the field
     */
    public void avatar(byte[] value) {
        setByteField("Avatar", value);
    }

    /**
     * Sets the avatar image
     *
     * @param value Value of the field
     */
    public void avatar(Image value) {
        setByteField("Avatar", Commons.imageToBytes(value));
    }

    /**
     * Gets the status of the user.
     *
     * @return Value of the field
     */
    public String status() {
        return getField("Status");
    }

    /**
     * Sets the status of the user.
     *
     * @param value Value of the field
     */
    public void status(String value) {
        setField("Status", value.trim());
    }

    /**
     * Gets the user's about me text.
     *
     * @return Value of the field
     */
    public String aboutme() {
        return getField("AboutMe");
    }

    /**
     * Sets the user's about me text.
     *
     * @param value Value of the field
     */
    public void aboutme(String value) {
        setField("AboutMe", value);
    }

}
