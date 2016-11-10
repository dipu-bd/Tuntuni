/*
 * Copyright 2016 Sudipto Chandra.
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
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.controlsfx.control.Notifications;
import org.tuntuni.Core;
import org.tuntuni.connection.Client;
import org.tuntuni.models.Logs;

/**
 * The controller for the main scene of this application.
 * <p>
 * It has two parts. One is side-bar. Another is the body. The side-bar is
 * positioned at the right side and collapsible. The body is divided into two
 * parts. One for text messaging. Another for video chatting.
 * </p>
 *
 */
public class MainController implements Initializable {

    @FXML
    private ListView userList;
    @FXML
    private Label statusLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TabPane tabPane;
    @FXML
    private Button profileButton;

//    private double userListPrefWidth;
    private volatile Client mSelected;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // set main controller to core
        Core.instance().main(this);
//        userListPrefWidth = userList.getPrefWidth();

        // capture status
        Logs.addHandler(new StatusHandler(statusLabel));

        // change of user name
        profileButton.setText(Core.instance().user().getName());
        Core.instance().user().userNameProperty().addListener((ov, o, n) -> {
            Platform.runLater(() -> profileButton.setText(n));
        });

        // change of setAvatar image
        final ImageView profileImage = (ImageView) profileButton.getGraphic();
        ChangeListener updateAvatar = (ov, o, n) -> {
            Image avatar = Core.instance().user().getAvatarImage(32, 32);
            profileImage.setImage(avatar);
        };
        updateAvatar.changed(null, null, null);
        Core.instance().user().avatarProperty().addListener(updateAvatar);

        // add existing users
        userList.getItems().clear();
        updateUserList(null, null);
        Core.instance().scanner().userListProperty().values().forEach((client) -> {
            Platform.runLater(() -> updateUserList(client, null));
        });
        // monitor user list
        Core.instance().scanner().userListProperty().addListener((MapChangeListener.Change<? extends Integer, ? extends Client> change) -> {
            Platform.runLater(() -> updateUserList(change.getValueAdded(), change.getValueRemoved()));
        });

        showUser(null);
    }

    // updates the user list
    private void updateUserList(Client add, Client remove) {
        // add item
        if (add != null) {
            UserItem item = UserItem.createInstance(add);
            userList.getItems().add(item);
            add.connectedProperty().addListener((ov, n, o) -> {
                if (add == mSelected) {
                    disconnectNotice();
                }
            });
        }
        // remove item
        if (remove != null) {
            Iterator<UserItem> it = userList.getItems().iterator();
            while (it.hasNext()) {
                UserItem item = it.next();
                if (item.getClient().equals(remove)) {
                    it.remove();
                    break;
                }
            }
        }
        // hide user list if empty 
//        if (userList.getItems().isEmpty()) {
//            userList.setPrefWidth(0.0);
//        } else {
//            userList.setPrefWidth(userListPrefWidth);
//        }
        // refresh profile view to show last user information
        //Core.instance().profile().refresh();
    }

    /**
     * Gets the currently selected client; Or null if none is selected.
     *
     * @return
     */
    public Client selectedClient() {
        return mSelected;
    }

    /**
     * Select a client and display profile immediately
     *
     * @param client
     */
    public void showUser(Client client) {
        if (client != null && !client.isConnected()) {
            return;
        }
        mSelected = client;
        refreshAll();
    }

    public void disconnectNotice() {
        if (mSelected == null) {
            return;
        }
        Platform.runLater(() -> {
            String msg = String.format(
                    "Sorry. We lost connection with %s (%s).",
                    mSelected.getUserName(), mSelected.toString());
            Notifications.create().title("User Disconnected!").text(msg).showError();
            showUser(null);
        });
    }

    public void refreshAll() {
        Platform.runLater(() -> {
            // refresh all contents
            Core.instance().profile().refresh();
            Core.instance().messaging().refresh();
            Core.instance().videocall().refresh();
            // select profile if necessary
            if (mSelected == null && !isProfile()) {
                selectProfile();
            }
            // disable or enable tab header
            ((Node) tabPane.lookup(".tab-header-area")).setDisable(mSelected == null);
        });
    }

    /**
     * Select and show the profile tab
     */
    public void selectProfile() {
        tabPane.getSelectionModel().select(0);
    }

    /**
     * Select and show the messaging tab
     */
    public void selectMessaging() {
        tabPane.getSelectionModel().select(1);
    }

    /**
     * Select and show the video call tab
     */
    public void selectVideoCall() {
        tabPane.getSelectionModel().select(2);
    }

    public boolean isProfile() {
        return tabPane.getSelectionModel().isSelected(0);
    }

    public boolean isMessaging() {
        return tabPane.getSelectionModel().isSelected(1);
    }

    public boolean isVideoCall() {
        return tabPane.getSelectionModel().isSelected(2);
    }

    @FXML
    private void handleProfileAction(ActionEvent event) {
        showUser(null);
    }


}
