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
package org.tuntuni.video;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.junit.BeforeClass;
import org.junit.Test;
import org.tuntuni.videocall.Dialer;
import org.tuntuni.videocall.VideoPlayer;
import org.tuntuni.videocall.VideoRecorder; 

/**
 *
 * @author dipu
 */
public class StreammingTest extends Application {

    ImageView mViewer;
    private VideoPlayer mPlayer;
    private VideoRecorder mRecorder;

    public StreammingTest() {
        mViewer = new ImageView();
    }

    @BeforeClass
    public static void start() {
        launch(new String[]{});
    }

    public void setUp() throws SocketException, UnknownHostException, IOException {
        System.out.println(":: Video Streamming Test ::");

        System.out.println(">>> starting player");
        mPlayer = new VideoPlayer(mViewer);
        mPlayer.start();

        System.out.println("Image port = " + mPlayer.getImagePort() + "{" + Dialer.IMAGE_PORT + "}");
        System.out.println("Audio port = " + mPlayer.getAudioPort() + "{" + Dialer.AUDIO_PORT + "}");

        System.out.println(">>> starting recorder");
        mRecorder = new VideoRecorder(InetAddress.getLocalHost());
        mRecorder.start();
    }

    public void tearDown() {
        System.out.println(">>> stopping");
        mPlayer.stop();
        mRecorder.stop();
    }

    @Test
    public void test() throws InterruptedException, Exception {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        mViewer.setFitHeight(480);
        mViewer.setFitWidth(640);
        BorderPane root = new BorderPane();
        root.setCenter(mViewer);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        primaryStage.show();

        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            tearDown();
        });

        setUp();
    }

}
