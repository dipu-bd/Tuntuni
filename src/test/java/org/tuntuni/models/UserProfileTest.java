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

import javafx.embed.swing.JFXPanel;
import javax.swing.SwingUtilities;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Sudipto Chandra
 */
public class UserProfileTest {

    public UserProfileTest() {
    }

    UserProfile profile;

    @Before
    public void testBefore() {
        SwingUtilities.invokeLater(() -> {
            JFXPanel jfxPanel = new JFXPanel(); // this will prepare JavaFX toolkit and environment           
        });
        profile = new UserProfile();
    }

    @Test
    public void testUsername_String() {
        System.out.println("testUsername_String");
        assertNotNull(profile.getName());
    }

    @Test
    public void testAvatar_Image() {
        System.out.println("testAvatar_Image");
        System.out.println("++" + profile.getAvatar());
        assertNotNull(profile.getAvatar());
        assertNotNull(profile.getAvatarImage());
        assertNotNull(profile.getAvatarImage(63, 83));
    }

    @Test
    public void testStatus_String() {
        System.out.println("testStatus_String");
        assertNotNull(profile.getStatus());
    }

    @Test
    public void testAboutme_String() {
        System.out.println("testAboutme_String");
        assertNotNull(profile.getAboutMe());
    }

    @Test
    public void testGetData() {
        System.out.println("testGetData");
        assertNotNull(profile.getData());
    }
}
