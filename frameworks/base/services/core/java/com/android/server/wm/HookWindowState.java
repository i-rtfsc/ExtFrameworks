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

package com.android.server.wm;

import system.ext.hook.Inject;

public interface HookWindowState {

    static HookWindowState get() {
        return (HookWindowState) Inject.getInstance().getInject(HookWindowState.class, null, true);
    }

    default int getPid() {
        return 0;
    }

    default float getPreferredRefreshRate() {
        return 0;
    }

    default void setPreferredRefreshRate(int pid, float refreshRate) {
    }

}
