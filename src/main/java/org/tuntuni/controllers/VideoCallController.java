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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
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
    
    private VideoFormat mFormat;
    private VideoCapturer mCapturer;
    private VideoRenderer mRenderer;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Core.instance().videocall(this);
        
        mFormat = new VideoFormat();
        mCapturer = new VideoCapturer(mFormat);
    }     
 
    public void setClient(Client client) {
        mClient = client;
        loadAll();
    }

    private void loadAll() {
        
        if(mClient != null && mClient.getUserData() != null) {
            userName.setText(mClient.getUserData().getUserName());
            userPhoto.setImage(mClient.getUserData().getAvatar(
                    userPhoto.getFitWidth(), userPhoto.getFitHeight()));
        }
    }
    
    @FXML
    private void startVideoCall(ActionEvent evt) {
        
    }
    
}
