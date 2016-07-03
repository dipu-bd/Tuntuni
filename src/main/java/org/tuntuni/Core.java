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

import javafx.scene.Scene;
import javafx.stage.Stage;
import org.tuntuni.connection.Server;
import org.tuntuni.connection.Subnet;
import org.tuntuni.controller.Main;
import org.tuntuni.controller.SideBar;

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
 * {@code Main.initialize()} method.</p>
 */
public class Core {

    // instance of context
    private static final Core mInstance = new Core();

    /**
     * Gets an instance of Core object.
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
    private Main mMainController;
    private SideBar mSideBarController;

    // Creates a new context. hidden from public.
    private Core() {
        mServer = new Server();
        mSubnet = new Subnet();
    }

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
     * @param primaryStage
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
     * Sets the MainController. Set when the main controller is initialized.
     *
     * @param main
     */
    public void main(Main main) {
        mMainController = main;
    }

    /**
     * Gets the main controller if it is initialized, otherwise a {@code null}
     * value is returned.
     *
     * @return
     */
    public Main main() {
        return mMainController;
    }

    /**
     * Sets the SideBar controller. Set when the side-bar controller is
     * initialized.
     *
     * @param sidebar
     */
    public void sidebar(SideBar sidebar) {
        mSideBarController = sidebar;
    }

    /**
     * Gets the side-bar controller if it is initialized, otherwise a
     * {@code null} value is returned
     *
     * @return
     */
    public SideBar sidebar() {
        return mSideBarController;
    }
}
