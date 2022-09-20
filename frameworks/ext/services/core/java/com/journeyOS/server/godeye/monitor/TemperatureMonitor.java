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

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.journeyOS.server.godeye.GodEyeManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import system.ext.utils.JosLog;

public class TemperatureMonitor extends BaseMonitor {
    private static final String TAG = TemperatureMonitor.class.getSimpleName();
    private static final boolean DEBUG = false;

    private static final String TEMPERATURE_FILE = "/sys/devices/virtual/thermal/thermal_zone65/temp";

    private static volatile TemperatureMonitor sInstance = null;

    private HandlerThread mThread = new HandlerThread("tmperature-thread");
    private MessageHandler mH;

    private TemperatureMonitor() {
        mThread.start();
        mH = new MessageHandler(mThread.getLooper());
    }

    public static TemperatureMonitor getInstance() {
        if (sInstance == null) {
            synchronized (TemperatureMonitor.class) {
                if (sInstance == null) {
                    sInstance = new TemperatureMonitor();
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
        mH.sendEmptyMessageDelayed(MessageHandler.MSG_READ_TMP, MessageHandler.DELAYED);
    }

    @Override
    protected void onStop() {
        mH.removeMessages(MessageHandler.MSG_READ_TMP);
    }

    private void handlerTemperature(long temperature) {
        JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, "handler temperature = [" + temperature + "]");
    }

    private long readTemperature() {
        BufferedReader reader = null;
        String line = null;
        try {
            reader = new BufferedReader(new FileReader(TEMPERATURE_FILE));
            line = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (line == null) {
            return 0;
        } else {
            try {
                return Long.valueOf(line);
            } catch (Throwable e) {
            }
        }
        return 0;
    }

    private class MessageHandler extends Handler {
        public static final long DELAYED = 3000;

        public static final int MSG_READ_TMP = 1;

        public MessageHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_READ_TMP:
                    long temperature = readTemperature();
                    handlerTemperature(temperature);
                    mH.sendEmptyMessageDelayed(MessageHandler.MSG_READ_TMP, MessageHandler.DELAYED);
                    break;
                default:
                    break;
            }
        }
    }

}
