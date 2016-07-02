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
package org.tuntuni.connection;

import org.tuntuni.models.MetaData;
import org.tuntuni.models.Status;
import org.tuntuni.models.UserProfile;

/**
 * To build proper response for a request coming from server.
 */
public class ServerRoute {

    /**
     * A function to which the server requests to get response.
     *
     * @param status the status of the request
     * @param data parameters sent by client
     * @return The response object. Can be {@code null}.
     */
    public static Object request(Status status, Object... data) {
        switch (status) {
            case TEST:
                return test();
            case META:
                return meta();
            case PROFILE:
                return profile();
        }
        return null;
    }

    // what to do when Status.TEST request arrived
    public static Object test() {
        return Server.SECRET_CODE;
    }

    // what to do when Status.META request arrived
    public static Object meta() {
        return MetaData.build();
    }

    // what to do when Status.PROFILE request arrived
    public static Object profile() {
        return UserProfile.build();
    }
}
