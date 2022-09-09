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

#ifndef ANDROID_VRR_MANAGER_H
#define ANDROID_VRR_MANAGER_H

#include <binder/IInterface.h>
#include <utils/RefBase.h>
#include <binder/Parcel.h>

#include "IVariableRefreshRateService.h"

namespace android {

    class VRRManager {
    private:
        sp <IVariableRefreshRateService> mServicePolicy;
        sp <IBinder::DeathRecipient> mDeathListener;

        sp <IVariableRefreshRateService> getServicePolicy();

        static VRRManager *sInstance;

        class DeathNotifier : public IBinder::DeathRecipient {
        private:
            sp <IVariableRefreshRateService> mService;
        public:
            DeathNotifier(sp <IVariableRefreshRateService> &service);

            virtual ~DeathNotifier();

            virtual void binderDied(const wp <IBinder> &who);
        };

    public:
        VRRManager();

        virtual ~VRRManager();

        static VRRManager *getInstance();

        void setRefreshRatePolicy(int displayId, float rate, int policy, bool statusOn);

        void onFpsChange(int fps);

    };

};

#endif //ANDROID_VRR_MANAGER_H
