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
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.tuntuni.Core;
import org.tuntuni.connection.Client;
import org.tuntuni.connection.StreamServer;
import org.tuntuni.video.AudioFrame;
import org.tuntuni.video.ImageFrame;

/**
 * Controller for video calling. It shows video in background.
 */
public class VideoCallController implements Initializable {

    private Client mClient;

    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private ImageView userPhoto;
    @FXML
    private Label userName;
    @FXML
    private Pane viewPane;

    private ImageView videoImage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Core.instance().videocall(this);
        videoImage = new ImageView();
        viewPane.getChildren().add(videoImage);
    }

    public void setClient(Client client) {
        mClient = client;
        loadAll();
    }

    private void loadAll() {
        if (mClient != null && mClient.getUserData() != null) {
            userName.setVisible(true);
            userName.setText(mClient.getUserData().getUserName());
            userPhoto.setImage(mClient.getUserData().getAvatar(
                    userPhoto.getFitWidth(), userPhoto.getFitHeight()));
        } else {
            userName.setVisible(false);
        }
        startButton.setVisible(true);
        stopButton.setVisible(false);
    }

    public void acceptCallDialog(Client client) {
        Platform.runLater(() -> {
            // set current client
            setClient(client);
            Core.instance().main().selectVideoCall();

            // create the custom dialog.
            Dialog<Boolean> dialog = new Dialog<>();
            dialog.setTitle("Incoming Call!");
            dialog.setHeaderText(userName.getText());
            dialog.setContentText("<b>" + userName.getText() + "</b> is calling you."
                    + " Do you want to accept this call?");

            // set the icon 
            dialog.setGraphic(new ImageView(userPhoto.getImage()));

            // set the button types.
            ButtonType acceptButton = new ButtonType("Accept", ButtonData.OK_DONE);
            ButtonType declineButton = new ButtonType("Decline", ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(acceptButton, declineButton);

            // define result converter 
            dialog.setResultConverter(dialogButton -> {
                return dialogButton == acceptButton;
            });

            // return the result
            Optional<Boolean> result = dialog.showAndWait();
            result.ifPresent((consumer) -> {
                Core.instance().dialer().informAcceptance(consumer.booleanValue());
            });
        });
    }

    @FXML
    private void startVideoCall(ActionEvent evt) {
        try {

            Core.instance().dialer().dialClient(mClient);

        } catch (Exception ex) {

            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Call failed!");
            alert.setHeaderText("Call failed!");
            alert.setContentText("ERROR: " + ex.getMessage());

            alert.showAndWait();
        }
    }

    @FXML
    private void endVideoCall(ActionEvent evt) {

        Core.instance().dialer().endCall();
    }

    public ImageView getVideoImage() {
        return videoImage;
    }

    public void callStarting() {
        Platform.runLater(() -> {
            startButton.setVisible(false);
            stopButton.setVisible(false);
        });
    }

    public void prepareDisplay() {
        Platform.runLater(() -> {
            startButton.setVisible(false);
            stopButton.setVisible(true);
        });
    }

    public void callEnded() {
        Platform.runLater(() -> {
            startButton.setVisible(true);
            stopButton.setVisible(false);
        });
    }
}
