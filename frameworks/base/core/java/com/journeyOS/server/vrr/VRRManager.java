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
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Singleton;

import system.ext.utils.JosLog;

public class VRRManager {
    public static final String VRR_TAG = "VRR";
    public static final String VRR_SERVICE = "vrr";
    private static final String TAG = VRRManager.class.getSimpleName();
    private static final boolean DEBUG = true;
    private static final Singleton<VRRManager> gDefault = new Singleton<VRRManager>() {
        @Override
        protected VRRManager create() {
            return new VRRManager();
        }
    };

    private IVariableRefreshRateService mService;

    private VRRManager() {
        getService();
    }

    public static VRRManager getDefault() {
        return gDefault.get();
    }

    private IVariableRefreshRateService getService() {
        if (mService == null) {
            try {
                IBinder binder = ServiceManager.getService(VRR_SERVICE);
                if (binder == null) {
                    JosLog.e(VRR_TAG, TAG, "can't get service binder");
                }
                mService = IVariableRefreshRateService.Stub.asInterface(binder);
                if (mService == null) {
                    JosLog.e(VRR_TAG, TAG, "can't get service interface");
                }
            } catch (Exception e) {
                JosLog.e(VRR_TAG, TAG, "can't get service interface");
                e.printStackTrace();
                mService = null;
            }
        }

        return mService;
    }

    public void setRefreshRatePolicy(int displayId, float rate, int policy, boolean statusOn) {
        JosLog.d(VRR_TAG, TAG, "setRefreshRatePolicy() called with: displayId = [" + displayId + "], rate = [" + rate + "], policy = [" + policy + "], statusOn = [" + statusOn + "]");
        IVariableRefreshRateService service = getService();
        if (service != null) {
            try {
                service.setRefreshRatePolicy(displayId, rate, policy, statusOn);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            JosLog.w(VRR_TAG, TAG, "vrr service disconnected");
        }
    }

}
