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

package system.ext.utils;

import android.util.Slog;

public class JosLog {
    private static final String TAG = "JOS";
    private static final boolean ENABLE_GLOBAL_TAG = true;

    private static Arg parse(String... args) {
        String tag = null;
        if (ENABLE_GLOBAL_TAG) {
            tag = TAG;
        }
        String msg = args[args.length - 1];
        for (int i = 0; i < args.length - 1; i++) {
            if (tag == null) {
                tag = args[i];
            } else {
                tag = tag + "-" + args[i];
            }
        }

        return new Arg(tag, msg);
    }

    public static void i(String... args) {
        Arg arg = parse(args);
        Slog.i(arg.tag, arg.msg);
    }

    public static void d(String... args) {
        Arg arg = parse(args);
        Slog.d(arg.tag, arg.msg);
    }

    public static void v(String... args) {
        Arg arg = parse(args);
        Slog.v(arg.tag, arg.msg);
    }

    public static void w(String... args) {
        Arg arg = parse(args);
        Slog.w(arg.tag, arg.msg);
    }

    public static void e(String... args) {
        Arg arg = parse(args);
        Slog.e(arg.tag, arg.msg);
    }

    private static class Arg {
        private String tag;
        private String msg;

        public Arg(String tag, String msg) {
            this.tag = tag;
            this.msg = msg;
        }
    }

}
