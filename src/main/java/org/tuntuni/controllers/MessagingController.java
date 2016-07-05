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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javax.swing.DefaultListCellRenderer;
import org.tuntuni.Core;
import org.tuntuni.components.MessageBox;
import org.tuntuni.components.UserItem;
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
    private final ExecutorService mExecutor;
    
    @FXML
    private TextArea messageText;
    @FXML
    private ListView messageList;
    
    public MessagingController() {
        mExecutor = Executors.newCachedThreadPool();
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
        messageText.clear();
        messageText.setEditable(mClient != null);
        // load list past messages

    }
    
    @FXML
    private void handleSendMessage(ActionEvent event) {
        String text = messageText.getText().trim();
        if (text.isEmpty()) {
            return;
        }
        mExecutor.submit(() -> sendMessage(text));
        messageText.clear();
    }
    
    private void sendMessage(String text) {
        Message message = new Message();
        message.setText(text);
        if (!mClient.message(message)) {
            showAlert("Could not send message.");
            messageText.setText(text + "\r\n" + messageText.getText());
            return;
        }
        mClient.addMessage(message);
        message.setClient(mClient);
    }
    
    private void showHistory() {
        messageList.getItems().clear();
        mClient.messageProperty().stream().forEach((message) -> { 
            messageList.getItems().add(MessageBox.createInstance(message));
        });
    }
    
    private void showAlert(String message) {
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }
}
