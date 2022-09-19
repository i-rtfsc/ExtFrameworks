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

import android.os.Parcel;
import android.os.Parcelable;

public class Scene implements Parcelable {

    public static final Creator<Scene> CREATOR = new Creator<Scene>() {
        @Override
        public Scene createFromParcel(Parcel in) {
            return new Scene(in);
        }

        @Override
        public Scene[] newArray(int size) {
            return new Scene[size];
        }
    };

    private long factorId;
    private String packageName = "";
    private int app;
    private int status;
    private String value;

    public Scene() {
    }

    protected Scene(Parcel in) {
        factorId = in.readLong();
        packageName = in.readString();
        app = in.readInt();
        status = in.readInt();
        value = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(factorId);
        dest.writeString(packageName);
        dest.writeInt(app);
        dest.writeInt(status);
        dest.writeString(value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void setFactorId(long factorId) {
        this.factorId = factorId;
    }

    public long getFactorId() {
        return factorId;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setApp(int app) {
        this.app = app;
    }

    public int getApp() {
        return app;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPlayer() {
        if (GodEyeManager.SCENE_FACTOR_VIDEO == factorId || GodEyeManager.SCENE_FACTOR_AUDIO == factorId) {
            return value;
        }

        return "";
    }

    public int getBrightness() {
        if (GodEyeManager.SCENE_FACTOR_BRIGHTNESS == factorId) {
            return Integer.valueOf(value);
        }

        return -1;
    }


    @Override
    public String toString() {
        return "Scene{" +
                "factorId=" + factorId +
                ", packageName='" + packageName + '\'' +
                ", app=" + app +
                ", status=" + status +
                ", value=" + value +
                '}';
    }

    public static class App {
        public static final int DEFAULT = 0;
        public static final int ALBUM = 1;
        public static final int BROWSER = 2;
        public static final int GAME = 3;
        public static final int IM = 4;
        public static final int MUSIC = 5;
        public static final int NEWS = 6;
        public static final int READER = 7;
        public static final int VIDEO = 8;
    }

    public static class State {
        public static final int UNKNOWN = 1;
        public static final int ON = 1;
        public static final int OFF = 0;
    }

}
