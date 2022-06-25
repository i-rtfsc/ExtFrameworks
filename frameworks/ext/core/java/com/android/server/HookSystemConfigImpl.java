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
package com.android.server;

import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Xml;

import com.android.internal.util.XmlUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import system.ext.utils.JosLog;

public class HookSystemConfigImpl implements HookSystemConfig {
    private static final String TAG = HookSystemConfigImpl.class.getSimpleName();
    private static final boolean DEBUG = true;
    static HookSystemConfigImpl sInstance;
    private final ArraySet<String> mBackgroundServices = new ArraySet<>();
    private SystemConfig mSystemConfig;

    public HookSystemConfigImpl() {
    }

    public static HookSystemConfigImpl getInstance() {
        synchronized (HookSystemConfigImpl.class) {
            if (sInstance == null) {
                sInstance = new HookSystemConfigImpl();
            }
            return sInstance;
        }
    }

    @Override
    public void init(SystemConfig systemConfig) {
        mSystemConfig = systemConfig;
    }

    @Override
    public void readPermissionsFromXml(File file) {
        FileInputStream stream;
        synchronized (file) {
            try {
                stream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                JosLog.e(TAG, "jos-feature.xml not found; Skipping.");
                return;
            }
            boolean success = false;
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(stream, null);
                int type;
                success = true;
                while ((type = parser.next()) != XmlPullParser.START_TAG
                        && type != XmlPullParser.END_DOCUMENT) {
                    ;
                }
                if (type != XmlPullParser.START_TAG) {
                    throw new IllegalStateException("no start tag found");
                }

                int outerDepth = parser.getDepth();
                while ((type = parser.next()) != XmlPullParser.END_DOCUMENT
                        && (type != XmlPullParser.END_TAG || parser.getDepth() > outerDepth)) {
                    if (type == XmlPullParser.END_TAG
                            || type == XmlPullParser.TEXT) {
                        continue;
                    }

                    String name = parser.getName();
                    switch (name) {
                        case "background-services":
                            String packageName = parser.getAttributeValue(null, "package");
                            mBackgroundServices.add(packageName);
                            break;
                        default:
                            JosLog.w(TAG, "Tag " + name + " is unknown in "
                                    + file + " at " + parser.getPositionDescription());
                            XmlUtils.skipCurrentTag(parser);
                            break;
                    }
                }
            } catch (IllegalStateException e) {
                JosLog.w(TAG, "Failed parsing " + e.toString());
            } catch (NullPointerException e) {
                JosLog.w(TAG, "Failed parsing " + e.toString());
            } catch (NumberFormatException e) {
                JosLog.w(TAG, "Failed parsing " + e.toString());
            } catch (XmlPullParserException e) {
                JosLog.w(TAG, "Failed parsing " + e.toString());
            } catch (IOException e) {
                JosLog.w(TAG, "Failed parsing " + e.toString());
            } catch (IndexOutOfBoundsException e) {
                JosLog.w(TAG, "Failed parsing " + e.toString());
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public boolean isExtFile(File file) {
        boolean extFile = false;
        if (file != null) {
            extFile = file.getPath().endsWith("etc/permissions/jos-feature.xml");
        }

        return extFile;
    }

    @Override
    public boolean supportBackgroundService(String packageName) {
        if (DEBUG) {
            JosLog.d(TAG, "query packageName = [" + packageName + "]");
        }
        boolean support = false;
        if (TextUtils.isEmpty(packageName)) {
            JosLog.w(TAG, "query packageName was null...");
            return support;
        }
        for (String backgroundService : mBackgroundServices) {
            if (DEBUG) {
                JosLog.d(TAG, "background service = [" + backgroundService + "]");
            }

            if (packageName.equals(backgroundService)) {
                support = true;
                break;
            }
        }

        return support;
    }

}
