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

#include "IGodEyeService.h"

namespace android {

    class BpGodEyeService : public BpInterface<IGodEyeService> {
    public:
        BpGodEyeService(const sp <IBinder> &binder)
                : BpInterface<IGodEyeService>(binder) {
        }

        void registerListener(const sp<IGodEyeListener>& listener) {
            Parcel data;
            data.writeInterfaceToken(BpGodEyeService::getInterfaceDescriptor());
            data.writeStrongBinder(IInterface::asBinder(listener));
            remote()->transact(TRANSACTION_REGISTER_LISTENER, data, NULL);
        }

        void unregisterListener(const sp<IGodEyeListener>& listener) {
            Parcel data;
            data.writeInterfaceToken(BpGodEyeService::getInterfaceDescriptor());
            data.writeStrongBinder(IInterface::asBinder(listener));
            remote()->transact(TRANSACTION_UNREGISTER_LISTENER, data, NULL);
        }

        void setFactor(const int64_t factors) {
            Parcel data;
            data.writeInterfaceToken(BpGodEyeService::getInterfaceDescriptor());
            data.writeInt64(factors);
            remote()->transact(TRANSACTION_SET_FACTOR, data, NULL);
        }

        void updateFactor(const int64_t factors) {
            Parcel data;
            data.writeInterfaceToken(BpGodEyeService::getInterfaceDescriptor());
            data.writeInt64(factors);
            remote()->transact(TRANSACTION_UPDATE_FACTOR, data, NULL);
        }

        void removeFactor(const int64_t factors) {
            Parcel data;
            data.writeInterfaceToken(BpGodEyeService::getInterfaceDescriptor());
            data.writeInt64(factors);
            remote()->transact(TRANSACTION_REMOVE_FACTOR, data, NULL);
        }

        void onActivityResumed(const String16& packageName) {
            Parcel data;
            data.writeInterfaceToken(BpGodEyeService::getInterfaceDescriptor());
            data.writeString16(packageName);
            remote()->transact(TRANSACTION_ON_ACTIVITY_RESUMED, data, NULL);
        }

        void onCameraConnected(const int cameraId, const String16& clientPackageName) {
            Parcel data, reply;
            data.writeInterfaceToken(BpGodEyeService::getInterfaceDescriptor());
            data.writeInt32(cameraId);
            data.writeString16(clientPackageName);
            remote()->transact(TRANSACTION_ON_CAMERA_CONNECTED, data, &reply, IBinder::FLAG_ONEWAY);
        }

        void onCameraDisconnected(const int cameraId, const String16& clientPackageName) {
            Parcel data, reply;
            data.writeInterfaceToken(BpGodEyeService::getInterfaceDescriptor());
            data.writeInt32(cameraId);
            data.writeString16(clientPackageName);
            remote()->transact(TRANSACTION_ON_CAMERA_DISCONNECTED, data, &reply, IBinder::FLAG_ONEWAY);
        }

        void onVideoStarted(const int callingPid) {
            Parcel data, reply;
            data.writeInterfaceToken(BpGodEyeService::getInterfaceDescriptor());
            data.writeInt32(callingPid);
            remote()->transact(TRANSACTION_ON_VIDEO_STARTED, data, &reply, IBinder::FLAG_ONEWAY);
        }

        void onVideoStopped(const int callingPid) {
            Parcel data, reply;
            data.writeInterfaceToken(BpGodEyeService::getInterfaceDescriptor());
            data.writeInt32(callingPid);
            remote()->transact(TRANSACTION_ON_VIDEO_STOPPED, data, &reply, IBinder::FLAG_ONEWAY);
        }

        void onAudioStarted(const int callingPid, const int stream, const int64_t track) {
            Parcel data, reply;
            data.writeInterfaceToken(BpGodEyeService::getInterfaceDescriptor());
            data.writeInt32(callingPid);
            data.writeInt32(stream);
            data.writeInt64(track);
            remote()->transact(TRANSACTION_ON_AUDIO_STARTED, data, &reply, IBinder::FLAG_ONEWAY);
        }

        void onAudioStopped(const int callingPid, const int stream, const int64_t track) {
            Parcel data, reply;
            data.writeInterfaceToken(BpGodEyeService::getInterfaceDescriptor());
            data.writeInt32(callingPid);
            data.writeInt32(stream);
            data.writeInt64(track);
            remote()->transact(TRANSACTION_ON_AUDIO_STARTED, data, &reply, IBinder::FLAG_ONEWAY);
        }
    };

    IMPLEMENT_META_INTERFACE(GodEyeService, "com.journeyOS.server.godeye.IGodEyeService");

    status_t BnGodEyeService::onTransact(uint32_t code, const Parcel &data, Parcel *reply, uint32_t flags) {
        switch (code) {
            default:
                return BBinder::onTransact(code, data, reply, flags);
        }
    }
};
