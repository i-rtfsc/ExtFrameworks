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
import com.journeyOS.server.godeye.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import system.ext.utils.JosLog;

public class MonitorManager {
    private static final String TAG = MonitorManager.class.getSimpleName();

    private static volatile MonitorManager sInstance = null;

    private final ConcurrentHashMap<Long, BaseMonitor> mMonitors = new ConcurrentHashMap<>();
    private final List<OnSceneListener> mListeners = new ArrayList<OnSceneListener>();

    private Scene mScene = new Scene();
    private Context mContext;

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

    public void init(Context context) {
        mContext = context;
    }

    protected boolean init(long factors) {
        if ((factors & GodEyeManager.SCENE_FACTOR_APP) != 0) {
            BaseMonitor monitor = mMonitors.get(GodEyeManager.SCENE_FACTOR_APP);
            if (monitor == null) {
                monitor = PackageNameMonitor.getInstance();
                monitor.init(mContext, GodEyeManager.SCENE_FACTOR_APP);
                mMonitors.put(GodEyeManager.SCENE_FACTOR_APP, monitor);
            }
        }

        if ((factors & GodEyeManager.SCENE_FACTOR_CAMERA) != 0) {
            BaseMonitor monitor = mMonitors.get(GodEyeManager.SCENE_FACTOR_CAMERA);
            if (monitor == null) {
                monitor = CameraMonitor.getInstance();
                monitor.init(mContext, GodEyeManager.SCENE_FACTOR_CAMERA);
                mMonitors.put(GodEyeManager.SCENE_FACTOR_CAMERA, monitor);
            }
        }

        if ((factors & GodEyeManager.SCENE_FACTOR_AUDIO) != 0 ||
                (factors & GodEyeManager.SCENE_FACTOR_VIDEO) != 0) {
            BaseMonitor monitor = mMonitors.get(GodEyeManager.SCENE_FACTOR_AUDIO);
            if (monitor == null) {
                monitor = MediaMonitor.getInstance();
                monitor.init(mContext, GodEyeManager.SCENE_FACTOR_AUDIO);
                mMonitors.put(GodEyeManager.SCENE_FACTOR_AUDIO, monitor);
            }
        }

        if ((factors & GodEyeManager.SCENE_FACTOR_BRIGHTNESS) != 0) {
            BaseMonitor monitor = mMonitors.get(GodEyeManager.SCENE_FACTOR_BRIGHTNESS);
            if (monitor == null) {
                monitor = BrightnessMonitor.getInstance();
                monitor.init(mContext, GodEyeManager.SCENE_FACTOR_BRIGHTNESS);
                mMonitors.put(GodEyeManager.SCENE_FACTOR_BRIGHTNESS, monitor);
            }
        }

        if ((factors & GodEyeManager.SCENE_FACTOR_TEMPERATURE) != 0) {
            BaseMonitor monitor = mMonitors.get(GodEyeManager.SCENE_FACTOR_TEMPERATURE);
            if (monitor == null) {
                monitor = TemperatureMonitor.getInstance();
                monitor.init(mContext, GodEyeManager.SCENE_FACTOR_TEMPERATURE);
                mMonitors.put(GodEyeManager.SCENE_FACTOR_TEMPERATURE, monitor);
            }
        }

        return true;
    }

    public boolean start(long factors) {
        init(factors);

        boolean result = false;

        if ((factors & GodEyeManager.SCENE_FACTOR_APP) != 0) {
            BaseMonitor monitor = mMonitors.get(GodEyeManager.SCENE_FACTOR_APP);
            result = monitor.start();
        }

        if ((factors & GodEyeManager.SCENE_FACTOR_CAMERA) != 0) {
            BaseMonitor monitor = mMonitors.get(GodEyeManager.SCENE_FACTOR_CAMERA);
            result = monitor.start();
        }

        if ((factors & GodEyeManager.SCENE_FACTOR_AUDIO) != 0 ||
                (factors & GodEyeManager.SCENE_FACTOR_VIDEO) != 0) {
            BaseMonitor monitor = mMonitors.get(GodEyeManager.SCENE_FACTOR_AUDIO);
            result = monitor.start();
        }

        if ((factors & GodEyeManager.SCENE_FACTOR_BRIGHTNESS) != 0) {
            BaseMonitor monitor = mMonitors.get(GodEyeManager.SCENE_FACTOR_BRIGHTNESS);
            result = monitor.start();
        }

        if ((factors & GodEyeManager.SCENE_FACTOR_TEMPERATURE) != 0) {
            BaseMonitor monitor = mMonitors.get(GodEyeManager.SCENE_FACTOR_TEMPERATURE);
            result = monitor.start();
        }

        return result;
    }

    public boolean stop(long factors) {
        boolean result = false;

        if ((factors & GodEyeManager.SCENE_FACTOR_APP) != 0) {
            BaseMonitor monitor = mMonitors.get(GodEyeManager.SCENE_FACTOR_APP);
            result = monitor.stop();
        }

        if ((factors & GodEyeManager.SCENE_FACTOR_CAMERA) != 0 ||
                (factors & GodEyeManager.SCENE_FACTOR_CAMERA) != 0) {
            BaseMonitor monitor = mMonitors.get(GodEyeManager.SCENE_FACTOR_CAMERA);
            result = monitor.stop();
        }

        if ((factors & GodEyeManager.SCENE_FACTOR_AUDIO) != 0 ||
                (factors & GodEyeManager.SCENE_FACTOR_VIDEO) != 0) {
            BaseMonitor monitor = mMonitors.get(GodEyeManager.SCENE_FACTOR_AUDIO);
            result = monitor.stop();
        }

        if ((factors & GodEyeManager.SCENE_FACTOR_BRIGHTNESS) != 0) {
            BaseMonitor monitor = mMonitors.get(GodEyeManager.SCENE_FACTOR_BRIGHTNESS);
            result = monitor.stop();
        }

        if ((factors & GodEyeManager.SCENE_FACTOR_TEMPERATURE) != 0) {
            BaseMonitor monitor = mMonitors.get(GodEyeManager.SCENE_FACTOR_TEMPERATURE);
            result = monitor.stop();
        }

        return result;
    }

    public void notifyResult(Scene scene) {
        notifyResult(scene, false);
    }

    public void notifyResult(Scene scene, boolean fakeData) {
        if (!fakeData) {
            mScene = scene;
        }
        synchronized (mScene) {
            for (OnSceneListener listener : mListeners) {
                listener.onChanged(scene);
            }
        }

        for (Map.Entry<Long, BaseMonitor> entry : mMonitors.entrySet()) {
            BaseMonitor monitor = entry.getValue();
            monitor.onChanged(scene);
        }
    }

    public Scene getPreviewScene() {
        synchronized (mScene) {
            return mScene;
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
