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

import com.sun.javafx.tk.Toolkit;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import org.tuntuni.Core;
import org.tuntuni.components.MessageBox;
import org.tuntuni.connection.Client;
import org.tuntuni.models.Message;

/**
 * The controller for text messaging and file sharing.
 * <p>
 * It gives a history based text conversation window. Below is a text box and
 * send text button. Above is the conversation history. </p>
 */
public class MessagingController implements Initializable {
    
    private Client mClient;
    
    @FXML
    private TextArea messageText;
    @FXML
    private ListView messageList;
    @FXML
    private Label errorLabel;
    
    private final double MINIMUM_HEIGHT = 70D;
    private final double MAXIMUM_HEIGHT = 150D;
    
    public MessagingController() {
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Core.instance().messaging(this);
    }
    
    public void setClient(Client client) {
        mClient = client;
        loadAll();
    }
    
    private void loadAll() {
        errorLabel.setText("");
        messageText.clear();
        messageList.getItems().clear();

        // load list of past messages
        messageList.getItems().clear();
        if (mClient != null) {
            showHistory();
            mClient.messageProperty().get().addListener(
                    (ListChangeListener.Change<? extends Message> c) -> {
                        showHistory();
                    });
        }
    }
    
    private void showHistory() {
        int elems = messageList.getItems().size();
        for (int i = elems; i < mClient.messageProperty().getSize(); ++i) {
            Message message = mClient.messageProperty().get(i);
            messageList.getItems().add(MessageBox.createInstance(message));
        }
        if (messageList.getItems().size() > 0) {
            messageList.scrollTo(messageList.getItems().size() - 1);
        }
    }
    
    @FXML
    private void handleMessageTyped(KeyEvent evt) {
        if (evt.getCharacter().length() > 0) {
            setTextAreaHeight();
            if (evt.getCharacter().charAt(0) == 13) {
                if (evt.isShiftDown()) {                    
                    messageText.appendText("\n");
                } else {
                    handleSendMessage(null);        
                    evt.consume();
                }
            }
        }
    }
    
    @FXML
    private void handleSendMessage(ActionEvent event) {
        errorLabel.setText("");
        String text = messageText.getText().trim();
        
        if (text.isEmpty()) {
            errorLabel.setText("Message is too short");
            return;
        }
        sendMessage(text);
    }
    
    private void sendMessage(String text) {
        if (mClient == null) {
            errorLabel.setText("The receiver is unknown");
            return;
        }
        
        Message message = new Message();
        message.setText(text);
        if (!mClient.sendMessage(message)) {
            errorLabel.setText("Could not send message.");
            return;
        }
        
        mClient.addMessage(message);
        message.setClient(mClient);
        messageText.clear();
    }
    
    private void setTextAreaHeight() {
        // elements
        Region content = (Region) messageText.lookup(".content");
        ScrollBar scrollBarv = (ScrollBar) messageText.lookup(".scroll-bar:vertical");
        // get the height of content
        double height = MINIMUM_HEIGHT;
        if (messageText.getText().length() > 10) {
            Insets padding = messageText.getPadding();
            height = content.getHeight() + padding.getTop() + padding.getBottom();
        }
        // set height
        height = Math.max(MINIMUM_HEIGHT, height);
        height = Math.min(MAXIMUM_HEIGHT, height);
        messageText.setMinHeight(height);
        messageText.setPrefHeight(height);
        // enable or the scroll bar
        scrollBarv.setVisibleAmount(MAXIMUM_HEIGHT);
    }
}
