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

#include <binder/IServiceManager.h>
#include <utils/Log.h>
#include <utils/String8.h>

#include "log.h"
#include "VRRManager.h"

namespace android {
    const String16 sServiceName("vrr");

    VRRManager *VRRManager::sInstance = NULL;

    VRRManager::VRRManager() {
    }

    VRRManager::~VRRManager() {
    }

    VRRManager *VRRManager::getInstance() {
        if (sInstance == NULL) {
            sInstance = new VRRManager();
        }

        return sInstance;
    }

    sp <IVariableRefreshRateService> VRRManager::getServicePolicy() {
        if (mServicePolicy == nullptr) {
            sp <IServiceManager> sm = defaultServiceManager();
            sp <IBinder> binder = sm->checkService(String16(sServiceName));
            if (binder != NULL) {
                mServicePolicy = interface_cast<IVariableRefreshRateService>(binder);
                mDeathListener = new DeathNotifier(mServicePolicy);
                binder->linkToDeath(mDeathListener);
            }
        }
        return mServicePolicy;
    }

    void VRRManager::setRefreshRatePolicy(int displayId, float rate, int policy, bool statusOn) {
        if (mServicePolicy == nullptr) {
            getServicePolicy();
        }
        mServicePolicy->setRefreshRatePolicy(displayId, rate, policy, statusOn);
    }

    void VRRManager::onFpsChange(int fps) {
        LOGI("on fps change, fps = %d", fps);
        if (mServicePolicy == nullptr) {
            getServicePolicy();
        }
        mServicePolicy->onFpsChange(fps);
    }

    VRRManager::DeathNotifier::DeathNotifier(sp <IVariableRefreshRateService> &service) {
        mService = service;
    }

    VRRManager::DeathNotifier::~DeathNotifier() {
    }

    void VRRManager::DeathNotifier::binderDied(const wp <IBinder> &who) {
        //LOGE("listener for process vrr was died");
        mService = nullptr;
    }
};
