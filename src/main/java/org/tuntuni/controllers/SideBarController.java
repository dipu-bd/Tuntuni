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
package org.tuntuni.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import org.tuntuni.Core;
import org.tuntuni.components.UserItem;
import org.tuntuni.connection.Client;

/**
 * SideBarController appears in the left side of the main scene.
 * <p>
 * It shows a list of users, and options to manage them. Also, the side-bar is
 * collapsible. In collapsed mode only the top active users will appear. And in
 * expanded mode a list of users with search option will be available.</p>
 */
public class SideBarController implements Initializable {

    @FXML
    private ListView userList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Core.instance().sidebar(this);

        // build user list later
        Platform.runLater(() -> {
            buildUserList(Core.instance().subnet().userListProperty());
            // add user list change listener
            Core.instance().subnet().userListProperty().addListener(
                    (observable, oldVal, newVal) -> buildUserList(newVal));
        });
    }

    private void buildUserList(ObservableSet<Client> clients) {

        userList.getItems().clear();

        clients.stream().forEach((client) -> {
            //cell.setText(client.user().fullname());
            //cell.setGraphic(new ImageView(client.user().avatar()));
            userList.getItems().add(UserItem.createInstance(client).getNode());
        });
    }
}
