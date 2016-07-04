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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import org.controlsfx.dialog.ExceptionDialog;
import org.tuntuni.Core;
import org.tuntuni.connection.Client;
import org.tuntuni.util.FileService;

/**
 * To view or edit user's profile
 */
public class ProfileController implements Initializable {

    private Client mClient;

    @FXML
    private TextField userName;
    @FXML
    private TextField statusText;
    @FXML
    private Button avatarButton;
    @FXML
    private ImageView avatarImage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Core.instance().profile(this);
    }

    public void setClient(Client client) {
        mClient = client;
        loadAll();
    }

    private void loadAll() {

        String name = Core.instance().user().username();
        String status = Core.instance().user().status();
        String about = Core.instance().user().aboutme();
        Image image = Core.instance().user().avatarImage();

        if (mClient != null) {
            name = mClient.getUserData().getUserName();
            status = mClient.getUserData().getStatus();
            image = mClient.getUserData().getAvatar();
            about = mClient.getUserData().getAboutMe();
        }

        userName.setText(name);
        statusText.setText(status);
        avatarImage.setImage(image);

        userName.setEditable(mClient == null);
        statusText.setEditable(mClient == null);
    }

    @FXML
    private void changeAvatar(ActionEvent event) {
        if (mClient == null) {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().setAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.PNG", "*.JPG", "*.BMP", "*.GIF"));
            fc.setTitle("Choose your avatar...");

            try {
                File choosen = fc.showOpenDialog(Core.instance().stage());
                String uploaded = FileService.instance().upload(choosen);
                Image image = FileService.instance().getImage(uploaded);
                Core.instance().user().avatar(uploaded);
            } catch (IOException ex) {
                ExceptionDialog dialog = new ExceptionDialog(ex);
                dialog.setTitle("Failed to upload image");
                dialog.showAndWait();
            }
        }
        loadAll();
    }

    @FXML
    private void changeName(ActionEvent event) {
        Core.instance().user().username(userName.getText());
    }

    @FXML
    private void changeStatus(ActionEvent event) {
        Core.instance().user().status(statusText.getText());
    }

}
