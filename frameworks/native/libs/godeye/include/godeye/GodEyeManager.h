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

#ifndef ANDROID_GOD_EYE_MANAGER_H
#define ANDROID_GOD_EYE_MANAGER_H

#include <binder/IInterface.h>
#include <utils/RefBase.h>
#include <binder/Parcel.h>

#include "IGodEyeService.h"

namespace android {

    class GodEyeManager {
    private:
        sp <IGodEyeService> mServicePolicy;
        sp <IBinder::DeathRecipient> mDeathListener;

        sp <IGodEyeService> getServicePolicy();

        static GodEyeManager *sInstance;

        class DeathNotifier : public IBinder::DeathRecipient {
        private:
            sp <IGodEyeService> mService;
        public:
            DeathNotifier(sp <IGodEyeService> &service);

            virtual ~DeathNotifier();

            virtual void binderDied(const wp <IBinder> &who);
        };

    public:
        GodEyeManager();

        virtual ~GodEyeManager();

        static GodEyeManager *getInstance();

        void release();

        void registerListener(const sp <IGodEyeListener> &listener);

        void unregisterListener(const sp <IGodEyeListener> &listener);

        void setFactor(const int64_t factors);

        void updateFactor(const int64_t factors);

        void removeFactor(const int64_t factors);

        void onActivityResumed(const String16 &packageName);

        void onCameraConnected(const int cameraId, const String16 &clientPackageName);

        void onCameraDisconnected(const int cameraId, const String16 &clientPackageName);

        void onVideoStarted(const int callingPid);

        void onVideoStopped(const int callingPid);

        void onAudioStarted(const int callingPid, const int stream, const int64_t track);

        void onAudioStopped(const int callingPid, const int stream, const int64_t track);
    };

};

#endif //ANDROID_GOD_EYE_MANAGER_H
