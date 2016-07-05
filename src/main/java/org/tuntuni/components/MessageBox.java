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
import java.util.Date;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import org.ocpsoft.prettytime.PrettyTime;
import org.tuntuni.Core;
import org.tuntuni.models.Message;
import org.tuntuni.util.Commons;

/**
 * A single message item view
 */
public class MessageBox extends BorderPane {

    public static MessageBox createInstance(Message message) {
        try {
            // build the component
            MessageBox mbox = (MessageBox) Commons.loadPaneFromFXML("/fxml/MessageBox.fxml");
            //post load work                           
            mbox.initialize(message);
            // return main object
            return mbox;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @FXML
    private ImageView senderAvatar;
    @FXML
    private Label timegapLabel;
    @FXML
    private Label messageBody;

    private Message mMessage;

    private Date mDate;
    private final PrettyTime mPrettyTime;
    private final Timeline mTimeline;

    private MessageBox() {
        mDate = new Date();
        mPrettyTime = new PrettyTime();
        mTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), (evt) -> updateTime()));
    }

    private void initialize(Message message) {
        mMessage = message;

        mDate = mMessage.getTime();
        messageBody.setText(mMessage.getText());

        mTimeline.setCycleCount(Animation.INDEFINITE);
        mTimeline.play();

        if (mMessage.isReceiver()) {
            senderAvatar.setImage(mMessage.getSender().getAvatar(
                    senderAvatar.getFitWidth(), senderAvatar.getFitHeight()));
            senderAvatar.setVisible(true);
            this.setId("message-receiver");
        } else {
            senderAvatar.setVisible(false);
            this.setId("message-sender");
        }
    }

    // updates the view of time
    private void updateTime() {
        timegapLabel.setText(mPrettyTime.format(mDate));
    }

    @FXML
    private void handleShowSender(ActionEvent evt) {
        Core.instance().profile().setClient(mMessage.getClient());
        Core.instance().main().selectProfile();
    }

}
