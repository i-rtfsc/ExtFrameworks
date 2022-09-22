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

#ifndef ANDROID_GOD_EYE_LISTENER_H
#define ANDROID_GOD_EYE_LISTENER_H

//frameworks/native/libs/binder/include/binder/IInterface.h
//使用android11之后宏IMPLEMENT_META_INTERFACE，必须在kManualInterfaces定义aidl
//可以通过定义DO_NOT_CHECK_MANUAL_BINDER_INTERFACES规避掉。
#define DO_NOT_CHECK_MANUAL_BINDER_INTERFACES 1

#include <binder/IInterface.h>
#include <utils/RefBase.h>
#include <binder/Parcel.h>

namespace android {

    struct Scene {
        int64_t factorId;
        String16 packageName;
        int type;
        int status;

        status_t writeToParcel(Parcel *p) const {
            p->writeInt64(factorId);
            p->writeString16(packageName);
            p->writeInt32(type);
            p->writeInt32(status);
            return OK;
        }

        status_t readFromParcel(const Parcel &p) {
            factorId = p.readInt64();
            packageName = p.readString16();
            type = p.readInt32();
            status = p.readInt32();
            return OK;
        }
    };


    class IGodEyeListener : public IInterface {
    public:
        enum {
            TRANSACTION_ON_SCENE_CHANGED = IBinder::FIRST_CALL_TRANSACTION,
        };

        virtual void onSceneChanged(const Scene scene) = 0;

        DECLARE_META_INTERFACE(GodEyeListener);
    };

    class BnGodEyeListener : public BnInterface<IGodEyeListener> {
    public:
        virtual status_t onTransact(uint32_t code, const Parcel &data, Parcel *reply, uint32_t flags = 0);
    };

};

#endif //ANDROID_GOD_EYE_LISTENER_H
