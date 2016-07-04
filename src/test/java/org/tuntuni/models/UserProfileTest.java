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

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.tuntuni.Core;

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
        profile = Core.instance().user();
    }

    @Test
    public void testHasField() {
        assertFalse(profile.hasField("nai_field"));
    }

    @Test
    public void testUsername_String() {
        assertNotNull(profile.username());
    }

    @Test
    public void testAvatar_Image() {
        assertNotNull(profile.avatar());
    }

    @Test
    public void testStatus_String() {
        assertNotNull(profile.status());
    }

    @Test
    public void testAboutme_String() {
        assertNotNull(profile.aboutme());
    }

    @Test
    public void testGetData() {
        assertNotNull(profile.getData());
    }
}
