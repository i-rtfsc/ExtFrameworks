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

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.app.IProcessObserver;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioPlaybackConfiguration;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

import com.journeyOS.server.godeye.GodEyeManager;
import com.journeyOS.server.godeye.GodEyeThread;
import com.journeyOS.server.godeye.Scene;

import java.util.HashMap;
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

    private IActivityManager mActivityManager;
    private final HashMap<Integer, PlayConfig> mPlayers = new HashMap<Integer, PlayConfig>();

    private MediaMonitor() {
        mH = new MessageHandler(GodEyeThread.getDefault().getLooper());
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

        try {
            if (mActivityManager == null) {
                mActivityManager = ActivityManager.getService();
            }
            mActivityManager.registerProcessObserver(mProcessObserver);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        mAm.unregisterAudioPlaybackCallback(mAudioManagerPlaybackListener);

        try {
            if (mActivityManager != null) {
                mActivityManager.unregisterProcessObserver(mProcessObserver);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onChanged(Scene scene) {
        JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, "scene app = [" + scene.getApp() + "]");
        if ((scene.getFactorId() & GodEyeManager.SCENE_FACTOR_APP) != 0) {
            switch (scene.getApp()) {
                case Scene.App.VIDEO:
                case Scene.App.MUSIC: {
                    handlerActivePlayer();
                    break;
                }
                default:
                    break;
            }
        }
    }

    private void handlerActivePlayer() {
        if (mAm != null) {
            List<AudioPlaybackConfiguration> configs = mAm.getActivePlaybackConfigurations();
            if (mAudioManagerPlaybackListener != null) {
                mAudioManagerPlaybackListener.handlerPlaybackConfigChanged(configs, true);
            }
        }
    }

    private void handlerActive(PlayConfig playConfig, boolean force) {
        PlayConfig pevNotifyPlayConfig = mPlayers.get(playConfig.clientPid);
        if (!force && playConfig.equals(pevNotifyPlayConfig)) {
            if (DEBUG) {
                JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, "has been notify, don't need notify again");
            }
            return;
        }

        JosLog.d(GodEyeManager.GOD_EYE_TAG, "handler active, config = [" + playConfig + "], force = [" + force + "]");
        mPlayers.put(playConfig.clientPid, playConfig);

        Scene.Builder builder = new Scene.Builder();
        builder.copy(getPreviewScene());
        builder.setStatus(Scene.State.ON);
        builder.setValue(playConfig.packageName);

//        if (!playConfig.packageName.equals(scene.getPackageName())) {
//            JosLog.w(GodEyeManager.GOD_EYE_TAG, TAG, "player on background, don't need notify...");
//            return;
//        }

        int app = convertApp(playConfig.packageName);
        if (Scene.App.VIDEO == app) {
            builder.setFactorId(GodEyeManager.SCENE_FACTOR_VIDEO);
        } else if (Scene.App.MUSIC == app) {
            builder.setFactorId(GodEyeManager.SCENE_FACTOR_AUDIO);
        } else {
            JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, "unknown app...");
            builder.setFactorId(GodEyeManager.SCENE_FACTOR_AUDIO);
        }

        notifyResult(builder.build());
    }

    private void handlerInactive(PlayConfig playConfig, boolean force) {
        PlayConfig pevNotifyPlayConfig = mPlayers.get(playConfig.clientPid);
        if (!force && playConfig.equals(pevNotifyPlayConfig)) {
            if (DEBUG) {
                JosLog.w(GodEyeManager.GOD_EYE_TAG, TAG, "has been notify, don't need notify again");
            }
            return;
        }

        JosLog.d(GodEyeManager.GOD_EYE_TAG, "handler inactive, config = [" + playConfig + "], force = [" + force + "]");
        mPlayers.put(playConfig.clientPid, playConfig);

        Scene.Builder builder = new Scene.Builder();
        builder.copy(getPreviewScene());
        builder.setStatus(Scene.State.ON);
        builder.setValue(playConfig.packageName);

//        if (!playConfig.packageName.equals(scene.getPackageName())) {
//            JosLog.w(GodEyeManager.GOD_EYE_TAG, TAG, "player on background, don't need notify.");
//            return;
//        }

        int app = convertApp(playConfig.packageName);
        if (Scene.App.VIDEO == app) {
            builder.setFactorId(GodEyeManager.SCENE_FACTOR_VIDEO);
        } else if (Scene.App.MUSIC == app) {
            builder.setFactorId(GodEyeManager.SCENE_FACTOR_AUDIO);
        } else {
            JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, "unknown app...");
            builder.setFactorId(GodEyeManager.SCENE_FACTOR_AUDIO);
        }

        notifyResult(builder.build());
    }

    private final IProcessObserver.Stub mProcessObserver = new IProcessObserver.Stub() {
        @Override
        public void onForegroundActivitiesChanged(int pid, int uid, boolean foregroundActivities)
                throws RemoteException {
            if (DEBUG) {
                JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, "on foreground activities changed, pid = [" + pid + "], package name = [" + getProcessName(pid) + "]");
            }
        }

        @Override
        public void onForegroundServicesChanged(int pid, int uid, int serviceTypes)
                throws RemoteException {
            if (DEBUG) {
                JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, "on foreground services changed, pid = [" + pid + "], package name = [" + getProcessName(pid) + "]");
            }
        }

        @Override
        public void onProcessDied(int pid, int uid)
                throws RemoteException {
            JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, "on process died, pid = [" + pid + "], package name = [" + getProcessName(pid) + "]");
            if (mPlayers.containsKey(pid)) {
                mPlayers.remove(pid);
            }
        }
    };

    private class MessageHandler extends Handler {
        public static final long DELAYED = 1000;

        public static final int MSG_ACTIVE = 1;
        public static final int MSG_INACTIVE = 2;

        public MessageHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ACTIVE:
                    handlerActive((PlayConfig) msg.obj, msg.arg1 == 1);
                    break;
                case MSG_INACTIVE:
                    handlerInactive((PlayConfig) msg.obj, msg.arg1 == 1);
                    break;
                default:
                    break;
            }
        }
    }

    private class PlayConfig {
        int playerState;
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
            handlerPlaybackConfigChanged(configs, false);
        }

        public void handlerPlaybackConfigChanged(List<AudioPlaybackConfiguration> configs, boolean force) {
            JosLog.i(GodEyeManager.GOD_EYE_TAG, TAG, "force = [" + force + "]");
            synchronized (mLock) {
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
                            message.arg1 = force ? 1 : 0;
                            mH.sendMessageDelayed(message, MessageHandler.DELAYED);
                        } else {
                            if (mH.hasMessages(MessageHandler.MSG_INACTIVE)) {
                                mH.removeMessages(MessageHandler.MSG_INACTIVE);
                            }
                            Message message = Message.obtain();
                            message.what = MessageHandler.MSG_INACTIVE;
                            message.obj = playConfig;
                            message.arg1 = force ? 1 : 0;
                            mH.sendMessageDelayed(message, MessageHandler.DELAYED);
                        }
                    }
                }
            }
        }
    }
}
