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
import android.app.ActivityTaskManager;
import android.app.TaskStackListener;
import android.content.Context;
import android.os.RemoteException;

import com.journeyOS.server.godeye.GodEyeManager;
import com.journeyOS.server.godeye.Scene;

import system.ext.utils.JosLog;

public class PackageNameMonitor extends BaseMonitor {
    private static final String TAG = PackageNameMonitor.class.getSimpleName();
    private static final boolean DEBUG = false;

    public static final String UNKNOWN = "unknown";
    public static final String ALBUM = "album";
    public static final String BROWSER = "browser";
    public static final String GAME = "game";
    public static final String IM = "im";
    public static final String MUSIC = "music";
    public static final String NEWS = "news";
    public static final String READER = "reader";
    public static final String VIDEO = "video";

    private static volatile PackageNameMonitor sInstance = null;

    private ActivityTaskManager mAtm = null;
    private AppTaskStackListener mListener = null;

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
        if (mAtm == null) {
            mAtm = (ActivityTaskManager) mContext.getSystemService(Context.ACTIVITY_TASK_SERVICE);
        }

        if (mListener == null) {
            mListener = new AppTaskStackListener();
        }
        mAtm.registerTaskStackListener(mListener);
    }

    @Override
    protected void onStop() {
        mAtm.unregisterTaskStackListener(mListener);
    }

    private class AppTaskStackListener extends TaskStackListener {
        @Override
        public void onTaskMovedToFront(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
            super.onTaskMovedToFront(taskInfo);
            String packageName = taskInfo.topActivity.getPackageName();
            String activity = taskInfo.topActivity.getClassName();
            JosLog.v(GodEyeManager.GOD_EYE_TAG, TAG, "on task moved to front, packageName = [" + packageName + "], activity = [" + activity + "]");

            Scene scene = getPreviewScene();
            scene.setFactorId(GodEyeManager.SCENE_FACTOR_APP);
            scene.setPackageName(packageName);
            scene.setApp(convertApp(packageName));
            notifyResult(scene);
        }

        @Override
        public void onTaskDescriptionChanged(ActivityManager.RunningTaskInfo taskInfo) throws RemoteException {
            super.onTaskDescriptionChanged(taskInfo);
            if (DEBUG) {
                String packageName = taskInfo.topActivity.getPackageName();
                String activity = taskInfo.topActivity.getClassName();
                JosLog.v(GodEyeManager.GOD_EYE_TAG, TAG, "on task description changed, packageName = [" + packageName + "], activity = [" + activity + "]");
            }
        }
    }

}
