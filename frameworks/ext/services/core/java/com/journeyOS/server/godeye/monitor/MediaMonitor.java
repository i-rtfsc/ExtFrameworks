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

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioPlaybackConfiguration;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.ArrayMap;

import androidx.annotation.NonNull;

import com.android.server.HookSystemConfig;
import com.journeyOS.server.godeye.GodEyeManager;

import java.util.List;

import system.ext.utils.JosLog;

public class MediaMonitor extends BaseMonitor {
    private static final String TAG = MediaMonitor.class.getSimpleName();
    private static final boolean DEBUG = false;
    private static volatile MediaMonitor sInstance = null;

    private final Object mLock = new Object();
    private AudioManager mAm = null;
    private AudioManagerPlaybackListener mAudioManagerPlaybackListener = null;
    private MessageHandler mH;

    private PlayConfig mPrevNotifyPlayConfig = null;

    private MediaMonitor() {
        mH = new MessageHandler(Looper.myLooper());
    }

    public static MediaMonitor getInstance() {
        if (sInstance == null) {
            synchronized (MediaMonitor.class) {
                if (sInstance == null) {
                    sInstance = new MediaMonitor();
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
        if (mAm == null) {
            mAm = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        }
        if (mAudioManagerPlaybackListener == null) {
            mAudioManagerPlaybackListener = new AudioManagerPlaybackListener();
        }
        mAm.registerAudioPlaybackCallback(mAudioManagerPlaybackListener, null);
    }

    @Override
    protected void onStop() {
        mAm.unregisterAudioPlaybackCallback(mAudioManagerPlaybackListener);
    }

    private void handlerActive(PlayConfig playConfig) {
        JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, "handler active, config = [" + playConfig + "]");
        if (playConfig.equals(mPrevNotifyPlayConfig)) {
            JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, "has been notify, don't need notify again");
            return;
        }
        //TODO
        mPrevNotifyPlayConfig = playConfig;
    }

    private void handlerInactive(PlayConfig playConfig) {
        JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, "handler inactive, config = [" + playConfig + "]");
        if (playConfig.equals(mPrevNotifyPlayConfig)) {
            JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, "has been notify, don't need notify again");
            return;
        }
        //TODO
        mPrevNotifyPlayConfig = playConfig;
    }

    private class MessageHandler extends Handler {
        public static final long DELAYED = 1000;

        public static final int MSG_ACTIVE = 1;
        public static final int MSG_INACTIVE = 2;

        public MessageHandler(@NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ACTIVE:
                    handlerActive((PlayConfig) msg.obj);
                    break;
                case MSG_INACTIVE:
                    handlerInactive((PlayConfig) msg.obj);
                    break;
                default:
                    break;
            }
        }
    }

    private class PlayConfig {
        int playerState ;
        int clientPid;
        String packageName;
        boolean isMusicActive;
        int playerInterfaceId;

        public PlayConfig(int playerState, int clientPid, String packageName, boolean isMusicActive, int playerInterfaceId) {
            this.playerState = playerState;
            this.clientPid = clientPid;
            this.packageName = packageName;
            this.isMusicActive = isMusicActive;
            this.playerInterfaceId = playerInterfaceId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || !(o instanceof PlayConfig)) {
                return false;
            }

            PlayConfig that = (PlayConfig) o;

            return ((playerState == that.playerState)
                    && (playerInterfaceId == that.playerInterfaceId));
        }

        @Override
        public String toString() {
            return "Config{" +
                    "playerState=" + playerState +
                    ", clientPid=" + clientPid +
                    ", packageName='" + packageName + '\'' +
                    ", isMusicActive=" + isMusicActive +
                    ", playerInterfaceId=" + playerInterfaceId +
                    '}';
        }
    }

    private class AudioManagerPlaybackListener extends AudioManager.AudioPlaybackCallback {
        @Override
        public void onPlaybackConfigChanged(List<AudioPlaybackConfiguration> configs) {
            synchronized (mLock) {
                ArrayMap<Integer, AudioPlaybackConfiguration> activeAudioPlaybackConfigs = new ArrayMap<>();
                for (AudioPlaybackConfiguration config : configs) {
                    int playerState = config.getPlayerState();
                    if (AudioPlaybackConfiguration.PLAYER_STATE_STARTED == playerState
                            || AudioPlaybackConfiguration.PLAYER_STATE_PAUSED == playerState) {

                        String playerStateName = AudioPlaybackConfiguration.toLogFriendlyPlayerState(playerState);
                        int clientPid = config.getClientPid();
                        String packageName = getProcessName(clientPid);
                        boolean isMusicActive = mAm.isMusicActive();
                        int playerInterfaceId = config.getPlayerInterfaceId();

                        if (DEBUG) {
                            JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, "player state = [" + playerStateName + "]" +
                                    ", client name = [" + packageName + "]" +
                                    ", player interface id = [" + playerInterfaceId + "]" +
                                    ", is music active = [" + isMusicActive + "]");
                        }

                        PlayConfig playConfig = new PlayConfig(playerState, clientPid, packageName, isMusicActive, playerInterfaceId);
                        if (config.isActive()) {
                            if (mH.hasMessages(MessageHandler.MSG_ACTIVE)) {
                                mH.removeMessages(MessageHandler.MSG_ACTIVE);
                            }
                            Message message = Message.obtain();
                            message.what = MessageHandler.MSG_ACTIVE;
                            message.obj = playConfig;
                            mH.sendMessageDelayed(message, MessageHandler.DELAYED);
                        } else {
                            if (mH.hasMessages(MessageHandler.MSG_INACTIVE)) {
                                mH.removeMessages(MessageHandler.MSG_INACTIVE);
                            }
                            Message message = Message.obtain();
                            message.what = MessageHandler.MSG_INACTIVE;
                            message.obj = playConfig;
                            mH.sendMessageDelayed(message, MessageHandler.DELAYED);
                        }
                    }
                }
            }
        }
    }
}
