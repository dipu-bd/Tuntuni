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
import javafx.scene.image.Image;
import org.tuntuni.Core;
import org.tuntuni.util.Commons;
import org.tuntuni.util.FileService;

/**
 * Profile information of current user. This class is directly linked to
 * {@code Database} and uses its APIs to save field values.
 */
public class UserProfile extends Persistent {

    private final Property<String> mName;
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
        mName = buildProperty("UserName", System.getProperty("user.name"));
        mAvatar = buildProperty("Avatar", "");
    }

    /**
     * Gets the serializable instant UserData from this user profile.
     *
     * @return serializable user data
     */
    public UserData getData() {
        return new UserData(this);
    }

////////////////////////////////////////////////////////////////////////////////
    
    /**
     * User name property
     *
     * @return
     */
    public Property<String> userNameProperty() {
        return mName;
    }

    /**
     * Avatar property
     *
     * @return
     */
    public Property<String> avatarProperty() {
        return mAvatar;
    }

    /**
     * Status property
     *
     * @return
     */
    public Property<String> statusProperty() {
        return mStatus;
    }

    /**
     * About me property
     *
     * @return
     */
    public Property<String> aboutMeProperty() {
        return mAboutMe;
    }

////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Sets the display setName.
     *
     * @param value Value of the field
     */
    public void setName(String value) {
        value = value.trim();
        if (!value.isEmpty()) {
            mName.setValue(value);
            Core.instance().changeState();
        }
    }

    /**
     * Sets the location of setAvatar image
     *
     * @param value
     */
    public void setAvatar(String value) {
        mAvatar.setValue(value);
        Core.instance().changeState();
    }

    /**
     * Sets the setStatus of the user.
     *
     * @param value Value of the field
     */
    public void setStatus(String value) {
        mStatus.setValue(value.trim());
        Core.instance().changeState();
    }

    /**
     * Sets the user's about me text.
     *
     * @param value Value of the field
     */
    public void setAboutMe(String value) {
        mAboutMe.setValue(value.trim());
        Core.instance().changeState();
    }

////////////////////////////////////////////////////////////////////////////////
    /**
     * Gets the display user name
     *
     * @return
     */
    public String getName() {
        return mName.getValue();
    }

    /**
     * Gets the setStatus of the user.
     *
     * @return Value of the field
     */
    public String getStatus() {
        return mStatus.getValue();
    }

    /**
     * Gets the user's about me text.
     *
     * @return Value of the field
     */
    public String getAboutMe() {
        return mAboutMe.getValue();
    }
    
    /**
     * Gets the file path of the setAvatar
     *
     * @return Value of the field
     */
    public String getAvatar() {
        return mAvatar.getValue();
    }

    /**
     * Gets the setAvatar image
     *
     * @return Avatar image; or {@code null} if none.
     */
    public Image getAvatarImage() {
        if (!mAvatar.getValue().isEmpty()) {
            return FileService.instance().getImage(mAvatar.getValue());
        } else {
            return new Image(getClass().getResourceAsStream("/img/avatar.png"));
        }
    }

    /**
     * Gets the setAvatar image of specified width and height
     *
     * @param width Preferred width
     * @param height Preferred height
     * @return Avatar image; or {@code null} if none.
     */
    public Image getAvatarImage(double width, double height) {
        return Commons.resizeImage(getAvatarImage(), width, height);
    }


}
