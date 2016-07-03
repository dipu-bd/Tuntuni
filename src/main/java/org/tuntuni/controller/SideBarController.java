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
package org.tuntuni.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import org.tuntuni.Core;

/**
 * SideBarController appears in the left side of the main scene.
 * <p>
 * It shows a list of users, and options to manage them. Also, the side-bar is
 * collapsible. In collapsed mode only the top active users will appear. And in
 * expanded mode a list of users with search option will be available.</p>
 */
public class SideBarController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Core.instance().sidebar(this);        
    }

    @FXML
    private ListView userList;
    @FXML
    private Button toggleButton;
}
