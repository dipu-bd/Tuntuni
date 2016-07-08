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

import com.sun.org.apache.xalan.internal.xsltc.trax.SAX2StAXBaseWriter;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

/**
 * Defined a few types of connection status client and server.
 */
public enum Status implements Externalizable {

    STATE,
    // to pass user profile information
    PROFILE,
    // to pass a single message
    MESSAGE;

    @Override
    public void writeExternal(ObjectOutput oo) throws IOException {

    }

    @Override
    public void readExternal(ObjectInput oi) throws IOException, ClassNotFoundException {

    }
}