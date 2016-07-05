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
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.tuntuni.connection.Client;
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
            // set client
            mbox.setMessage(message);
            //post load work      
            mbox.initialize();
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
    private ImageView receiverAvatar;
    @FXML
    private Label arrivalTime;
    @FXML
    private Label messageBody;

    private Message mMessage;

    public void setMessage(Message message) {
        mMessage = message;
    }
    
    private void initialize() {
        assert mMessage != null;
        
        
    }

}
