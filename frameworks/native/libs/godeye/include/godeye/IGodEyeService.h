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

#ifndef ANDROID_GOD_EYE_SERVICE_H
#define ANDROID_GOD_EYE_SERVICE_H

//frameworks/native/libs/binder/include/binder/IInterface.h
//使用android11之后宏IMPLEMENT_META_INTERFACE，必须在kManualInterfaces定义aidl
//可以通过定义DO_NOT_CHECK_MANUAL_BINDER_INTERFACES规避掉。
#define DO_NOT_CHECK_MANUAL_BINDER_INTERFACES 1

#include <binder/IInterface.h>
#include <utils/RefBase.h>
#include <binder/Parcel.h>

#include "IGodEyeListener.h"

namespace android {

    class IGodEyeService : public IInterface {
    public:
        enum {
            TRANSACTION_REGISTER_LISTENER = IBinder::FIRST_CALL_TRANSACTION,
            TRANSACTION_UNREGISTER_LISTENER,
            TRANSACTION_SET_FACTOR,
            TRANSACTION_UPDATE_FACTOR,
            TRANSACTION_REMOVE_FACTOR,
            TRANSACTION_ON_ACTIVITY_RESUMED,
            TRANSACTION_ON_CAMERA_CONNECTED,
            TRANSACTION_ON_CAMERA_DISCONNECTED,
            TRANSACTION_ON_VIDEO_STARTED,
            TRANSACTION_ON_VIDEO_STOPPED,
            TRANSACTION_ON_AUDIO_STARTED,
            TRANSACTION_ON_AUDIO_STOPPED,
        };

        virtual void registerListener(const sp <IGodEyeListener> &listener) = 0;

        virtual void unregisterListener(const sp <IGodEyeListener> &listener) = 0;

        virtual void setFactor(const int64_t factors) = 0;

        virtual void updateFactor(const int64_t factors) = 0;

        virtual void removeFactor(const int64_t factors) = 0;

        virtual void onActivityResumed(const String16 &packageName) = 0;

        virtual void onCameraConnected(const int cameraId, const String16 &clientPackageName) = 0;

        virtual void onCameraDisconnected(const int cameraId, const String16 &clientPackageName) = 0;

        virtual void onVideoStarted(const int callingPid) = 0;

        virtual void onVideoStopped(const int callingPid) = 0;

        virtual void onAudioStarted(const int callingPid, const int stream, const int64_t track) = 0;

        virtual void onAudioStopped(const int callingPid, const int stream, const int64_t track) = 0;

        DECLARE_META_INTERFACE(GodEyeService);
    };

    class BnGodEyeService : public BnInterface<IGodEyeService> {
    public:
        virtual status_t onTransact(uint32_t code, const Parcel &data, Parcel *reply, uint32_t flags = 0);
    };

};

#endif //ANDROID_GOD_EYE_SERVICE_H
