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

import android.app.ActivityManager;
import android.content.Context;

import com.android.server.HookSystemConfig;
import com.journeyOS.server.godeye.GodEyeManager;
import com.journeyOS.server.godeye.Scene;

import java.util.List;

import system.ext.utils.JosLog;

public abstract class BaseMonitor {
    private static final String TAG = BaseMonitor.class.getSimpleName();

    protected Context mContext;
    protected long mFactoryId;
    protected boolean mInit = false;
    protected boolean mStart = false;

    public final synchronized boolean init(Context context, long factoryId) {
        mContext = context;
        mFactoryId = factoryId;
        if (mInit) {
            JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, this.getClass().getSimpleName() + " is already inited");
        } else {
            JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, this.getClass().getSimpleName() + " init , factoryId = [" + mFactoryId + "]");
            onInit(factoryId);
            mInit = true;
        }
        return mInit;
    }

    public final synchronized boolean start() {
        if (mStart) {
            JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, this.getClass().getSimpleName() + " is already started");
        } else {
            JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, this.getClass().getSimpleName() + " start , factoryId = [" + mFactoryId + "]");
            onStart();
            mStart = true;
        }
        return mStart;
    }

    public final synchronized boolean stop() {
        boolean ret = false;
        if (!mStart) {
            JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, this.getClass().getSimpleName() + " is already stopped");
        } else {
            JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, this.getClass().getSimpleName() + " stop , factoryId = [" + mFactoryId + "]");
            onStop();
            mInit = false;
            mStart = false;
        }
        return ret;
    }

    public final synchronized boolean isStarted() {
        return mStart;
    }

    protected abstract void onInit(long factoryId);

    protected abstract void onStart();

    protected abstract void onStop();

    protected void onChanged(Scene scene) {
    }

    protected String getProcessName(int pid) {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> apps = activityManager.getRunningAppProcesses();
        if (apps != null) {
            for (int i = 0; i < apps.size(); i++) {
                ActivityManager.RunningAppProcessInfo app = apps.get(i);
                if (app.pid == pid) {
                    if (app.pkgList != null) {
                        return app.pkgList[0];
                    } else {
                        return app.processName;
                    }
                }
            }
        }
        return null;
    }

    public static int convertApp(String packageName) {
        int type = Scene.App.DEFAULT;
        String appType = HookSystemConfig.get().getGodEyeAppType(packageName);
        switch (appType) {
            case PackageNameMonitor.ALBUM:
                type = Scene.App.ALBUM;
                break;
            case PackageNameMonitor.BROWSER:
                type = Scene.App.BROWSER;
                break;
            case PackageNameMonitor.GAME:
                type = Scene.App.GAME;
                break;
            case PackageNameMonitor.IM:
                type = Scene.App.IM;
                break;
            case PackageNameMonitor.MUSIC:
                type = Scene.App.MUSIC;
                break;
            case PackageNameMonitor.NEWS:
                type = Scene.App.NEWS;
                break;
            case PackageNameMonitor.READER:
                type = Scene.App.READER;
                break;
            case PackageNameMonitor.VIDEO:
                type = Scene.App.VIDEO;
                break;
            default:
                type = Scene.App.DEFAULT;
                break;
        }

        return type;
    }

    protected Scene getPreviewScene() {
        return MonitorManager.getInstance().getPreviewScene();
    }

    protected void notifyResult(Scene scene) {
        MonitorManager.getInstance().notifyResult(scene);
    }
}
