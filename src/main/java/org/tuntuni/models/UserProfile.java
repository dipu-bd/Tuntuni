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
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import org.tuntuni.util.Commons;
import org.tuntuni.util.FileService;

/**
 * Profile information of current user. This class is directly linked to
 * {@code Database} and uses its APIs to save field values.
 */
public class UserProfile extends Persistent {

    private final Property<String> mUserName;
    private final Property<String> mAvatar;
    private final Property<String> mStatus;
    private final Property<String> mAboutMe;
    
    /**
     * Initializes a new User Profile
     */
    public UserProfile() {
        super("Profile");
        mStatus = buildProperty("Status", "");
        mAboutMe = buildProperty("AboutMe", "");
        mUserName = buildProperty("UserName", System.getProperty("user.name"));
        mAvatar = buildProperty("Avatar", Commons.getResource("avatar.png"));
    }

    /**
     * Gets the serializable instant UserData from this user profile.
     *
     * @return serializable user data
     */
    public UserData getData() {
        return new UserData(this);
    }

    public Property<String> usernameProperty() {
        return mUserName;
    }

    public Property<String> avatarProperty() {
        return mAvatar;
    }

    public Property<String> statusProperty() {
        return mStatus;
    }

    public Property<String> aboutmeProperty() {
        return mAboutMe;
    }

    /**
     * Gets the display user name
     *
     * @return
     */
    public String username() {
        return mUserName.getValue();
    }

    /**
     * Sets the display username.
     *
     * @param value Value of the field
     */
    public void username(String value) {
        value = value.trim();
        if (!value.isEmpty()) {
            mUserName.setValue(value);
        }
    }

    /**
     * Gets the file path of the avatar
     *
     * @return Value of the field
     */
    public String avatar() {
        return mAvatar.getValue();
    }

    /**
     * Sets the location of avatar image
     *
     * @param value
     */
    public void avatar(String value) {
        mAvatar.setValue(value);
    }

    /**
     * Gets the avatar image
     *
     * @return Avatar image; or {@code null} if none.
     */
    public Image getAvatarImage() {
        return FileService.instance().getImage(mAvatar.getValue());
    }

    /**
     * Gets the avatar image of specified width and height
     *
     * @param width Preferred width
     * @param height Preferred height
     * @return Avatar image; or {@code null} if none.
     */
    public Image getAvatarImage(double width, double height) {
        return Commons.resizeImage(getAvatarImage(), width, height);
    }

    /**
     * Gets the status of the user.
     *
     * @return Value of the field
     */
    public String status() {
        return mStatus.getValue();
    }

    /**
     * Sets the status of the user.
     *
     * @param value Value of the field
     */
    public void status(String value) {
        mStatus.setValue(value.trim());
    }

    /**
     * Gets the user's about me text.
     *
     * @return Value of the field
     */
    public String aboutme() {
        return mAboutMe.getValue();
    }

    /**
     * Sets the user's about me text.
     *
     * @param value Value of the field
     */
    public void aboutme(String value) {
        mAboutMe.setValue(value.trim());
    }
}
