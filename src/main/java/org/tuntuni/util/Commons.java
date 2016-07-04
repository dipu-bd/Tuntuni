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
package org.tuntuni.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javax.imageio.ImageIO;

/**
 * Some commonly used functions and methods
 */
public class Commons {

    /**
     * Create a new custom pane from FXML data. <br>
     * Restraints: Name and package of FXML file should be the same as
     * <code>resourceClass</code>. <code>resourceClass</code> should extend
     * <code>BorderPane</code> or one of its descendents.
     *
     * @param resourcePath Resource path to FXML file.
     * @return Pane type object containing loaded node.
     * @throws java.io.IOException
     */
    public static Pane loadPaneFromFXML(String resourcePath) throws IOException {
        //init loader           
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Commons.class.getResource(resourcePath));
         
        //load fxml
        Node node = (Node) loader.load();
        BorderPane control = (BorderPane) loader.getController();

        BorderPane.setAlignment(node, Pos.CENTER);
        control.setCenter(node);

        return control;
    }

    public static byte[] imageToBytes(Image img) {
        try (ByteArrayOutputStream byteOutput = new ByteArrayOutputStream()) {
            ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", byteOutput);
            return byteOutput.toByteArray();
        } catch (IOException ex) {
            return null;
        }
    }

    public static Image bytesToImage(byte[] data) {
        try (ByteArrayInputStream byteInput = new ByteArrayInputStream(data)) {
            return new Image(byteInput);
        } catch (IOException ex) {
            return null;
        }
    }

    public static Image bytesToImage(byte[] data, int height, int width) {
        try (ByteArrayInputStream byteInput = new ByteArrayInputStream(data)) {
            return new Image(byteInput, height, width, true, true);
        } catch (IOException ex) {
            return null;
        }
    }
}
