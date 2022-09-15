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

package com.journeyOS.server.godeye.clients;

import android.os.Handler;
import android.os.RemoteException;

import com.journeyOS.server.godeye.GodEyeManager;
import com.journeyOS.server.godeye.IGodEyeListener;
import com.journeyOS.server.godeye.Scene;

import system.ext.utils.JosLog;

public class Clients extends ClientsImpl {
    private static final String TAG = Clients.class.getSimpleName();

    public Clients(Handler handler) {
        super(handler);
    }

    public synchronized void dispatchFactorEvent(final Scene result) {
        Operation operation = new Operation() {
            @Override
            public void execute(IGodEyeListener listener, int pid) throws RemoteException {
                try {
                    JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, "dispatch to pid = [" + pid + "], result = [" + result + "]");
                    listener.onSceneChanged(result);
                } catch (Throwable e) {
                    JosLog.e(GodEyeManager.GOD_EYE_TAG, TAG, "Exception in dispatch factor event with appInfo!!!");
                }
            }
        };
        foreach(operation, result.getFactorId());
    }

    private interface Operation extends ListenerOperation<IGodEyeListener> {
    }
}