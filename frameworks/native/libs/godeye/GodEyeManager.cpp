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
#include "GodEyeManager.h"

namespace android {
    const String16 sServiceName("god_eye");

    GodEyeManager *GodEyeManager::sInstance = NULL;

    GodEyeManager::GodEyeManager() {
    }

    GodEyeManager::~GodEyeManager() {
    }

    GodEyeManager *GodEyeManager::getInstance() {
        if (sInstance == NULL) {
            sInstance = new GodEyeManager();
        }

        return sInstance;
    }

    void GodEyeManager::release() {
        LOGI("release");
        if (sInstance != NULL) {
            delete sInstance;
            sInstance = NULL;
        }
    }

    sp <IGodEyeService> GodEyeManager::getServicePolicy() {
        if (mServicePolicy == nullptr) {
            sp <IServiceManager> sm = defaultServiceManager();
            sp <IBinder> binder = sm->checkService(String16(sServiceName));
            if (binder != NULL) {
                mServicePolicy = interface_cast<IGodEyeService>(binder);
                mDeathListener = new DeathNotifier(mServicePolicy);
                binder->linkToDeath(mDeathListener);
            }
        }
        return mServicePolicy;
    }

    void GodEyeManager::registerListener(const sp<IGodEyeListener>& listener) {
        LOGI("register listener = %p", &listener);
        if (mServicePolicy == nullptr) {
            getServicePolicy();
        }
        mServicePolicy->registerListener(listener);
    }

    void GodEyeManager::unregisterListener(const sp<IGodEyeListener>& listener) {
        LOGI("register listener = %p", &listener);
        if (mServicePolicy == nullptr) {
            getServicePolicy();
        }
        mServicePolicy->unregisterListener(listener);
    }

    void GodEyeManager::setFactor(const int64_t factors) {
        LOGI("set factors = %d", factors);
        if (mServicePolicy == nullptr) {
            getServicePolicy();
        }
        mServicePolicy->setFactor(factors);
    }

    void GodEyeManager::updateFactor(const int64_t factors) {
        LOGI("update factors = %d", factors);
        if (mServicePolicy == nullptr) {
            getServicePolicy();
        }
        mServicePolicy->updateFactor(factors);
    }

    void GodEyeManager::removeFactor(const int64_t factors) {
        LOGI("remove factors = %d", factors);
        if (mServicePolicy == nullptr) {
            getServicePolicy();
        }
        mServicePolicy->removeFactor(factors);
    }

    void GodEyeManager::activityResumed(const String16& packageName) {
        LOGI("activity resumed, packageName = %s", String16(packageName).string());
        if (mServicePolicy == nullptr) {
            getServicePolicy();
        }
        mServicePolicy->activityResumed(packageName);
    }

    void GodEyeManager::onCameraConnected(const int cameraId, const String16& clientPackageName) {
        LOGI("on camera connected, cameraId = %d, clientPackageName = %s", cameraId, String16(clientPackageName).string());
        if (mServicePolicy == nullptr) {
            getServicePolicy();
        }
        mServicePolicy->onCameraConnected(cameraId, clientPackageName);
    }

    void GodEyeManager::onCameraDisconnected(const int cameraId, const String16& clientPackageName) {
        LOGI("on camera disconnected, cameraId = %d, clientPackageName = %s", cameraId, String16(clientPackageName).string());
        if (mServicePolicy == nullptr) {
            getServicePolicy();
        }
        mServicePolicy->onCameraDisconnected(cameraId, clientPackageName);
    }

    void GodEyeManager::onVideoStarted(const int callingPid) {
        LOGI("on video started, callingPid = %d", callingPid);
        if (mServicePolicy == nullptr) {
            getServicePolicy();
        }
        mServicePolicy->onVideoStarted(callingPid);
    }

    void GodEyeManager::onVideoStopped(const int callingPid) {
        LOGI("on video stopped, callingPid = %d", callingPid);
        if (mServicePolicy == nullptr) {
            getServicePolicy();
        }
        mServicePolicy->onVideoStopped(callingPid);
    }

    void GodEyeManager::onAudioStarted(const int callingPid, const int stream, const int64_t track) {
        LOGI("on audio started, callingPid = %d， stream = %d", callingPid, stream);
        if (mServicePolicy == nullptr) {
            getServicePolicy();
        }
        mServicePolicy->onAudioStarted(callingPid, stream, track);
    }

    void GodEyeManager::onAudioStopped(const int callingPid, const int stream, const int64_t track) {
        LOGI("on audio stopped, callingPid = %d， stream = %d", callingPid, stream);
        if (mServicePolicy == nullptr) {
            getServicePolicy();
        }
        mServicePolicy->onAudioStopped(callingPid, stream, track);
    }

    GodEyeManager::DeathNotifier::DeathNotifier(sp <IGodEyeService> &service) {
        mService = service;
    }

    GodEyeManager::DeathNotifier::~DeathNotifier() {
    }

    void GodEyeManager::DeathNotifier::binderDied(const wp <IBinder> &who) {
        //LOGE("listener for process godeye was died");
        mService = nullptr;
    }
};
