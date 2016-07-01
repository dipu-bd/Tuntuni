/*
 * Copyright 2016 Sudipto Chandra.
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
package org.tuntuni.connection;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableSet;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sudipto Chandra
 */
public class SubnetTest {

    public SubnetTest() {
    }

    /**
     * Test of userListProperty method, of class Subnet.
     */
    @Test
    public void testUserListProperty() {

    }

    /**
     * Test of getUserList method, of class Subnet.
     */
    @Test
    public void testGetUserList() throws InterruptedException {
        Subnet subnet = new Subnet();
        subnet.userListProperty().addListener((observable, oldValue, newValue) -> {
            System.out.print("+++++ set changed: ");
            System.out.println(oldValue.size() + " to " + newValue.size() + " of " + observable);            

        });

        Thread.sleep(600_000);  // run for 10 mins
    }
}