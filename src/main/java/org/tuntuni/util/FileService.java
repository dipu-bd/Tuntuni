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

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import javafx.scene.image.Image;
import org.apache.commons.io.FileUtils;

/**
 * For accessing and managing files and folders
 */
public class FileService {

    private static FileService mFileService;

    public static FileService instance() {
        if (mFileService == null) {
            mFileService = new FileService();
        }
        return mFileService;
    }

    private final File mFolder;

    public FileService() {
        mFolder = new File(System.getProperty("user.home")
                + File.separator + ".tuntuni");
        mFolder.mkdirs();
    }

    public boolean exists(String... relative) {
        return resolve(relative).exists();
    }

    public File resolve(String... names) {
        return FileUtils.getFile(mFolder, names);
    }

    public String relative(File path) {
        return mFolder.toURI().relativize(path.toURI()).toString();
    }

    public String upload(File file) throws IOException {
        String random = UUID.randomUUID().toString();
        File dest = resolve("uploads", random);
        FileUtils.copyFile(file, dest);
        return relative(dest);
    }

    public Image getImage(String path) {
        if (exists(path)) {
            return new Image(resolve(path).toURI().toString());
        } else if ((new File(path)).exists()) {
            return new Image(path);
        }
        return null;
    }

    public byte[] read(String fileName) {
        try {
            // check relative path
            if (resolve(fileName).exists()) {
                return FileUtils.readFileToByteArray(resolve(fileName));
            }
            // otherwise non-relative
            return FileUtils.readFileToByteArray(resolve(fileName));

        } catch (IOException ex) {
            // failure. return empty byte
            return new byte[0];
        }
    }

}
