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

package com.journeyOS.server.vrr;

import android.content.Context;
import android.provider.Settings;
import android.util.Singleton;

public class VrrDisplayModeDirector {
    private static final String TAG = VrrDisplayModeDirector.class.getSimpleName();

    private static final Singleton<VrrDisplayModeDirector> gDefault = new Singleton<VrrDisplayModeDirector>() {
        @Override
        protected VrrDisplayModeDirector create() {
            return new VrrDisplayModeDirector();
        }
    };

    public VrrDisplayModeDirector() {
    }

    public static VrrDisplayModeDirector getDefault() {
        return gDefault.get();
    }

    public void setRefreshRate(Context context, float refreshRate) {
        Settings.System.putFloat(context.getContentResolver(), Settings.System.PEAK_REFRESH_RATE, refreshRate);
    }

}
