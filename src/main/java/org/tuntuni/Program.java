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
package org.tuntuni;

import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The entry point of the application.
 */
public class Program extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // set current stage to core
        Core.instance().stage(stage);
        Core.instance().start();
        // catch on close
        stage.setOnCloseRequest((evt) -> Core.instance().close());

        // get the parent node
        Parent root = (Parent) FXMLLoader.load(
                getClass().getResource("/fxml/Main.fxml"));
        // build the default scene
        Scene scene = new Scene(root);
        // add custom styles to the scene
        scene.getStylesheets().add("/css/default.css");

        // prepare the stage
        stage.setTitle(Core.instance().meta().title());
        // set the scene to stage
        stage.setScene(scene);
        // display the stage
        stage.show();

        // call other ui methods
        Platform.runLater(() -> {
            Core.instance().profile().setClient(null);
            Core.instance().messaging().setClient(null);
            Core.instance().videocall().setClient(null);
        });
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
