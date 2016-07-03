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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sudipto Chandra
 */
public class UserProfileTest {

    public UserProfileTest() {
    }

    UserProfile profile;

    @Before
    public void setUp() {
        profile = new UserProfile();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testHasField() {
        assertFalse(profile.hasField("nai_field"));
    }

    @Test
    public void testFullname() {
        System.out.print("+++ full name = ");
        System.out.println(profile.fullname());
    }

    @Test
    public void testUsername_String() {
        System.out.print("+++ user name = ");
        System.out.println(profile.username());
    }

    @Test
    public void testDateofBirth() {
        System.out.print("+++ date of birth = ");
        System.out.println(profile.dateofBirth());
    }

    @Test
    public void testAvatar_Image() {
        System.out.print("+++ avatar = ");
        System.out.println(profile.avatar());
    }

    @Test
    public void testStatus_String() {
        System.out.print("+++ status = ");
        System.out.println(profile.status());
    }

    @Test
    public void testAboutme_String() {
        System.out.print("+++ aboutme = ");
        System.out.println(profile.aboutme());
    }

}
