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

import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.journeyOS.server.godeye.IGodEyeListener;
import com.journeyOS.server.godeye.Scene;

public final class ClientSession {
    private static volatile ClientSession sInstance = null;
    private final HandlerThread mHandlerThread;
    private final Handler mHandler;
    private final Clients mClients;

    private ClientSession() {
        mHandlerThread = new HandlerThread("ClientSession");
        mHandlerThread.start();
        mHandler = new H(mHandlerThread.getLooper());
        mClients = new Clients(mHandler);
    }

    public static ClientSession getInstance() {
        if (sInstance == null) {
            synchronized (ClientSession.class) {
                if (sInstance == null) {
                    sInstance = new ClientSession();
                }
            }
        }
        return sInstance;
    }

    public boolean insertToCategory(IGodEyeListener listener) {
        int callingPid = Binder.getCallingPid();
        return mClients.addRemoteListener(callingPid, listener);
    }

    public boolean removeFromCategory(IGodEyeListener listener) {
        return mClients.removeRemoteListener(listener);
    }

    public boolean setFactorToCategory(long factors) {
        int callingPid = Binder.getCallingPid();
        return mClients.setRemoteFactor(callingPid, factors);
    }

    public boolean updateFactorToCategory(long factors) {
        int callingPid = Binder.getCallingPid();
        return mClients.updateRemoteFactor(callingPid, factors);
    }

    public boolean removeFactorFromCategory(long factors) {
        int callingPid = Binder.getCallingPid();
        return mClients.removeRemoteFactor(callingPid, factors);
    }

    public boolean checkFactorFromCategory(long factors) {
        return mClients.checkFactor(factors);
    }

    public synchronized void dispatchFactorEvent(final Scene result) {
        Message message = Message.obtain();
        message.what = H.MSG_OBJ;
        message.obj = result;
        mHandler.sendMessageDelayed(message, H.DELAYED_MILLIS);
    }

    private class H extends Handler {
        public static final long DELAYED_MILLIS = 1;
        public static final int MSG_OBJ = 1;

        public H(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OBJ:
                    mClients.dispatchFactorEvent((Scene) msg.obj);
                    break;
                default:
                    break;
            }
        }
    }
}