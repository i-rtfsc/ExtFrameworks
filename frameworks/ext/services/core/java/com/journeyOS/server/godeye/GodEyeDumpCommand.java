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

package com.journeyOS.server.godeye;

import com.journeyOS.server.godeye.monitor.MonitorManager;
import com.journeyOS.server.godeye.monitor.PackageNameMonitor;

import java.io.FileDescriptor;
import java.io.PrintWriter;

import system.ext.utils.JosLog;

public class GodEyeDumpCommand {
    private static final String TAG = GodEyeDumpCommand.class.getSimpleName();

    private static final String H = "-h";
    private static final String HELP = "-help";

    private static final int POSITION_FACTOR_ID = 0;
    private static final int POSITION_PACKAGE_NAME = 1;
    private static final int POSITION_TYPE = 2;
    private static final int POSITION_STATUS = 3;

    private long mArgFactorId;
    private String mArgPackageName;
    private int mArgType;
    private int mArgStatus;

    public GodEyeDumpCommand() {
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (pw == null || args == null) {
            JosLog.w(GodEyeManager.GOD_EYE_TAG, TAG, "writer or args wasn't null");
            return;
        }

        for (int position = 0; position < args.length; position++) {
            String opt = args[position];
            JosLog.v(GodEyeManager.GOD_EYE_TAG, TAG, "position = [" + position + "]," + " cmd = [" + opt + "]");
            if ((H.equals(opt) || HELP.equals(opt))) {
                dumpHelp(pw);
                return;
            }

            switch (position) {
                case POSITION_FACTOR_ID:
                    mArgFactorId = Long.parseLong(opt);
                    break;
                case POSITION_PACKAGE_NAME:
                    mArgPackageName = opt;
                    break;
                case POSITION_TYPE:
                    mArgType = Integer.parseInt(opt);
                    break;
                case POSITION_STATUS:
                    mArgStatus = Integer.parseInt(opt);
                    break;
            }
        }

        JosLog.i(GodEyeManager.GOD_EYE_TAG, TAG, "dump with factorId = [" + mArgFactorId + "]," +
                " package name = [" + mArgPackageName + "]," +
                " type = [" + mArgType + "]," +
                " status = [" + mArgStatus + "]");
        test();
    }

    private void dumpHelp(PrintWriter pw) {
        pw.println("godeye service commands:");
        pw.println("  help");
        pw.println("      Print this help text.");
        pw.println("      arg1(long) factorId");
        pw.println("      arg2(String) packageName");
        pw.println("      arg3(int) type");
        pw.println("      arg4(int) status");
        //TODO
    }

    private void test() {
        Scene scene = new Scene(mArgFactorId);
        scene.setPackageName(mArgPackageName);
        scene.setType(PackageNameMonitor.getInstance().convertType(mArgPackageName));
        scene.setStatus(mArgStatus);
        MonitorManager.getInstance().notifyResult(scene, true);
    }

}
