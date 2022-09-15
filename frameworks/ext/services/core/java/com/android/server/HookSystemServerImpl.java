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

package com.android.server;

import android.content.Context;

import com.journeyOS.server.godeye.GodEyeManager;
import com.journeyOS.server.godeye.GodEyeService;

import system.ext.utils.JosLog;

public class HookSystemServerImpl implements HookSystemServer {
    private static final String TAG = HookSystemServerImpl.class.getSimpleName();

    private Context mContext;

    private GodEyeService mGodEyeService;

    public HookSystemServerImpl() {
    }

    @Override
    public void initSystemServer(Context systemContext) {
        JosLog.i(TAG, "init system server");
        mContext = systemContext;
    }

    @Override
    public void startBootstrapServices() {
        JosLog.i(TAG, "start bootstrap services");
    }

    @Override
    public void startCoreServices() {
        JosLog.i(TAG, "start core services");
        if (mGodEyeService == null) {
            mGodEyeService = new GodEyeService(mContext);
            try {
                mGodEyeService.systemReady();
            } catch (Exception e) {
                JosLog.e(GodEyeManager.GOD_EYE_TAG, TAG, "publish fail = " + e);
                e.printStackTrace();
                mGodEyeService = null;
            }
        }
    }

    @Override
    public void startOtherServices() {
        JosLog.i(TAG, "start other services");
    }

}
