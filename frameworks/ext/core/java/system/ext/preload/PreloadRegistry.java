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
package system.ext.preload;

import com.android.server.IExtSystemConfig;
import com.android.server.ExtSystemConfig;

import system.ext.loader.ExtCreator;
import system.ext.loader.ExtRegistry;

public class PreloadRegistry {
    private static final String TAG = PreloadRegistry.class.getSimpleName();
    private static final boolean DEBUG = true;
    private static PreloadRegistry sInstance;
    private boolean init = false;

    public static PreloadRegistry getInstance() {
        synchronized (PreloadRegistry.class) {
            if (sInstance == null) {
                sInstance = new PreloadRegistry();
            }
            return sInstance;
        }
    }

    public PreloadRegistry() {
        init();
    }

    public void init() {
        if (!init) {
            ExtRegistry.registerExt(IExtSystemConfig.class, new ExtCreator<IExtSystemConfig>() {
                @Override
                public IExtSystemConfig createExtWith(Object obj) {
                    return ExtSystemConfig.getInstance(obj);
                }
            });
            init = true;
        }
    }

}
