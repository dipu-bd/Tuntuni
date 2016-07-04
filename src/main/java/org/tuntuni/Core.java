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

import java.util.logging.Logger;
import java.util.logging.Level;
import javafx.stage.Stage;
import org.tuntuni.connection.Server;
import org.tuntuni.connection.Subnet;
import org.tuntuni.models.MetaData;
import org.tuntuni.models.UserProfile;
import org.tuntuni.controllers.MainController;
import org.tuntuni.controllers.MessagingController;
import org.tuntuni.controllers.ProfileController; 
import org.tuntuni.controllers.VideoCallController;
import org.tuntuni.models.Logs;

/**
 * To handle inter-application communication. An instance of this class can only
 * be access by {@linkplain Core.instance()}.
 * <p>
 * The server and subnet objects are created with constructor. But they are not
 * yet started. Don't forget to call the {@code start()} of Server and Subnet
 * instances, after the initialization phase is done.</p>
 * <p>
 * Note that, the controllers must be set after they are initialized. e.g: To
 * set MainController call {@code Core.instance().main(this)} in the
 * {@code MainController.initialize()} method.</p>
 */
public final class Core {

    // logger
    private static final Logger logger = Logger.getGlobal();

    // instance of context
    private static final Core mInstance = new Core();

    /**
     * Gets an instance of this class.
     *
     * @return
     */
    public static final Core instance() {
        return mInstance;
    }

    // private variables
    private final Server mServer;
    private final Subnet mSubnet;
    private Stage mPrimaryStage;
    // data 
    private final MetaData mMeta;
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
        mServer = new Server();
        mSubnet = new Subnet();
        mMeta = new MetaData();
        mUser = new UserProfile();
    }

    public void start() {
        mServer.start();
        mSubnet.start();
    }

    public void close() {
        logger.log(Level.INFO, Logs.PROGRAM_CLOSING);
        // stop all
        mServer.stop();
        mSubnet.stop();
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
    public Server server() {
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
     * Gets the meta data
     *
     * @return an instance of the object
     */
    public MetaData meta() {
        return mMeta;
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
