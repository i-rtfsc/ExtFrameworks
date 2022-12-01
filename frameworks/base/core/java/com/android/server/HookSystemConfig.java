/*
 * Copyright (c) 2022 anqi.huang@outlook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server;

import java.io.File;

import system.ext.hook.Inject;

public interface HookSystemConfig {

    /**
     * get the implementation class name from interface object
     * if implementation class not exists, return interface object
     *
     * @return implementation object
     */
    static HookSystemConfig get() {
        Object inject = Inject.getInstance().getInject(HookSystemConfig.class);
        if (inject == null) {
            return new HookSystemConfig() {
            };
        } else {
            return (HookSystemConfig) inject;
        }
    }

    /**
     * implementation object hold param object
     *
     * @param systemConfig @see com.android.server.SystemConfig
     */
    default void init(SystemConfig systemConfig) {
    }

    /**
     * read config file
     *
     * @param file config file
     */
    default void readPermissionsFromXml(File file) {
    }

    /**
     * check this config file is "ext" file
     *
     * @param file config file
     * @return true if "ext" file
     */
    default boolean isExtFile(File file) {
        return false;
    }

    /**
     * Check packages that are running in background
     *
     * @param packageName packages
     * @return true can running in background
     */
    default boolean supportBackgroundService(String packageName) {
        return false;
    }

    /**
     * get app type
     *
     * @param packageName packages
     * @return app type
     */
    default String getGodEyeAppType(String packageName) {
        return "UNKNOWN";
    }

    /**
     * album app
     *
     * @param packageName packages
     * @return true if album
     */
    default boolean isAlbum(String packageName) {
        return false;
    }

    /**
     * browser app
     *
     * @param packageName packages
     * @return true if browser
     */
    default boolean isBrowser(String packageName) {
        return false;
    }

    /**
     * game app
     *
     * @param packageName packages
     * @return true if game
     */
    default boolean isGame(String packageName) {
        return false;
    }

    /**
     * im app
     *
     * @param packageName packages
     * @return true if im
     */
    default boolean isIM(String packageName) {
        return false;
    }

    /**
     * music app
     *
     * @param packageName packages
     * @return true if music
     */
    default boolean isMusic(String packageName) {
        return false;
    }

    /**
     * news app
     *
     * @param packageName packages
     * @return true if news
     */
    default boolean isNews(String packageName) {
        return false;
    }

    /**
     * reader app
     *
     * @param packageName packages
     * @return true if reader
     */
    default boolean isReader(String packageName) {
        return false;
    }

    /**
     * video app
     *
     * @param packageName packages
     * @return true if video
     */
    default boolean isVideo(String packageName) {
        return false;
    }
}
