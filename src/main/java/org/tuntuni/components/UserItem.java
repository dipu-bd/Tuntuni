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

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.tuntuni.connection.Client;
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
            //post load work      
            uitem.initialize();
            // return main object
            return uitem;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @FXML
    private ImageView imageView;
    @FXML
    private Label fullName;
    @FXML
    private Label statusLabel;

    private Client mClient;

    private void initialize() {
        assert mClient != null;
        fullName.setText(mClient.getUserData().getUserName());
        imageView.setImage(mClient.getUserData().getAvatar());

        String status = mClient.getUserData().getStatus();
        statusLabel.setText(status.isEmpty() ? toString() : status);
    }

    private void setClient(Client client) {
        mClient = client;
    }

    public Client getClient() {
        return mClient;
    }

    @Override
    public String toString() {
        return mClient.getHostString()
                + "@" + mClient.getHostString()
                + ":" + mClient.getPort();
    }
}
