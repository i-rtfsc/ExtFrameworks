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

package com.journeyOS.server.vrr;

import android.os.IBinder;
import android.util.ArrayMap;
import android.util.Singleton;
import android.view.SurfaceControl;

import com.android.server.wm.HookWindowState;

import system.ext.utils.JosLog;

public class VrrSurfaceControlProxy {
    private static final String TAG = VrrSurfaceControlProxy.class.getSimpleName();
    private static final boolean DEBUG = true;

    private static final Singleton<VrrSurfaceControlProxy> gDefault = new Singleton<VrrSurfaceControlProxy>() {
        @Override
        protected VrrSurfaceControlProxy create() {
            return new VrrSurfaceControlProxy();
        }
    };

    private static final ArrayMap<Integer, Float> DISPLAY_MODE = new ArrayMap<>();
    private IBinder mDisplayToken = null;

    public VrrSurfaceControlProxy() {
        initDisplayMode();
    }

    public static VrrSurfaceControlProxy getDefault() {
        return gDefault.get();
    }

    private void initDisplayMode() {
        IBinder displayToken = getPhysicalDisplayToken();
        SurfaceControl.DynamicDisplayInfo dynamicDisplayInfo = SurfaceControl.getDynamicDisplayInfo(displayToken);
        for (SurfaceControl.DisplayMode mode : dynamicDisplayInfo.supportedDisplayModes) {
            JosLog.d(VRRManager.VRR_TAG, TAG, "mode id = [" + mode.id + "], refreshRate = [" + mode.refreshRate + "]");
            DISPLAY_MODE.put(mode.id, mode.refreshRate);
        }
    }

    public IBinder getPhysicalDisplayToken() {
        if (mDisplayToken == null) {
            final long[] physicalDisplayIds = SurfaceControl.getPhysicalDisplayIds();
            if (physicalDisplayIds.length == 0) {
                return null;
            }
            mDisplayToken = SurfaceControl.getPhysicalDisplayToken(physicalDisplayIds[0]);
        }

        return mDisplayToken;
    }

    public int getActiveDisplayModeId() {
        IBinder displayToken = getPhysicalDisplayToken();
        SurfaceControl.DynamicDisplayInfo dynamicDisplayInfo = SurfaceControl.getDynamicDisplayInfo(displayToken);
        if (DEBUG) {
            JosLog.d(VRRManager.VRR_TAG, TAG, "dynamic display info = [" + dynamicDisplayInfo.toString() + "]");
        }
        return dynamicDisplayInfo.activeDisplayModeId;
    }


    public float getRefreshRate(int modeId) {
        return DISPLAY_MODE.get(modeId);
    }

    public int findDefaultModeIdByRefreshRate(float refreshRate) {
        for (Integer modeId : DISPLAY_MODE.keySet()) {
            if (Math.round(DISPLAY_MODE.get(modeId)) == Math.round(refreshRate)) {
                return modeId;
            }
        }

        return 0;
    }

    public float getPreferredRefreshRate() {
        return HookWindowState.get().getPreferredRefreshRate();
    }

}
