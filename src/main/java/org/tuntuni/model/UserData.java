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

import java.io.Serializable;
import java.util.Date;
import javafx.scene.image.Image;
import org.tuntuni.util.HelperUtils;

/**
 *
 */
public class UserData implements Serializable {

    private final String mFullName;
    private final String mUserName;
    private final String mStatus;
    private final String mAboutMe;
    private final byte[] mAvatar;
    private final Date mDateOfBirth; 

    public UserData(UserProfile profile) {
        mUserName = profile.username();
        mFullName = profile.fullname();
        mStatus = profile.status();
        mAboutMe = profile.aboutme();
        mDateOfBirth = profile.dateofBirth();
        mAvatar = HelperUtils.imageToBytes(profile.avatar());
    }

    public String getFullName() {
        return mFullName;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getStatus() {
        return mStatus;
    }

    public String getAboutMe() {
        return mAboutMe;
    }

    public Image getAvatar() {
        return HelperUtils.bytesToImage(mAvatar);
    }

    public Date getDateOfBirth() {
        return mDateOfBirth;
    }
}