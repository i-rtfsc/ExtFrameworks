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

import android.content.Context;
import android.os.ServiceManager;
import android.os.RemoteException;

import java.io.FileDescriptor;
import java.io.PrintWriter;

import system.ext.utils.JosLog;

/**
 * VariableRefreshRateService
 */
public class VariableRefreshRateService extends IVariableRefreshRate.Stub {
    private static final String TAG = VariableRefreshRateService.class.getSimpleName();

    private final Context mContext;

    public VariableRefreshRateService(Context context) {
        this.mContext = context;
    }

    public void systemReady() {
        JosLog.i(VRRManager.VRR_TAG, TAG, "systemReady");
        ServiceManager.addService(VRRManager.VRR_SERVICE, asBinder());
    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        new VrrDumpCommand().dump(fd, pw, args);
    }

    @Override
    public void setRefreshRatePolicy(int displayId, float rate, int policy, boolean statusOn) throws RemoteException {
        JosLog.d(VRRManager.VRR_TAG, TAG, "setRefreshRatePolicy() called with: displayId = [" + displayId + "], rate = [" + rate + "], policy = [" + policy + "], statusOn = [" + statusOn + "]");
    }

}
