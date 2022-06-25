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

package com.android.server.display;

import android.content.Context;
import android.os.Parcel;
import android.os.RemoteException;

import system.ext.hook.Inject;

public interface HookDisplayManagerService {

    static HookDisplayManagerService get() {
        return (HookDisplayManagerService) Inject.getInstance().getInject(HookDisplayManagerService.class);
    }

    void init(DisplayManagerService displayManagerService);

    default void onStart() {
    }

    default void systemReady(Context context, LogicalDisplayMapper logicalDisplayMapper) {
    }

    default boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        return false;
    }

}
