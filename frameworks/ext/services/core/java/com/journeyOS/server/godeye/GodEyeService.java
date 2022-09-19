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
import android.os.RemoteException;
import android.os.ServiceManager;

import com.journeyOS.server.godeye.clients.ClientSession;
import com.journeyOS.server.godeye.monitor.MonitorManager;
import com.journeyOS.server.godeye.monitor.PackageNameMonitor;

import java.io.FileDescriptor;
import java.io.PrintWriter;

import system.ext.utils.JosLog;

/**
 * GodEyeService
 */
public class GodEyeService extends IGodEyeService.Stub implements MonitorManager.OnSceneListener {
    private static final String TAG = GodEyeService.class.getSimpleName();

    private final Context mContext;

    public GodEyeService(Context context) {
        this.mContext = context;
    }

    public void systemReady() {
        JosLog.i(GodEyeManager.GOD_EYE_TAG, TAG, "systemReady");
        ServiceManager.addService(GodEyeManager.GOD_EYE_SERVICE, asBinder());
        MonitorManager.getInstance().init(mContext);
        MonitorManager.getInstance().registerListener(this);
    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        new GodEyeDumpCommand().dump(fd, pw, args);
    }

    @Override
    public void registerListener(IGodEyeListener listener) throws RemoteException {
        ClientSession.getInstance().insertToCategory(listener);
    }

    @Override
    public void unregisterListener(IGodEyeListener listener) throws RemoteException {
        ClientSession.getInstance().removeFromCategory(listener);
    }

    @Override
    public void setFactor(long factors) throws RemoteException {
        MonitorManager.getInstance().start(factors);
        ClientSession.getInstance().setFactorToCategory(factors);
    }

    @Override
    public void updateFactor(long factors) throws RemoteException {
        MonitorManager.getInstance().start(factors);
        ClientSession.getInstance().updateFactorToCategory(factors);
    }

    @Override
    public void removeFactor(long factors) throws RemoteException {
        if (ClientSession.getInstance().checkFactorFromCategory(factors)) {
            MonitorManager.getInstance().stop(factors);
        }
        ClientSession.getInstance().removeFactorFromCategory(factors);
    }

    @Override
    public void activityResumed(String packageName) throws RemoteException {
        JosLog.v(GodEyeManager.GOD_EYE_TAG, TAG, "activity resumed, packageName = [" + packageName + "]");
    }

    @Override
    public void onCameraConnected(int cameraId, String clientPackageName) throws RemoteException {
        JosLog.v(GodEyeManager.GOD_EYE_TAG, TAG, "on camera connected, cameraId = [" + cameraId + "], clientPackageName = [" + clientPackageName + "]");
    }

    @Override
    public void onCameraDisconnected(int cameraId, String clientPackageName) throws RemoteException {
        JosLog.v(GodEyeManager.GOD_EYE_TAG, TAG, "on camera disconnected, cameraId = [" + cameraId + "], clientPackageName = [" + clientPackageName + "]");
    }

    @Override
    public void onVideoStarted(int callingPid) throws RemoteException {
        JosLog.v(GodEyeManager.GOD_EYE_TAG, TAG, "on video started, callingPid = [" + callingPid + "]");
    }

    @Override
    public void onVideoStopped(int callingPid) throws RemoteException {
        JosLog.v(GodEyeManager.GOD_EYE_TAG, TAG, "on video stopped, callingPid = [" + callingPid + "]");
    }

    @Override
    public void onAudioStarted(int callingPid, int stream, long track) throws RemoteException {
        JosLog.v(GodEyeManager.GOD_EYE_TAG, TAG, "on audio started, callingPid = [" + callingPid + "], stream = [" + stream + "], track = [" + track + "]");
    }

    @Override
    public void onAudioStopped(int callingPid, int stream, long track) throws RemoteException {
        JosLog.v(GodEyeManager.GOD_EYE_TAG, TAG, "on audio stopped, callingPid = [" + callingPid + "], stream = [" + stream + "], track = [" + track + "]");
    }

    @Override
    public void onChanged(Scene scene) {
        JosLog.v(GodEyeManager.GOD_EYE_TAG, TAG, "on changed, scene = [" + scene.toString() + "]");
        ClientSession.getInstance().dispatchFactorEvent(scene);
    }
}
