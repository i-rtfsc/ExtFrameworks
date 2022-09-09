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

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Singleton;

public class VrrThread {
    private static final String TAG = VrrThread.class.getSimpleName();
    private static final String THREAD_NAME = "vrr_thread";

    private static final Singleton<VrrThread> gDefault = new Singleton<VrrThread>() {
        @Override
        protected VrrThread create() {
            return new VrrThread();
        }
    };

    private final HandlerThread mHandlerThread;
    private final Handler mHandler;

    public VrrThread() {
        mHandlerThread = new HandlerThread(THREAD_NAME);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
    }

    public static VrrThread getDefault() {
        return gDefault.get();
    }

    public Handler getHandler() {
        return mHandler;
    }

    public Looper getLooper() {
        return mHandlerThread.getLooper();
    }
}