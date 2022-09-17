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

import java.io.FileDescriptor;
import java.io.PrintWriter;

import system.ext.utils.JosLog;

public class VrrDumpCommand {
    private static final String TAG = VrrDumpCommand.class.getSimpleName();

    private static final String H = "-h";
    private static final String HELP = "-help";

    private static final int POSITION_RATE = 0;
    private static final int POSITION_POLICY = 1;
    private static final int POSITION_STATUS = 2;

    private Context mContext;

    private float mArgRate = 60.0f;
    private String mArgPolicy = "system";
    private int mArgStatus = 0;

    public VrrDumpCommand(Context context) {
        mContext = context;
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (pw == null || args == null) {
            JosLog.w(VRRManager.VRR_TAG, TAG, "writer or args wasn't null");
            return;
        }

        for (int position = 0; position < args.length; position++) {
            String opt = args[position];
            JosLog.v(VRRManager.VRR_TAG, TAG, "position = [" + position + "]," + " cmd = [" + opt + "]");
            if ((H.equals(opt) || HELP.equals(opt))) {
                dumpHelp(pw);
                return;
            }

            switch (position) {
                case POSITION_RATE:
                    mArgRate = Float.parseFloat(opt);
                    break;
                case POSITION_POLICY:
                    mArgPolicy = opt;
                    break;
                case POSITION_STATUS:
                    mArgStatus = Integer.parseInt(opt);
                    break;
            }

        }

        JosLog.i(VRRManager.VRR_TAG, TAG, "dump with refresh rate = [" + mArgRate + "]," +
                " policy = [" + mArgPolicy + "]," +
                " status = [" + mArgStatus + "]");
        //VrrSurfaceFlinger.getDefault().setRefreshRate(mArgRate);
        VrrDisplayModeDirector.getDefault().setRefreshRate(mContext, mArgRate);
    }

    private void dumpHelp(PrintWriter pw) {
        pw.println("vrr service commands:");
        pw.println("  help");
        pw.println("      Print this help text.");
        pw.println("      arg1(float) refresh rate");
        pw.println("      arg2(String) policy");
        pw.println("      arg3(int) status");
        //TODO
    }

}
