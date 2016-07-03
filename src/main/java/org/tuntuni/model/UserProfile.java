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
package org.tuntuni.model;

import com.sun.org.apache.xml.internal.security.utils.HelperNodeList;
import java.lang.reflect.Type;
import java.util.Date;
import javafx.scene.image.Image;
import org.tuntuni.Core;
import org.tuntuni.util.HelperUtils;

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
     * Checks if the user has any value set for the given field
     *
     * @param key Field name
     * @return True if has value.
     */
    boolean hasField(String key) {
        return Core.instance().database().hasField(STORE_NAME, key);
    }

    /**
     * Sets a string-type value to a field.
     *
     * @param key Field name.
     * @param value Field value.
     */
    void setField(String key, String value) {
        Core.instance().database().putData(STORE_NAME, key, value);
    }

    /**
     * Sets any value into a field
     *
     * @param key Field name
     * @param value Object value
     */
    void setField(String key, Object value) {
        Core.instance().database().putObject(STORE_NAME, key, value);
    }

    /**
     * Gets a string-type field value.
     *
     * @param key Field name.
     * @return Value of the field
     */
    String getField(String key) {
        return Core.instance().database().getData(STORE_NAME, key);
    }

    /**
     * Gets any field value.
     *
     * @param <T> Type of field value
     * @param key Field name
     * @param typeOfT Type of field value
     * @return Value of the field
     */
    <T extends Object> T getField(String key, Type typeOfT) {
        return Core.instance().database().getObject(STORE_NAME, key, typeOfT);
    }

    /**
     * Gets the full name of the user.
     *
     * @return Value of the field
     */
    public String fullname() {
        if (!hasField("FullName")) {
            return username();
        }
        return getField("FullName");
    }

    /**
     * Sets the full name of the user.
     *
     * @param value Value of the field
     */
    public void fullname(String value) {
        setField("FullName", value.trim());
    }

    /**
     * Gets the display name of user.
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
     * Sets the display name of user.
     *
     * @param value Value of the field
     */
    public void username(String value) {
        setField("UserName", value.trim());
    }

    /**
     * Gets the user's date of birth
     *
     * @return Value of the field
     */
    public Date dateofbirth() {
        if (!hasField("DateOfBirth")) {
            return null;
        }
        return getField("DateOfBirth", Date.class);
    }

    /**
     * Sets the user's date of birth
     *
     * @param value Value of the field
     */
    public void dateOfBirth(Date value) {
        setField("DateOfBirth", value);
    }

    /**
     * Gets the avatar image
     *
     * @return Value of the field
     */
    public Image avatar() {
        if (!hasField("Avatar")) {
            return Core.instance().resource().getImage("default-avatar.png");
        } else {
            return HelperUtils.bytesToImage(getField("Avatar", Byte[].class));
        }
    }

    /**
     * Sets the avatar image
     *
     * @param value Value of the field
     */
    public void avatar(Image value) {
        setField("Avatar", HelperUtils.imageToBytes(value));
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
        setField("Status", value);
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
