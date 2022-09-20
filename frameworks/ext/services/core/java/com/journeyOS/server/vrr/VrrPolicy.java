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
import android.os.Looper;
import android.os.Message;
import android.util.Singleton;

import com.journeyOS.server.godeye.GodEyeManager;
import com.journeyOS.server.godeye.LocalGodEyeService;
import com.journeyOS.server.godeye.Scene;

import system.ext.utils.JosLog;

public class VrrPolicy implements LocalGodEyeService.GodEyeListener {
    private static final String TAG = VrrPolicy.class.getSimpleName();

    private static final Singleton<VrrPolicy> gDefault = new Singleton<VrrPolicy>() {
        @Override
        protected VrrPolicy create() {
            return new VrrPolicy();
        }
    };

    private MessageHandler mH;

    public VrrPolicy() {
        mH = new MessageHandler(VrrThread.getDefault().getLooper());
        mH.sendEmptyMessageDelayed(MessageHandler.MSG_REGISTER, MessageHandler.DELAYED);
    }

    public static VrrPolicy getDefault() {
        return gDefault.get();
    }

    private void registerListener() {
        long factors = GodEyeManager.SCENE_FACTOR_APP
                | GodEyeManager.SCENE_FACTOR_CAMERA
                | GodEyeManager.SCENE_FACTOR_VIDEO
                | GodEyeManager.SCENE_FACTOR_AUDIO
                | GodEyeManager.SCENE_FACTOR_BRIGHTNESS
                | GodEyeManager.SCENE_FACTOR_TEMPERATURE;
        LocalGodEyeService.get().registerListener(factors, this);
    }

    private void unregisterListener() {
        LocalGodEyeService.get().unregisterListener(this);
    }

    @Override
    public void onSceneChanged(Scene scene) {
        long factors = scene.getFactorId();
        if ((factors & GodEyeManager.SCENE_FACTOR_APP) != 0) {
            JosLog.d(VRRManager.VRR_TAG, TAG, "app = [" + scene.getApp() + "]");
        }

        if ((factors & GodEyeManager.SCENE_FACTOR_CAMERA) != 0) {
            JosLog.d(VRRManager.VRR_TAG, TAG, "camera status = [" + scene.getStatus() + "]");
        }

        if ((factors & GodEyeManager.SCENE_FACTOR_VIDEO) != 0) {
            JosLog.d(VRRManager.VRR_TAG, TAG, "video status = [" + scene.getStatus() + "], packageName = [" + scene.getPlayer() + "]");
        }

        if ((factors & GodEyeManager.SCENE_FACTOR_AUDIO) != 0) {
            JosLog.d(VRRManager.VRR_TAG, TAG, "audio status = [" + scene.getStatus() + "], packageName = [" + scene.getPlayer() + "]");
        }

        if ((factors & GodEyeManager.SCENE_FACTOR_BRIGHTNESS) != 0) {
            JosLog.d(VRRManager.VRR_TAG, TAG, "brightness = [" + scene.getBrightness() + "]");
        }
    }

    private class MessageHandler extends Handler {
        public static final long DELAYED = 1000;

        public static final int MSG_REGISTER = 1;
        public static final int MSG_UNREGISTER = 2;

        public MessageHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER:
                    registerListener();
                    break;
                case MSG_UNREGISTER:
                    unregisterListener();
                    break;
                default:
                    break;
            }
        }
    }
}
