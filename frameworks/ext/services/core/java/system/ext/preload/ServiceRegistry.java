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

import com.android.server.display.IExtDisplayManagerService;
import com.android.server.display.ExtDisplayManagerService;

import com.android.server.IExtSystemServer;
import com.android.server.ExtSystemServer;

import system.ext.loader.ExtCreator;
import system.ext.loader.ExtRegistry;

public class ServiceRegistry {
    private static final String TAG = ServiceRegistry.class.getSimpleName();
    private static final boolean DEBUG = true;
    private static ServiceRegistry sInstance;
    private boolean init = false;

    public static ServiceRegistry getInstance() {
        synchronized (ServiceRegistry.class) {
            if (sInstance == null) {
                sInstance = new ServiceRegistry();
            }
            return sInstance;
        }
    }

    public ServiceRegistry() {
        init();
    }

    public void init() {
        if (!init) {
            ExtRegistry.registerExt(IExtSystemServer.class, new ExtCreator<IExtSystemServer>() {
                @Override
                public IExtSystemServer createExtWith(Object obj) {
                    return new ExtSystemServer();
                }
            });
            ExtRegistry.registerExt(IExtDisplayManagerService.class, new ExtCreator<IExtDisplayManagerService>() {
                @Override
                public IExtDisplayManagerService createExtWith(Object obj) {
                    return new ExtDisplayManagerService(obj);
                }
            });
            init = true;
        }
    }

}
