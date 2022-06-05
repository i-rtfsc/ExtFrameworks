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

import android.content.Context;

import system.ext.utils.JosLog;

public class ExtSystemServer implements IExtSystemServer {
    private static final String TAG = ExtSystemServer.class.getSimpleName();

    public ExtSystemServer() {
    }

    @Override
    public void initSystemServer(Context systemContext) {
        JosLog.i(TAG, "init system server");
    }

    @Override
    public void startBootstrapServices() {
        JosLog.i(TAG, "start bootstrap services");
    }

    @Override
    public void startCoreServices() {
        JosLog.i(TAG, "start core services");
    }

    @Override
    public void startOtherServices() {
        JosLog.i(TAG, "start other services");
    }

}
