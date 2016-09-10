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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.System.out;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    /**
     * Calculate the integer number from an array of bytes.
     * <p>
     * IPv4 address byte array must be 4 bytes long</p>
     *
     * @param bytes Array of bytes to convert.
     * @return
     */
    public static int bytesToInt(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    /**
     * Converts an integer to byte array.
     * <p>
     * IPv4 address byte array must be 4 bytes long</p>
     *
     * @param number Integer number to convert.
     * @return
     */
    public static byte[] intToBytes(int number) {
        return ByteBuffer.allocate(4).putInt(number).array();
    }

    /**
     * Convert any object to byte array
     * @param obj
     * @return 
     */
    public static byte[] toBytes(Object obj) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            return baos.toByteArray();
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Get the object from byte array
     * @param <T>
     * @param data
     * @param expectedClass
     * @return 
     */
    public static <T> T fromBytes(byte[] data, Class<T> expectedClass) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
                ObjectInputStream ois = new ObjectInputStream(bais)) {
            return expectedClass.cast(ois.readObject());
        } catch (IOException | ClassNotFoundException ex) { 
            return null;
        }
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

    public static Image resizeImage(Image img, double width, double height) {
        byte[] data = imageToBytes(img);
        try (ByteArrayInputStream byteInput = new ByteArrayInputStream(data)) {
            return new Image(byteInput, width, height, true, true);
        } catch (IOException ex) {
            return null;
        }
    }
}
