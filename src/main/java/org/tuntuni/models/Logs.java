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
package org.tuntuni.models;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lists some log messages used through-out the application.
 */
public final class Logs {

    // instance of context
    private static final class LogsHolder {

        private static final Logger INSTANCE = Logger.getGlobal();
    }

    /**
     * Gets an instance of this class.
     *
     * @return
     */
    public static final Logger instance() {
        return LogsHolder.INSTANCE;
    }

    public static Logger getLogger() {
        return instance();
    }

    public static void addHandler(Handler handler) {
        instance().addHandler(handler);
    }

    public static void log(Level level, String message, Object... data) {
        instance().log(level, message, data);
    }

    public static void log(Level level, Class source, String message, Object... data) {
        log(level, "[" + source.getSimpleName() + "] "+ message, data);
    }

    public static void info(String message, Object... data) {
        log(Level.INFO, message, data);
    }

    public static void info(Class from, String message, Object... data) {
        log(Level.INFO, from, message, data);
    }

    public static void info(String type, String message, Object... data) {
        info("[" + type + "] " + message, data);
    }

    public static void severe(String message, Object... data) {
        log(Level.SEVERE, message, data);
    }

    public static void warning(String message, Object... data) {
        log(Level.WARNING, message, data);
    }

    public static void warning(String type, String message, Object... data) {
        warning("[" + type + "] " + message, data);
    }

    public static void warning(Class from, String message, Object... data) {
        log(Level.WARNING, from, message, data);
    }

    public static void error(String message, Object... data) {
        severe(message, data);
    }

    public static void error(Class from, String message, Object... data) {
        log(Level.SEVERE, from, message, data);
    }

    public static void error(String type, String message, Object... data) {
        error("[" + type + "] " + message, data);
    }

    public static void config(String message, Object... data) {
        log(Level.CONFIG, message, data);
    }

    public static void config(Class from, String message, Object... data) {
        log(Level.CONFIG, from, message, data);
    }

}
