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
package org.tuntuni.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import org.tuntuni.Core;
import org.tuntuni.components.UserItem;
import org.tuntuni.connection.Client;

/**
 * The controller for the main scene of this application.
 * <p>
 * It has two parts. One is side-bar. Another is the body. The side-bar is
 * positioned at the right side and collapsible. The body is divided into two
 * parts. One for text messaging. Another for video chatting.
 * </p>
 *
 */
public class MainController implements Initializable {

    @FXML
    private ListView userList;
    @FXML
    private Label statusLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TabPane tabPane;
    @FXML
    private Button profileButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // set main controller to core
        Core.instance().main(this);

        // build user list later
        Platform.runLater(() -> {
            // run build immediately
            buildUserList();
            // listen to user list change
            Core.instance().subnet().userListProperty().addListener((ov, o, n)
                    -> Platform.runLater(() -> buildUserList()));

            // bind profile button text
            profileButton.setText(Core.instance().user().username());
            Core.instance().user().usernameProperty().addListener(
                    (ov, o, n) -> profileButton.setText(n));

            // avatar image
            ChangeListener updateAvatar = (ov, o, n)
                    -> ((ImageView) profileButton.getGraphic())
                    .setImage(Core.instance().user().getAvatarImage(32, 32));
            updateAvatar.changed(null, null, null);
            Core.instance().user().avatarProperty().addListener(updateAvatar);
        });
    }

    @FXML
    private void handleProfileAction(ActionEvent event) {
        selectProfile();
        Core.instance().profile().setClient(null);
        userList.getSelectionModel().clearSelection();
    }

    private synchronized void buildUserList() {
        // defaine client consumer
        Consumer<Client> consumer = (client) -> {
            // check if client is connected
            if (client.isConnected()) {
                // show client
                UserItem item = UserItem.createInstance(client);
                userList.getItems().add(item);
                item.setOnMouseClicked((evt) -> showUser(item));
                item.setOnKeyReleased((evt) -> showUser(item));
            }
        };
        // add all items
        userList.getItems().clear();
        Core.instance().subnet().userListProperty()
                .values().stream().forEach(consumer);
        // hide user list if no items
        userList.setPrefWidth(userList.getItems().isEmpty() ? 0.0 : 250.0);
        // refresh last user
        Core.instance().profile().refresh();
    }

    private void showUser(UserItem item) {
        if (item != null) {
            Core.instance().profile().setClient(item.getClient());
            Core.instance().messaging().setClient(item.getClient());
            Core.instance().videocall().setClient(item.getClient());
        }
    }

    public void selectProfile() {
        tabPane.getSelectionModel().select(0);
    }

    public void selectMessaging() {
        tabPane.getSelectionModel().select(1);
    }

    public void selectVideoCall() {
        tabPane.getSelectionModel().select(2);
    }

}
