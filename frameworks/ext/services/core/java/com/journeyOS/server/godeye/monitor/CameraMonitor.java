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

import android.hardware.ICameraService;
import android.hardware.ICameraServiceListener;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;

import com.journeyOS.server.godeye.GodEyeManager;
import com.journeyOS.server.godeye.Scene;

import system.ext.utils.JosLog;

public class CameraMonitor extends BaseMonitor {
    private static final String TAG = CameraMonitor.class.getSimpleName();
    private static final boolean DEBUG = false;
    private static volatile CameraMonitor sInstance = null;

    private ICameraService mCameraService;
    private ICameraServiceListener mListener;

    public static CameraMonitor getInstance() {
        if (sInstance == null) {
            synchronized (CameraMonitor.class) {
                if (sInstance == null) {
                    sInstance = new CameraMonitor();
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
        if (mCameraService == null) {
            IBinder cameraServiceBinder = ServiceManager.getService("media.camera");
            mCameraService = ICameraService.Stub.asInterface(cameraServiceBinder);
        }

        if (mListener == null) {
            mListener = new CameraServiceListener();
        }
        try {
            mCameraService.addListener(mListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        try {
            mCameraService.removeListener(mListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private class CameraServiceListener extends ICameraServiceListener.Stub {
        @Override
        public void onStatusChanged(int status, String cameraId)
                throws RemoteException {
            if (DEBUG) {
                JosLog.v(GodEyeManager.GOD_EYE_TAG, TAG, String.format("Camera %s has status changed to 0x%x", cameraId, status));
            }
        }

        @Override
        public void onTorchStatusChanged(int status, String cameraId)
                throws RemoteException {
            if (DEBUG) {
                JosLog.v(GodEyeManager.GOD_EYE_TAG, TAG, String.format("Camera %s has torch status changed to 0x%x", cameraId, status));
            }
        }

        @Override
        public void onTorchStrengthLevelChanged(String cameraId, int torchStrength) {
            if (DEBUG) {
                JosLog.v(GodEyeManager.GOD_EYE_TAG, TAG, String.format("Camera " + cameraId + " torch strength level changed to " + torchStrength));
            }
        }

        @Override
        public void onPhysicalCameraStatusChanged(int status, String cameraId,
                                                  String physicalCameraId) throws RemoteException {
            if (DEBUG) {
                JosLog.v(GodEyeManager.GOD_EYE_TAG, TAG, String.format("Camera %s : %s has status changed to 0x%x", cameraId, physicalCameraId, status));
            }
        }

        @Override
        public void onCameraAccessPrioritiesChanged() {
            if (DEBUG) {
                JosLog.v(GodEyeManager.GOD_EYE_TAG, TAG, "Camera access permission change");
            }
        }

        @Override
        public void onCameraOpened(String cameraId, String clientPackageName) {
            JosLog.v(GodEyeManager.GOD_EYE_TAG, TAG, String.format("Camera %s is opened by client package %s", cameraId, clientPackageName));
            Scene.Builder builder = new Scene.Builder();
            builder.copy(getPreviewScene());
            builder.setFactorId(GodEyeManager.SCENE_FACTOR_CAMERA);
            builder.setStatus(Scene.State.ON);
            notifyResult(builder.build());
        }

        @Override
        public void onCameraClosed(String cameraId) {
            JosLog.v(GodEyeManager.GOD_EYE_TAG, TAG, String.format("Camera %s is closed", cameraId));
            Scene.Builder builder = new Scene.Builder();
            builder.copy(getPreviewScene());
            builder.setFactorId(GodEyeManager.SCENE_FACTOR_CAMERA);
            builder.setStatus(Scene.State.OFF);
            notifyResult(builder.build());
        }
    }
}
