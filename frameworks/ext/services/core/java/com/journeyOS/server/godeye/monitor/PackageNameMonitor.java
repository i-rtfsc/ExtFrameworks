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

import com.android.server.HookSystemConfig;
import com.journeyOS.server.godeye.GodEyeManager;
import com.journeyOS.server.godeye.Scene;

import system.ext.utils.JosLog;

public class PackageNameMonitor extends BaseMonitor {
    private static final String TAG = PackageNameMonitor.class.getSimpleName();

    private static final String UNKNOWN = "unknown";
    private static final String ALBUM = "album";
    private static final String BROWSER = "browser";
    private static final String GAME = "game";
    private static final String IM = "im";
    private static final String MUSIC = "music";
    private static final String NEWS = "news";
    private static final String READER = "reader";
    private static final String VIDEO = "video";

    private static volatile PackageNameMonitor sInstance = null;

    private PackageNameMonitor() {
    }

    public static PackageNameMonitor getInstance() {
        if (sInstance == null) {
            synchronized (PackageNameMonitor.class) {
                if (sInstance == null) {
                    sInstance = new PackageNameMonitor();
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

    public void activityResumed(String packageName) {
        JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, "activity resumed, packageName = [" + packageName + "]");
    }

    public int convertType(String packageName) {
        int type = Scene.App.UNKNOWN.ordinal;
        String appType = HookSystemConfig.get().getGodEyeAppType(packageName);
        switch (appType) {
            case ALBUM:
                type = Scene.App.ALBUM.ordinal;
                break;
            case BROWSER:
                type = Scene.App.BROWSER.ordinal;
                break;
            case GAME:
                type = Scene.App.GAME.ordinal;
                break;
            case IM:
                type = Scene.App.IM.ordinal;
                break;
            case MUSIC:
                type = Scene.App.MUSIC.ordinal;
                break;
            case NEWS:
                type = Scene.App.NEWS.ordinal;
                break;
            case READER:
                type = Scene.App.READER.ordinal;
                break;
            case VIDEO:
                type = Scene.App.VIDEO.ordinal;
                break;
            default:
                type = Scene.App.UNKNOWN.ordinal;
                break;
        }

        return type;
    }

}
