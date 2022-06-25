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

    static HookSystemConfig get() {
        return (HookSystemConfig) Inject.getInstance().getInject(HookSystemConfig.class);
    }

    void init(SystemConfig systemConfig);

    default void readPermissionsFromXml(File file) {
    }

    default boolean isExtFile(File file) {
        return false;
    }

    default boolean supportBackgroundService(String packageName) {
        return false;
    }
}
