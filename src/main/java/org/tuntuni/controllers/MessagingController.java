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
import javafx.fxml.Initializable;
import org.tuntuni.Core;

/**
 * The controller for text messaging and file sharing.
 * <p>
 * It gives a history based text conversation window. Below is a text box and
 * send text button. Above is the conversation history. </p>
 */
public class MessagingController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Core.instance().messaging(this);
    }

}
