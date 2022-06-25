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

package com.android.server.display;

import android.content.Context;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.DisplayInfo;

import com.journeyOS.server.vrr.VRRManager;
import com.journeyOS.server.vrr.VariableRefreshRateService;

import system.ext.utils.JosLog;

public class HookDisplayManagerServiceImpl implements HookDisplayManagerService {
    private static final String TAG = HookDisplayManagerServiceImpl.class.getSimpleName();

    private DisplayManagerService mDms;
    private VariableRefreshRateService mVrr;

    public HookDisplayManagerServiceImpl() {
    }

    @Override
    public void init(DisplayManagerService displayManagerService) {
        mDms = displayManagerService;
    }

    @Override
    public void systemReady(Context context, LogicalDisplayMapper logicalDisplayMapper) {
        JosLog.i(TAG, "systemReady");
        if (mVrr == null) {
            LogicalDisplay display = logicalDisplayMapper.getDisplayLocked(0);
            JosLog.i(VRRManager.VRR_TAG, TAG, "systemReady display = [" + display + "]");
            DisplayInfo displayInfo = null;
            if (display != null) {
                displayInfo = display.getDisplayInfoLocked();
            }
            JosLog.i(VRRManager.VRR_TAG, TAG, "systemReady displayInfo = [" + displayInfo + "]");
            this.mVrr = new VariableRefreshRateService(context, displayInfo);
            try {
                mVrr.systemReady();
            } catch (Exception e) {
                JosLog.e(TAG, "vrr publish fail = " + e.toString());
                e.printStackTrace();
                this.mVrr = null;
            }
        }
    }

    @Override
    public void onStart() {
        JosLog.i(TAG, "onStart");
    }

    @Override
    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        //TODO
        return false;
    }

}
