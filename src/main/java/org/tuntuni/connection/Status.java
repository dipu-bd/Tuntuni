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
package org.tuntuni.connection;

/**
 * Defined a few types of connection status client and server.
 */
public enum Status {

    //
    // The enumeration fields
    //
    INVALID("The format is not valid"),
    TEST("Test the connection"),
    USER("User profile information");

    //
    // Section to handle extra information passed with enumeration type 
    //
    // to hold message
    private final String mMessage;

    // empty constructor
    Status() {
        this("");
    }

    // constructor with a message
    Status(String message) {
        mMessage = message;
    }

    /**
     * Gets the message with this {@linkplain Status} type, if any.
     *
     * @return The message or an empty string.
     */
    public String message() {
        return mMessage;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", name(), message());
    }
}
