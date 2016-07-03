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

import java.io.InputStream;
import javafx.scene.image.Image;

/**
 *
 */
public class Resources {

    public Resources() {
    }

    public String getPath(String fileName) {
        return Resources.class.getResource("/img/" + fileName).toString();
    }

    public InputStream getStream(String fileName) {
        return Resources.class.getResourceAsStream("/img/" + fileName);
    }

    public Image getImage(String fileName) {
        return new Image(getStream(fileName));
    }
}