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

#include "IGodEyeListener.h"

namespace android {

    class BpGodEyeListener : public BpInterface<IGodEyeListener> {
    public:
        BpGodEyeListener(const sp <IBinder> &binder)
                : BpInterface<IGodEyeListener>(binder) {
        }

        void onSceneChanged(const Scene scene) {
            Parcel data, reply;
            data.writeInterfaceToken(IGodEyeListener::getInterfaceDescriptor());
            data.writeInt64(scene.factorId);
            data.writeString16(scene.packageName);
            data.writeInt32(scene.type);
            data.writeInt32(scene.status);
            remote()->transact(ON_SCENE_CHANGED, data, &reply, IBinder::FLAG_ONEWAY);
        }

    };

    IMPLEMENT_META_INTERFACE(GodEyeListener, "com.journeyOS.server.godeye.IGodEyeListener");

    status_t BnGodEyeListener::onTransact(uint32_t code, const Parcel &data, Parcel *reply, uint32_t flags) {
        switch (code) {
            case ON_SCENE_CHANGED: {
                CHECK_INTERFACE(IGodEyeListener, data, reply);
                Scene scene;
                scene.readFromParcel(data);
                onSceneChanged(scene);
                return OK;
            }
            default:
                return BBinder::onTransact(code, data, reply, flags);
        }
    }
};
