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

#include "IVariableRefreshRateService.h"

namespace android {

    class BpVariableRefreshRateService : public BpInterface<IVariableRefreshRateService> {
    public:
        BpVariableRefreshRateService(const sp <IBinder> &binder)
                : BpInterface<IVariableRefreshRateService>(binder) {
        }

        void setRefreshRatePolicy(int displayId, float rate, int policy, bool statusOn) {
            Parcel data, reply;
            data.writeInterfaceToken(BpVariableRefreshRateService::getInterfaceDescriptor());
            data.writeInt32(displayId);
            data.writeFloat(rate);
            data.writeInt32(policy);
            data.writeBool(statusOn);
            remote()->transact(TRANSACTION_SET_REFRESH_RATE_POLICY, data, &reply, IBinder::FLAG_ONEWAY);
        }

        void onFpsChange(int fps) {
            Parcel data, reply;
            data.writeInterfaceToken(BpVariableRefreshRateService::getInterfaceDescriptor());
            data.writeInt32(fps);
            remote()->transact(TRANSACTION_ON_FPS_CHANGE, data, &reply, IBinder::FLAG_ONEWAY);
        }
    };

    IMPLEMENT_META_INTERFACE(VariableRefreshRateService,
    "com.journeyOS.server.godeye.IVariableRefreshRateService");

    status_t
    BnVariableRefreshRateService::onTransact(uint32_t code, const Parcel &data, Parcel *reply, uint32_t flags) {
        switch (code) {
            default:
                return BBinder::onTransact(code, data, reply, flags);
        }
    }
};
