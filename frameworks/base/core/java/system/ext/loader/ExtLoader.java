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

import system.ext.utils.JosLog;

public class ExtLoader {
    private static final String TAG = ExtLoader.class.getSimpleName();

    public static <T> ExtBuilder<T> type(Class<T> extClass) {
        return new ExtBuilder().type(extClass);
    }

    public static class ExtBuilder<T> {
        private Object mBase;
        private Class<T> mExtClass;

        private ExtBuilder() {
        }

        public ExtBuilder<T> type(Class<T> extClass) {
            this.mExtClass = extClass;
            return this;
        }

        public ExtBuilder<T> base(Object base) {
            this.mBase = base;
            return this;
        }

        public T create() {
            ExtCreator<T> extCreator = ExtRegistry.getExt(this.mExtClass);
            if (extCreator != null) {
                JosLog.d(TAG, this.mExtClass + " createExtWith");
                return extCreator.createExtWith(this.mBase);
            }

            JosLog.d(TAG, this.mExtClass + " create null");
            return null;
        }
    }
}
