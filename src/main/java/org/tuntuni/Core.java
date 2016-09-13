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
package org.tuntuni;

import java.net.SocketException;
import javafx.stage.Stage;
import org.tuntuni.connection.SubnetServer;
import org.tuntuni.connection.MainServer;
import org.tuntuni.connection.Subnet;
import org.tuntuni.models.UserProfile;
import org.tuntuni.controllers.MainController;
import org.tuntuni.controllers.MessagingController;
import org.tuntuni.controllers.ProfileController;
import org.tuntuni.controllers.VideoCallController;
import org.tuntuni.video.Dialer;

/**
 * To handle inter-application communication. An instance of this class can only
 * be access by {@linkplain Core.instance()}.
 * <p>
 * The server and subnet objects are created with constructor. But they are not
 * yet started. Don't forget to call the {@code start()} of MainServer and
 * Subnet instances, after the initialization phase is done.</p>
 * <p>
 * Note that, the controllers must be set after they are initialized. e.g: To
 * set MainController call {@code Core.instance().main(this)} in the
 * {@code MainController.initialize()} method.</p>
 */
public final class Core {

    public static final int PORTS[] = {
        24914, //PRIMARY_PORT
        42016, //BACKUP_PORT  
    };

    // instance of context
    private static class CoreHolder {

        public static final Core INSTANCE = new Core();
    }

    /**
     * Gets an instance of this class.
     *
     * @return
     */
    public static final Core instance() {
        return CoreHolder.INSTANCE;
    }

    // private variables
    private final MainServer mServer;
    private final Subnet mSubnet;
    private Stage mPrimaryStage;
    // multicast server
    private SubnetServer mSubnetServer;
    // dialer
    private Dialer mDialer;
    // data  
    private final UserProfile mUser;
    // controllers
    private MainController mMain;
    private VideoCallController mVideoCall;
    private MessagingController mMessaging;
    private ProfileController mProfile;

    // Creates a new context. hidden from public.
    private Core() {
        // order might be important here
        // put simple & light constructors first 
        mServer = new MainServer();
        mSubnet = new Subnet();
        mUser = new UserProfile();
        mDialer = new Dialer();
        // create a listenner server for network discovery
        for (int port : PORTS) {
            try {
                mSubnetServer = new SubnetServer(port);
            } catch (SocketException ex) {
            }
        }
    }

    /**
     * Starts all server and background tasks
     */
    public void start() {
        mServer.start();
        mSubnetServer.start();
        mSubnet.start();
    }

    /**
     * Stop server and background tasks
     */
    public void close() {
        mSubnet.stop();
        mSubnetServer.stop();
        mServer.stop();
        mDialer.endCall();
    }

    ///
    ////////////////////////////////////////////////////////////////////////////
    /// READONLY DATA: getter functions for readonly variables
    ////////////////////////////////////////////////////////////////////////////
    ///
    /**
     * Get an instance of the server.
     *
     * @return
     */
    public MainServer server() {
        return mServer;
    }

    /**
     * Get an instance of the subnet scanner
     *
     * @return
     */
    public Subnet subnet() {
        return mSubnet;
    }

    /**
     * Get instance of the subnet discovery server
     *
     * @return
     */
    public SubnetServer scanner() {
        return mSubnetServer;
    }

    /**
     * Gets the dialer class for video calling
     *
     * @return
     */
    public Dialer dialer() {
        return mDialer;
    }

    /**
     * Sets the primary stage of the application.
     *
     * @param primaryStage stage to assign
     */
    public void stage(Stage primaryStage) {
        mPrimaryStage = primaryStage;
    }

    /**
     * Gets the primary stage of the application.
     *
     * @return
     */
    public Stage stage() {
        return mPrimaryStage;
    }

    /**
     * Gets the current user user.
     *
     * @return an instance of the object
     */
    public UserProfile user() {
        return mUser;
    }

    ///
    ////////////////////////////////////////////////////////////////////////////
    /// CONTROLLERS : getter and setter functions for controllers
    ////////////////////////////////////////////////////////////////////////////
    ///
    /**
     * Sets the MainController. Set when the main controller is initialized.
     *
     * @param main controller to assign
     */
    public void main(MainController main) {
        mMain = main;
    }

    /**
     * Gets the main controller if it is initialized, otherwise a {@code null}
     * value is returned.
     *
     * @return the assigned controller
     */
    public MainController main() {
        return mMain;
    }

    /**
     * Sets the ProfileController controller. Set when the controller is
     * initialized.
     *
     * @param profile controller to assign
     */
    public void profile(ProfileController profile) {
        mProfile = profile;
    }

    /**
     * Gets the profile controller if it is initialized, otherwise a
     * {@code null} value is returned
     *
     * @return the assigned controller
     */
    public ProfileController profile() {
        return mProfile;
    }

    /**
     * Sets the VideoCallController controller. Set when the controller is
     * initialized.
     *
     * @param videocall controller to assign
     */
    public void videocall(VideoCallController videocall) {
        mVideoCall = videocall;
    }

    /**
     * Gets the VideoCallController controller if it is initialized, otherwise a
     * {@code null} value is returned
     *
     * @return
     */
    public VideoCallController videocall() {
        return mVideoCall;
    }

    /**
     * Sets the MessagingController controller. Set when the controller is
     * initialized.
     *
     * @param messaging
     */
    public void messaging(MessagingController messaging) {
        mMessaging = messaging;
    }

    /**
     * Gets the MessagingController controller if it is initialized, otherwise a
     * {@code null} value is returned
     *
     * @return
     */
    public MessagingController messaging() {
        return mMessaging;
    }

}
