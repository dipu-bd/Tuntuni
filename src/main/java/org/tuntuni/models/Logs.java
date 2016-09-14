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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lists some log messages used through-out the application.
 */
public abstract class Logs {

    private static final Logger logger = Logger.getGlobal();
 
    public static void log(Level level, String message, Object... data) {
        logger.log(level, message, data);
    }

    public static void info(String message, Object... data) {
        log(Level.INFO, message, data);
    }

    public static void info(Class from, String message, Object... data) {
        info(from.getName(), message, data);
    }

    public static void info(String type, String message, Object... data) {
        info("[" + type + "]" + message, data);
    }

    public static void severe(String message, Object... data) {
        log(Level.SEVERE, message, data);
    }

    public static void warning(String message, Object... data) {
        log(Level.WARNING, message, data);
    }

    public static void warning(String type, String message, Object... data) {
        warning("[" + type + "]" + message, data);
    }

    public static void warning(Class from, String message, Object... data) {
        warning(from.getName(), message, data);
    }

    public static void error(String message, Object... data) {
        severe(message, data);
    }

    public static void error(Class from, String message, Object... data) {
        error(from.getName(), message, data);
    }

    public static void error(String type, String message, Object... data) {
        error("[" + type + "]" + message, data);
    }

    public static void config(String message, Object... data) {
        log(Level.CONFIG, message, data);
    }

}
