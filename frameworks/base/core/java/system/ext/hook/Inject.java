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

package system.ext.hook;

import android.text.TextUtils;
import android.util.ArrayMap;

import system.ext.reflect.MethodUtils;
import system.ext.utils.JosLog;

public class Inject {
    private static final String TAG = Inject.class.getSimpleName();
    private static final boolean DEBUG = false;
    private static final ArrayMap<Class<?>, Object> INJECT_MAPS = new ArrayMap<>();
    private static Inject sInstance;

    public Inject() {
    }

    public static Inject getInstance() {
        synchronized (Inject.class) {
            if (sInstance == null) {
                sInstance = new Inject();
            }
            return sInstance;
        }
    }

    private static Object create(String clsName, ClassLoader classLoader, boolean isSingleton) {
        JosLog.i(TAG, "impl class = [" + clsName + "]");
        try {
            Class<?> clazz;
            if (classLoader != null) {
                clazz = Class.forName(clsName, false, classLoader);
            } else {
                clazz = Class.forName(clsName);
            }
            if (clazz != null) {
                if (isSingleton) {
                    return MethodUtils.invokeStaticMethod(clazz, "getInstance");
                } else {
                    return MethodUtils.invokeConstructor(clazz);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getInject(Class tClass) {
        return getInject(tClass, null, false);
    }

    public Object getInject(Class tClass, String dClass) {
        return getInject(tClass, dClass, false);
    }

    public Object getInject(Class tClass, String dClass, boolean isSingleton) {
        if (DEBUG) {
            JosLog.d(TAG, "interface class = [" + tClass + "], impl class = [" + dClass + "], isSingleton = [" + isSingleton + "]");
        }

        if (!INJECT_MAPS.containsKey(tClass)) {
            String clsName = dClass;
            if (TextUtils.isEmpty(clsName)) {
                clsName = tClass.getCanonicalName() + "Impl";
            }
            if (DEBUG) {
                JosLog.d(TAG, "impl class = [" + tClass + "]");
            }

            Object inject = create(clsName, tClass.getClassLoader(), isSingleton);
            if (DEBUG) {
                JosLog.d(TAG, "constructor impl class = [" + tClass + "]");
            }

            if (inject != null && tClass.isAssignableFrom(inject.getClass())) {
                INJECT_MAPS.put(tClass, inject);
            } else {
                INJECT_MAPS.put(tClass, null);
            }
        }
        return INJECT_MAPS.get(tClass);
    }
}