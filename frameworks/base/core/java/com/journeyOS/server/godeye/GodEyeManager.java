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

import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Singleton;

import system.ext.utils.JosLog;

public class GodEyeManager {
    public static final String GOD_EYE_TAG = "GodEye";
    public static final String GOD_EYE_SERVICE = "god_eye";

    public static final long SCENE_FACTOR_UNKNOWN = -1;
    public static final long SCENE_FACTOR_APP = 1 << 1;
    public static final long SCENE_FACTOR_CAMERA = 1 << 2;
    public static final long SCENE_FACTOR_VIDEO = 1 << 3;
    public static final long SCENE_FACTOR_AUDIO = 1 << 4;
    public static final long SCENE_FACTOR_BRIGHTNESS = 1 << 5;
    public static final long SCENE_FACTOR_TEMPERATURE = 1 << 6;
    //public static final long SCENE_FACTOR_TOUCH = 1 << 7;

    private static final String TAG = GodEyeManager.class.getSimpleName();
    private static final boolean DEBUG = true;
    private static final Singleton<GodEyeManager> gDefault = new Singleton<GodEyeManager>() {
        @Override
        protected GodEyeManager create() {
            return new GodEyeManager();
        }
    };
    private IGodEyeService mService;

    private GodEyeManager() {
        getService();
    }

    public static GodEyeManager getDefault() {
        return gDefault.get();
    }

    private IGodEyeService getService() {
        if (mService == null) {
            try {
                IBinder binder = ServiceManager.getService(GOD_EYE_SERVICE);
                if (binder == null) {
                    JosLog.e(GOD_EYE_TAG, TAG, "can't get service binder");
                }
                mService = IGodEyeService.Stub.asInterface(binder);
                if (mService == null) {
                    JosLog.e(GOD_EYE_TAG, TAG, "can't get service interface");
                }
            } catch (Exception e) {
                JosLog.e(GOD_EYE_TAG, TAG, "can't get service interface");
                e.printStackTrace();
                mService = null;
            }
        }

        return mService;
    }

    public boolean subscribeObserver(GodEyeObserver observer) {
        IGodEyeService service = getService();
        boolean success = (service != null);
        if (success) {
            try {
                service.registerListener(observer);
            } catch (RemoteException e) {
                JosLog.e(TAG, "subscribe observer fail = " + e);
                e.printStackTrace();
                success = false;
            }
        } else {
            JosLog.w(GOD_EYE_TAG, TAG, "service disconnected");
        }

        return success;
    }

    public boolean unsubscribeObserver(GodEyeObserver observer) {
        IGodEyeService service = getService();
        boolean success = (service != null);
        if (success) {
            try {
                service.unregisterListener(observer);
            } catch (RemoteException e) {
                JosLog.e(TAG, "unsubscribe observer fail = " + e);
                e.printStackTrace();
                success = false;
            }
        } else {
            JosLog.w(GOD_EYE_TAG, TAG, "service disconnected");
        }

        return success;
    }

    public boolean setFactor(long factors) {
        IGodEyeService service = getService();
        boolean success = (service != null);
        if (success) {
            try {
                service.setFactor(factors);
            } catch (RemoteException | NullPointerException e) {
                JosLog.e(TAG, "set factors = " + e);
                e.printStackTrace();
                success = false;
            }
        }

        return success;
    }

    public boolean updateFactor(long factors) {
        IGodEyeService service = getService();
        boolean success = (service != null);
        if (success) {
            try {
                service.updateFactor(factors);
            } catch (RemoteException | NullPointerException e) {
                JosLog.e(TAG, "update factors = " + e);
                e.printStackTrace();
                success = false;
            }
        }

        return success;
    }

    public boolean removeFactor(long factors) {
        IGodEyeService service = getService();
        boolean success = (service != null);
        if (success) {
            try {
                service.removeFactor(factors);
            } catch (RemoteException | NullPointerException e) {
                JosLog.e(TAG, "set factors = " + e);
                e.printStackTrace();
                success = false;
            }
        }

        return success;
    }

}
