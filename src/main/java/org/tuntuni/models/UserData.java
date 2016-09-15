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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javafx.scene.image.Image;
import org.tuntuni.util.Commons;

/**
 * Data that is passed between server and client as User Profile
 */
public class UserData implements Externalizable {

    private static final int AVATAR_MAX_SIZE = 128;

    private String mName;
    private String mStatus;
    private String mAboutMe; 
    private byte[] mAvatar;

    public UserData() {
    }
    
    public UserData(String userName) {
        mName = userName;
    }

    public UserData(UserProfile profile) {
        mName = profile.getName();
        mStatus = profile.getStatus();
        mAboutMe = profile.getAboutMe(); 
        mAvatar = Commons.imageToBytes(
                profile.getAvatarImage(AVATAR_MAX_SIZE, AVATAR_MAX_SIZE));
    }

    @Override
    public void writeExternal(ObjectOutput oo) throws IOException { 
        oo.writeUTF(mName);
        oo.writeUTF(mStatus);
        oo.writeUTF(mAboutMe);
        oo.writeInt(mAvatar.length);
        oo.write(mAvatar, 0, mAvatar.length);
    }

    @Override
    public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {           
        mName = (String) oi.readUTF();
        mStatus = (String) oi.readUTF();
        mAboutMe = (String) oi.readUTF();
        mAvatar = new byte[oi.readInt()];
        oi.readFully(mAvatar);        
    }

    public String getUserName() {
        return mName;
    } 
    
    public String getStatus() {
        return mStatus;
    }

    public String getAboutMe() {
        return mAboutMe;
    }

    public Image getAvatar() {
        return Commons.bytesToImage(mAvatar);
    }

    public Image getAvatar(double width, double height) {
        return Commons.resizeImage(getAvatar(), width, height);
    }

    public byte[] getAvatarData() {
        return mAvatar;
    }

}
