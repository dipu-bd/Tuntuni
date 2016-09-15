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

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.tuntuni.Core;
import org.tuntuni.connection.Client;
import org.tuntuni.models.Logs;
import org.tuntuni.models.UserData;
import org.tuntuni.util.Commons;

/**
 * FXML Controller class
 *
 * @author Sudipto Chandra
 */
public class UserItem extends BorderPane {

    public static UserItem createInstance(Client client) {
        try {
            // build the component
            UserItem uitem = (UserItem) Commons.loadPaneFromFXML("/fxml/UserItem.fxml");
            // set client
            uitem.setClient(client);
            // return main object
            return uitem;
        } catch (IOException | NullPointerException ex) {
            Logs.error("UserItem", "Failed to create instance. {0}", ex);
            //ex.printStackTrace();
        }
        return null;
    }

    @FXML
    private ImageView imageView;
    @FXML
    private Label fullName;
    @FXML
    private Label statusLabel;
    @FXML
    private Button unseenCount;

    private Client mClient;

    public Client getClient() {
        return mClient;
    }

    private void setClient(Client client) throws NullPointerException {
        mClient = client;
        refresh(client.getUserData());
        client.userdataProperty().addListener((ov, o, n) -> {
            Platform.runLater(() -> refresh(n));
        });
        client.unseenCountProperty().addListener((ov, o, n) -> {
            Platform.runLater(() -> {
                unseenCount.setText(String.valueOf(n));
            });
        });
    }

    private void refresh(UserData data) {
        // if user data is not avaiable
        if (data == null) {
            setVisible(false);
            return;
        }
        // show data
        setVisible(true);
        fullName.setText(data.getUserName());
        imageView.setImage(data.getAvatar(
                imageView.getFitWidth(), imageView.getFitHeight()));
        String status = data.getStatus();
        statusLabel.setText(status.isEmpty() ? mClient.toString() : status);
    }

    @FXML
    public void onMouseClicked() {
        Core.instance().main().showUser(mClient);
    }

    @FXML
    public void onUnseenClicked() {
        Core.instance().main().showUser(mClient);
        Core.instance().main().selectMessaging();
    }
}
