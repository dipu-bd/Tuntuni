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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.tuntuni.Core;
import org.tuntuni.connection.Client;
import org.tuntuni.models.Logs;
import org.tuntuni.videocall.DialStatus;

/**
 * Controller for video calling. It shows video in background.
 */
public class VideoCallController implements Initializable {

    Image mBlackImage;
    Image mCallImage;

    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private ImageView userPhoto;
    @FXML
    private Label userName;
    @FXML
    private ImageView videoImage;
    @FXML
    private CheckBox shareScreenChecker;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        buildImage();
        Core.instance().videocall(this);
        dialStatusChanged(DialStatus.IDLE);
        Core.instance().dialer().statusProperty().addListener((ov, o, n) -> {
            Platform.runLater(() -> dialStatusChanged(n));
        });
    }

    private void buildImage() {
        try {
            BufferedImage image = new BufferedImage(640, 480, 1);
            for (int i = 0; i < image.getWidth(); ++i) {
                for (int j = 0; j < image.getHeight(); ++j) {
                    image.setRGB(i, j, Color.black.getRGB());
                }
            }
            mBlackImage = SwingFXUtils.toFXImage(image, null);
            mCallImage = new Image(getClass().getResourceAsStream("/img/calling.gif"));
        } catch (Exception ex) {
        }
    }

    public Client client() {
        return Core.instance().selected();
    }

    public void refresh() {
        loadAll();
    }

    private void loadAll() {
        if (client() != null && client().getUserData() != null) {
            userName.setVisible(true);
            userName.setText(client().getUserData().getUserName());
            userPhoto.setImage(client().getUserData().getAvatar(
                    userPhoto.getFitWidth(), userPhoto.getFitHeight()));
        } else {
            userName.setVisible(false);
        }
    }

    public void acceptCallDialog(final Client client) {
        // set current client
        Core.instance().main().showUser(client);
        Core.instance().main().selectVideoCall();

        // create the custom dialog.
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Incoming Call!");
        dialog.setHeaderText(userName.getText());
        dialog.setContentText(userName.getText() + " is calling...\n"
                + "Do you want to accept this call?");

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
        if (result.isPresent() && result.get()) {
            Core.instance().dialer().acceptResponse(client, null);
        } else {
            Core.instance().dialer().acceptResponse(client, new Exception("Call was rejected"));
        }
    }

    public ImageView getViewer() {
        return videoImage;
    }

    public void dialStatusChanged(DialStatus status) {
        switch (status) {
            case IDLE:
                videoImage.setImage(mBlackImage);
                startButton.setText("Start Call");
                startButton.setDisable(false);
                stopButton.setText("End Call");
                stopButton.setDisable(true);
                break;

            case DIALING:
                videoImage.setImage(mCallImage);
                startButton.setText("Dialing...");
                startButton.setDisable(true);
                stopButton.setText("Cancel");
                stopButton.setDisable(false);
                break;

            case BUSY:
                videoImage.setImage(mBlackImage);
                startButton.setText("In Call");
                startButton.setDisable(true);
                stopButton.setText("End Call");
                stopButton.setDisable(false);
                break;
        }
    }

    @FXML
    private void startVideoCall(ActionEvent evt) {
        // dial
        Exception ex = Core.instance().dialer().dial(client());
        // if something went wrong
        if (ex != null) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Failure!");
            alert.setHeaderText("Dial Failed.");
            alert.setContentText("ERROR: " + ex.getMessage());
            alert.show();
        }
    }

    @FXML
    private void onShareScreenChecked(ActionEvent evt) {
        try {
            boolean sel = shareScreenChecker.isSelected();
            Core.instance().dialer().setShareScreen(sel);
        } catch (Exception ex) {
            Logs.error(getClass(), "Error: {0}", ex);
        }
    }

    @FXML
    private void endVideoCall(ActionEvent evt) {
        Core.instance().dialer().endCall();
    }

}
