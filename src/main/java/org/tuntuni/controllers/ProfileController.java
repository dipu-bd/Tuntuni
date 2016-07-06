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
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.controlsfx.dialog.ExceptionDialog;
import org.tuntuni.Core;
import org.tuntuni.connection.Client;
import org.tuntuni.util.Database;
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
    private TextArea aboutMe;
    @FXML
    private Button avatarButton;
    @FXML
    private ImageView avatarImage;
    @FXML
    private GridPane aboutGridPane;
    @FXML
    private Button messageButton;
    @FXML
    private Button videoCallButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Core.instance().profile(this);

        // bind all 
        userName.textProperty().addListener((a, b, c) -> changeName());
        statusText.textProperty().addListener((a, b, c) -> changeStatus());
        aboutMe.textProperty().addListener((a, b, c) -> changeAboutMe());
    }

    public void setClient(Client client) {
        if (mClient != null) {
            mClient = client;
            refresh();
        }
    }

    public void refresh() {

        boolean client = mClient != null && mClient.isConnected();

        double width = avatarImage.getFitWidth();
        double height = avatarImage.getFitHeight();

        String name = Core.instance().user().username();
        String status = Core.instance().user().status();
        String about = Core.instance().user().aboutme();
        Image image = Core.instance().user().getAvatarImage(width, height);

        if (client) {
            name = mClient.getUserData().getUserName() + " ";
            status = mClient.getUserData().getStatus() + " ";
            about = mClient.getUserData().getAboutMe() + " ";
            image = mClient.getUserData().getAvatar(width, height);
        }

        userName.setText(name);
        statusText.setText(status);
        aboutMe.setText(about);
        avatarImage.setImage(image);

        aboutMe.setEditable(!client);
        userName.setEditable(!client);
        statusText.setEditable(!client);
        messageButton.setVisible(client);
        videoCallButton.setVisible(client);

        if (!client) {
            userName.setCursor(Cursor.TEXT);
            statusText.setCursor(Cursor.TEXT);
            avatarButton.setCursor(Cursor.HAND);
            aboutGridPane.getColumnConstraints().get(1).setMinWidth(0);
            aboutGridPane.getColumnConstraints().get(1).setMaxWidth(0);
        } else {
            userName.setCursor(Cursor.DEFAULT);
            statusText.setCursor(Cursor.DEFAULT);
            avatarButton.setCursor(Cursor.DEFAULT);
            aboutGridPane.getColumnConstraints().get(1).setMinWidth(60);
            aboutGridPane.getColumnConstraints().get(1).setMaxWidth(60);
        }
    }

    @FXML
    private void handleSendMessage(ActionEvent event) {
        Core.instance().main().selectMessaging();
    }

    @FXML
    private void handleSendCall(ActionEvent event) {
        Core.instance().main().selectVideoCall();
    }

    @FXML
    private void changeAvatar(ActionEvent event) {
        if (mClient == null) {
            FileChooser fc = new FileChooser();
            File init = new File(Database.instance().get("Initial Directory"));
            if (init.exists()) {
                fc.setInitialDirectory(init);
            }
            fc.getExtensionFilters().setAll(
                    new FileChooser.ExtensionFilter("Image Files",
                            "*.png", "*.jpg", "*.bmp", "*.gif"));
            fc.setTitle("Choose your avatar...");
            File choosen = fc.showOpenDialog(Core.instance().stage());
            if (choosen != null) {
                changeAvatar(choosen);
                Database.instance().set("Initial Directory",
                        choosen.getParentFile().toString());
            }
        } 
    }

    private void changeName() {
        if (mClient == null) {
            Core.instance().user().username(userName.getText());
        }
    }

    private void changeStatus() {
        if (mClient == null) {
            Core.instance().user().status(statusText.getText());
        }
    }

    private void changeAboutMe() {
        if (mClient == null) {
            Core.instance().user().aboutme(aboutMe.getText());
        }
    }

    private void changeAvatar(File choosen) {
        try {
            String uploaded = FileService.instance().upload(choosen);
            Image image = FileService.instance().getImage(uploaded);
            Core.instance().user().avatar(uploaded);
        } catch (IOException ex) {
            ExceptionDialog dialog = new ExceptionDialog(ex);
            dialog.setTitle("Failed to upload image");
            dialog.showAndWait();
        }
    }

}
