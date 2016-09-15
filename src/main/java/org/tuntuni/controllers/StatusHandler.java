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

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import javafx.application.Platform;
import javafx.scene.control.Label;

/**
 *
 * @author Sudipto Chandra
 */
public class StatusHandler extends Handler {

    final int SHOW_TIME = 5000;

    private final Label mStatusLabel;
    private final Formatter mLogFormatter;

    private Timer mTimer = new Timer(true);
    private volatile long mLastUpdate = 0;
    private volatile String mMessageToDisplay;

    public StatusHandler(Label statusLabel) {
        mStatusLabel = statusLabel;
        mLogFormatter = new SimpleFormatter();
        mLastUpdate = 0;
        mMessageToDisplay = "";
        mTimer = new Timer(true);
        mTimer.scheduleAtFixedRate(mTask, 0, 1000);
    }

    private final TimerTask mTask = new TimerTask() {
        @Override
        public void run() {
            long diff = System.currentTimeMillis() - mLastUpdate;
            if (diff > SHOW_TIME) {
                mMessageToDisplay = "";
            }
            setStatus(mMessageToDisplay);
        }
    };

    private void clearStatus() {
        mMessageToDisplay = "";
    }

    private void setStatus(String msg) {
        Platform.runLater(() -> mStatusLabel.setText(msg));
    }

    @Override
    public void publish(LogRecord lr) {
        mLastUpdate = System.currentTimeMillis();
        mMessageToDisplay = mLogFormatter.formatMessage(lr);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }

}
