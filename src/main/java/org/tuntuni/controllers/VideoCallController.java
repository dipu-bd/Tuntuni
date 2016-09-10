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
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import org.tuntuni.Core;
import org.tuntuni.connection.Client;
import org.tuntuni.video.VideoCapturer;
import org.tuntuni.video.VideoFormat;
import org.tuntuni.video.VideoRenderer;

/**
 * Controller for video calling. It shows video in background.
 */
public class VideoCallController implements Initializable {

    private Client mClient;

    @FXML
    private Button callButton;
    @FXML
    private ImageView userPhoto;
    @FXML
    private Label userName;
    @FXML
    private Pane viewPane;

    private VideoFormat myFormat;
    private VideoFormat userFormat;
    private VideoCapturer mCapturer;
    private VideoRenderer mRenderer;
    private Background mPaneBG;

    private Property<Image> mImage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Core.instance().videocall(this);

        mImage = new SimpleObjectProperty<>(null);
        mImage.setValue(new Image(getClass()
                .getResourceAsStream("/img/calling.gif")));

        mImage.addListener((ov, n, o) -> {
            if (mImage.getValue() == null) {
                viewPane.setBackground(null);
            } else {
                BackgroundImage bgImg = new BackgroundImage(mImage.getValue(),
                        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
                viewPane.setBackground(new Background(bgImg));
            }
        });
    }

    public void setClient(Client client) {
        mClient = client;
        loadAll();
    }

    public VideoFormat acceptCall() {
        startCall();
        return myFormat;
    }

    private void loadAll() {
        if (mClient != null && mClient.getUserData() != null) {
            userName.setVisible(true);
            callButton.setVisible(true);
            userName.setText(mClient.getUserData().getUserName());
            userPhoto.setImage(mClient.getUserData().getAvatar(
                    userPhoto.getFitWidth(), userPhoto.getFitHeight()));
        } else {
            userName.setVisible(false);
            callButton.setVisible(false);
        }
    }

    @FXML
    private void startVideoCall(ActionEvent evt) {
        if (callButton.getUserData() == null) {
            startCall();
        } else {
            stopCall();
        }
    }

    private void startCall() {
        myFormat = new VideoFormat();
        mCapturer = new VideoCapturer(myFormat);
        mCapturer.initialize();
        mCapturer.start();
        myFormat.setAudioPort(mCapturer.getAudioPort());
        myFormat.setImagePort(mCapturer.getImagePort());

        mImage.setValue(new Image(
                getClass().getResourceAsStream("/img/calling.gif")));
        callButton.setText("Calling...");
        callButton.setDisable(true);

        mImage.setValue(new Image(getClass()
                .getResourceAsStream("/img/calling.gif")));

        (new Thread(() -> makeCall())).start();
    }

    private void makeCall() {
        try {
            userFormat = mClient.getFormat();
            userFormat.setInetAddress(mClient.getHostString());
            mRenderer = new VideoRenderer(userFormat, (img) -> {
                mImage.setValue(img);
            });
            mRenderer.initialize();
            mRenderer.start();

            Platform.runLater(() -> {
                callButton.setUserData(false);
                callButton.setDisable(false);
                callButton.setText("Stop Call");
            });
        } catch (Exception ex) {
            stopCall();
        }
    }

    private void stopCall() {
        Platform.runLater(() -> {
            mImage.setValue(null);
            callButton.setUserData(null);
            callButton.setDisable(false);
            callButton.setText("Send Call");
        });
        mCapturer.stop();
        mRenderer.stop();
    }
}
