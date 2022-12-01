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
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Xml;

import com.android.internal.util.XmlUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import system.ext.utils.JosLog;

public class HookSystemConfigImpl implements HookSystemConfig {
    private static final String TAG = HookSystemConfigImpl.class.getSimpleName();
    private static final boolean DEBUG = false;

    private static final ArrayMap<String, String> GOG_EYE_APPS = new ArrayMap<>();

    private static final String UNKNOWN = "unknown";
    private static final String ALBUM = "album";
    private static final String BROWSER = "browser";
    private static final String GAME = "game";
    private static final String IM = "im";
    private static final String MUSIC = "music";
    private static final String NEWS = "news";
    private static final String READER = "reader";
    private static final String VIDEO = "video";

    static HookSystemConfigImpl sInstance;
    private final ArraySet<String> mBackgroundServices = new ArraySet<>();
    private SystemConfig mSystemConfig;

    ArrayList<String> mFile = new ArrayList<String>() {{
        add("etc/permissions/jos-feature.xml");
        add("etc/sysconfig/jos-apps.xml");
    }};

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
                        case "background-services": {
                            String packageName = parser.getAttributeValue(null, "package");
                            mBackgroundServices.add(packageName);
                            break;
                        }
                        case "god-eye-app": {
                            String packageName = parser.getAttributeValue(null, "package");
                            String appType = parser.getAttributeValue(null, "type");
                            if (!GOG_EYE_APPS.containsKey(packageName)) {
                                GOG_EYE_APPS.put(packageName, appType);
                            }
                            break;
                        }
                        default:
                            JosLog.w(TAG, "Tag " + name + " is unknown in "
                                    + file + " at " + parser.getPositionDescription());
                            XmlUtils.skipCurrentTag(parser);
                            break;
                    }
                }
            } catch (IllegalStateException e) {
                JosLog.w(TAG, "Failed parsing " + e);
            } catch (NullPointerException e) {
                JosLog.w(TAG, "Failed parsing " + e);
            } catch (NumberFormatException e) {
                JosLog.w(TAG, "Failed parsing " + e);
            } catch (XmlPullParserException e) {
                JosLog.w(TAG, "Failed parsing " + e);
            } catch (IOException e) {
                JosLog.w(TAG, "Failed parsing " + e);
            } catch (IndexOutOfBoundsException e) {
                JosLog.w(TAG, "Failed parsing " + e);
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
            for (String josFile : mFile) {
                extFile = file.getPath().endsWith(josFile);
                break;
            }
        }

        return extFile;
    }

    @Override
    public boolean supportBackgroundService(String packageName) {
        boolean support = false;
        if (TextUtils.isEmpty(packageName)) {
            JosLog.w(TAG, "query packageName was null...");
            return support;
        }
        for (String backgroundService : mBackgroundServices) {
            if (packageName.equals(backgroundService)) {
                support = true;
                break;
            }
        }

        if (DEBUG) {
            JosLog.i(TAG, "packageName = [" + packageName + "], support background service = [" + support + "]");
        }
        return support;
    }

    @Override
    public String getGodEyeAppType(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            JosLog.w(TAG, "query packageName was null...");
            return UNKNOWN;
        }

        String appType = GOG_EYE_APPS.get(packageName);
        if (TextUtils.isEmpty(appType)) {
            JosLog.w(TAG, "packageName = [" + packageName + "], not in god-eye-app list...");
            return UNKNOWN;
        }

        if (DEBUG) {
            JosLog.i(TAG, "packageName = [" + packageName + "], type = [" + appType + "]");
        }
        return appType;
    }

    @Override
    public boolean isAlbum(String packageName) {
        String appType = getGodEyeAppType(packageName);
        return ALBUM.equals(appType);
    }

    @Override
    public boolean isBrowser(String packageName) {
        String appType = getGodEyeAppType(packageName);
        return BROWSER.equals(appType);
    }

    @Override
    public boolean isGame(String packageName) {
        String appType = getGodEyeAppType(packageName);
        return GAME.equals(appType);
    }

    @Override
    public boolean isIM(String packageName) {
        String appType = getGodEyeAppType(packageName);
        return IM.equals(appType);
    }

    @Override
    public boolean isMusic(String packageName) {
        String appType = getGodEyeAppType(packageName);
        return MUSIC.equals(appType);
    }

    @Override
    public boolean isNews(String packageName) {
        String appType = getGodEyeAppType(packageName);
        return NEWS.equals(appType);
    }

    @Override
    public boolean isReader(String packageName) {
        String appType = getGodEyeAppType(packageName);
        return READER.equals(appType);
    }

    @Override
    public boolean isVideo(String packageName) {
        String appType = getGodEyeAppType(packageName);
        return VIDEO.equals(appType);
    }

}
