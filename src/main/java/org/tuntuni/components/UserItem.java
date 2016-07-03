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
package org.tuntuni.components;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.tuntuni.connection.Client;

/**
 * FXML Controller class
 *
 * @author Sudipto Chandra
 */
public class UserItem {

    private Node mNode;
    private final Client mClient;

    @FXML
    private ImageView imageView;
    @FXML
    private Label fullName;
    @FXML
    private Label statusLabel;

    private UserItem(Client client) {
        mClient = client;
    }

    public static UserItem createInstance(Client client) {
        try {
            UserItem uitem = new UserItem(client);
            //init loader           
            FXMLLoader loader = new FXMLLoader();
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            loader.setController(uitem);
            loader.setLocation(UserItem.class
                    .getResource("/fxml/UserItem.fxml"));
            //load fxml
            uitem.setNode(loader.load());
            //post load work               
            Platform.runLater(() -> uitem.initialize());
            // return main object
            return uitem;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void initialize() {
        fullName.setText(mClient.getUserData().getFullName());
        statusLabel.setText(mClient.getUserData().getStatus());
        imageView.setImage(mClient.getUserData().getAvatar(48, 48));
    }

    void setNode(Node node) {
        mNode = node;
    }

    public Node getNode() {
        return mNode;
    }

}
