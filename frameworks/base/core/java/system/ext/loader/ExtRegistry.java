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
package system.ext.loader;

import java.util.HashMap;
import java.util.Map;

public class ExtRegistry {
    private static final Map<Class<?>, ExtCreator<?>> EXT_CREATORS = new HashMap();

    public static <T> void registerExt(Class<T> extClass, ExtCreator<T> extFetcher) {
        if (extClass != null && extFetcher != null) {
            EXT_CREATORS.put(extClass, extFetcher);
        }
    }

    public static <T> ExtCreator<T> getExt(Class<T> extClass) {
        if (extClass == null) {
            return null;
        }
        return (ExtCreator<T>) EXT_CREATORS.get(extClass);
    }
}