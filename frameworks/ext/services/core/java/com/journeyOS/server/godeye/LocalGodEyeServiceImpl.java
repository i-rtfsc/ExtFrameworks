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

package com.journeyOS.server.godeye;

import android.content.Context;

import com.journeyOS.server.godeye.clients.ClientSession;
import com.journeyOS.server.godeye.monitor.MonitorManager;

import java.util.HashMap;
import java.util.Map;

import system.ext.utils.JosLog;

/**
 * GodEyeService
 */
public class LocalGodEyeServiceImpl implements LocalGodEyeService, MonitorManager.OnSceneListener {
    private static final String TAG = LocalGodEyeServiceImpl.class.getSimpleName();

    private final Context mContext;

    private final HashMap<GodEyeListener, Long> mListeners = new HashMap<GodEyeListener, Long>();

    public LocalGodEyeServiceImpl(Context context) {
        this.mContext = context;
        MonitorManager.getInstance().init(mContext);
        MonitorManager.getInstance().registerListener(this);
    }

    @Override
    public void registerListener(long factors, GodEyeListener listener) {
        if (!mListeners.containsKey(listener)) {
            mListeners.put(listener, factors);
        }
        MonitorManager.getInstance().start(factors);
    }

    @Override
    public void unregisterListener(GodEyeListener listener) {
        if (mListeners.containsKey(listener)) {
            mListeners.remove(listener);
        }
    }

    public boolean checkFactorFromLocal(long factors) {
        boolean exist = mListeners.isEmpty() ? true : false;

        for (Map.Entry<GodEyeListener, Long> entry : mListeners.entrySet()) {
            long localFactors = entry.getValue();

            if ((localFactors & factors) != 0) {
                exist = true;
            }

            if (exist) {
                break;
            }
        }

        return exist;
    }

    @Override
    public void onChanged(Scene scene) {
        JosLog.v(GodEyeManager.GOD_EYE_TAG, TAG, "on changed, scene = [" + scene.toString() + "]");
        JosLog.v(GodEyeManager.GOD_EYE_TAG, TAG, "on changed, listener isEmpty  = [" + mListeners.isEmpty() + "]");
        for (Map.Entry<GodEyeListener, Long> entry : mListeners.entrySet()) {
            GodEyeListener listener = entry.getKey();
            listener.onSceneChanged(scene);
        }

        ClientSession.getInstance().dispatchFactorEvent(scene);
    }
}
