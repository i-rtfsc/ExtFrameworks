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

package com.journeyOS.server.godeye.monitor;

import android.os.FileObserver;

import com.journeyOS.server.godeye.GodEyeManager;

import system.ext.utils.JosLog;

public class BrightnessMonitor extends BaseMonitor {
    private static final String TAG = BrightnessMonitor.class.getSimpleName();

    private static volatile BrightnessMonitor sInstance = null;

    private BrightnessFileObserver mBrightnessFileObserver = null;

    private BrightnessMonitor() {
    }

    public static BrightnessMonitor getInstance() {
        if (sInstance == null) {
            synchronized (BrightnessMonitor.class) {
                if (sInstance == null) {
                    sInstance = new BrightnessMonitor();
                }
            }
        }
        return sInstance;
    }

    @Override
    protected void onInit(long factoryId) {
    }

    @Override
    protected void onStart() {
        if (mBrightnessFileObserver == null) {
            mBrightnessFileObserver = new BrightnessFileObserver();
        }
        mBrightnessFileObserver.startWatching();
    }

    @Override
    protected void onStop() {
        if (mBrightnessFileObserver != null) {
            mBrightnessFileObserver.stopWatching();
        }
    }


    private class BrightnessFileObserver extends FileObserver {
        private static final String BRIGHTNESS_FILE = "/sys/class/backlight/panel0-backlight/brightness";

        public BrightnessFileObserver() {
            super(BRIGHTNESS_FILE, FileObserver.MODIFY);
        }

        @Override
        public void onEvent(int event, String path) {
            JosLog.w(GodEyeManager.GOD_EYE_TAG, TAG, "path " + path);
        }
    }

}
