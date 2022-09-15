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

import android.content.Context;

import com.journeyOS.server.godeye.GodEyeManager;

import system.ext.utils.JosLog;

public class AudioMonitor extends BaseMonitor {
    private static final String TAG = AudioMonitor.class.getSimpleName();

    private static volatile AudioMonitor sInstance = null;

    private AudioMonitor() {
    }

    public static AudioMonitor getInstance() {
        if (sInstance == null) {
            synchronized (AudioMonitor.class) {
                if (sInstance == null) {
                    sInstance = new AudioMonitor();
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
    }

    @Override
    protected void onStop() {
    }

    public void onMusicStarted(Context context, int callingPid) {
        String packageName = getProcessName(context, callingPid);
        JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, "on music started, callingPid = [" + callingPid + "], packageName = [" + packageName + "]");
    }

    public void onMusicStopped(Context context, int callingPid) {
        String packageName = getProcessName(context, callingPid);
        JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, "on music stopped, callingPid = [" + callingPid + "], packageName = [" + packageName + "]");
    }

}
