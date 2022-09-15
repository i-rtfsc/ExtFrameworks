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

import com.journeyOS.server.godeye.GodEyeManager;
import com.journeyOS.server.godeye.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import system.ext.utils.JosLog;

public class MonitorManager {
    private static final String TAG = MonitorManager.class.getSimpleName();

    private static volatile MonitorManager sInstance = null;

    private final ConcurrentHashMap<Long, BaseMonitor> mMonitors = new ConcurrentHashMap<>();
    private final List<OnSceneListener> mListeners = new ArrayList<OnSceneListener>();

    private Scene mScene;

    private MonitorManager() {
    }

    public static MonitorManager getInstance() {
        if (sInstance == null) {
            synchronized (MonitorManager.class) {
                if (sInstance == null) {
                    sInstance = new MonitorManager();
                }
            }
        }
        return sInstance;
    }

    protected boolean init(long factors) {
        if ((factors & GodEyeManager.SCENE_FACTOR_BRIGHTNESS) != 0) {
            BaseMonitor monitor = mMonitors.get(GodEyeManager.SCENE_FACTOR_BRIGHTNESS);
            if (monitor == null) {
                monitor = BrightnessMonitor.getInstance();
                monitor.init(GodEyeManager.SCENE_FACTOR_BRIGHTNESS);
                mMonitors.put(GodEyeManager.SCENE_FACTOR_BRIGHTNESS, monitor);
            }
        }
        return true;
    }

    public boolean start(long factors) {
        init(factors);

        boolean result = false;

        if ((factors & GodEyeManager.SCENE_FACTOR_BRIGHTNESS) != 0) {
            BaseMonitor monitor = mMonitors.get(GodEyeManager.SCENE_FACTOR_BRIGHTNESS);
            result = monitor.start();
        }
        return result;
    }

    public boolean stop(long factors) {
        boolean result = false;
        if ((factors & GodEyeManager.SCENE_FACTOR_BRIGHTNESS) != 0) {
            BaseMonitor monitor = mMonitors.get(GodEyeManager.SCENE_FACTOR_BRIGHTNESS);
            result = monitor.stop();
        }

        return result;
    }

    public synchronized void notifyResult(Scene scene) {
        notifyResult(scene, false);
    }

    public synchronized void notifyResult(Scene scene, boolean fakeData) {
        if (!fakeData) {
            mScene = scene;
        }

        for (OnSceneListener listener : mListeners) {
            listener.onChanged(scene);
        }
    }

    /**
     * Registers a callback to be invoked on voice command result.
     *
     * @param listener The callback that will run.
     */
    public void registerListener(OnSceneListener listener) {
        if (listener == null) {
            JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, "listener should not be null");
            return;
        }

        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    /**
     * Unregisters a previous callback.
     *
     * @param listener The callback that should be unregistered.
     * @see #registerListener
     */
    public void unregisterListener(OnSceneListener listener) {
        if (listener == null) {
            JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, "listener should not be null");
            return;
        }

        mListeners.remove(listener);
    }

    public interface OnSceneListener {
        void onChanged(Scene scene);
    }
}
