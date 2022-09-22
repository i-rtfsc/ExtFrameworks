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

#ifndef ANDROID_VRR_SERVICE_H
#define ANDROID_VRR_SERVICE_H

//frameworks/native/libs/binder/include/binder/IInterface.h
//使用android11之后宏IMPLEMENT_META_INTERFACE，必须在kManualInterfaces定义aidl
//可以通过定义DO_NOT_CHECK_MANUAL_BINDER_INTERFACES规避掉。
#define DO_NOT_CHECK_MANUAL_BINDER_INTERFACES 1

#include <binder/IInterface.h>
#include <utils/RefBase.h>
#include <binder/Parcel.h>

namespace android {

    class IVariableRefreshRateService : public IInterface {
    public:
        enum {
            TRANSACTION_SET_REFRESH_RATE_POLICY = IBinder::FIRST_CALL_TRANSACTION,
            TRANSACTION_ON_FPS_CHANGE,
        };

        virtual void setRefreshRatePolicy(int displayId, float rate, int policy, bool statusOn) = 0;

        virtual void onFpsChange(int fps) = 0;

        DECLARE_META_INTERFACE(VariableRefreshRateService);
    };

    class BnVariableRefreshRateService : public BnInterface<IVariableRefreshRateService> {
    public:
        virtual status_t onTransact(uint32_t code, const Parcel &data, Parcel *reply, uint32_t flags = 0);
    };

};

#endif //ANDROID_VRR_SERVICE_H
