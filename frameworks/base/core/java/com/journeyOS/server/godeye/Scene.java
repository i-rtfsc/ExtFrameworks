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

import android.annotation.SystemApi;
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
    private String activity = "";
    private int app;
    private int status;
    private String value;

    public Scene(long factorId, String packageName, String activity, int app, int status, String value) {
        this.factorId = factorId;
        this.packageName = packageName;
        this.activity = activity;
        this.app = app;
        this.status = status;
        this.value = value;
    }

    protected Scene(Parcel in) {
        factorId = in.readLong();
        packageName = in.readString();
        activity = in.readString();
        app = in.readInt();
        status = in.readInt();
        value = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(factorId);
        dest.writeString(packageName);
        dest.writeString(activity);
        dest.writeInt(app);
        dest.writeInt(status);
        dest.writeString(value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public long getFactorId() {
        return factorId;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getActivity() {
        return activity;
    }

    public int getApp() {
        return app;
    }

    public int getStatus() {
        return status;
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
                ", activity='" + activity + '\'' +
                ", app=" + app +
                ", status=" + status +
                ", value='" + value + '\'' +
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

    /**
     * Builder
     */
    @SystemApi
    public static class Builder {
        private static final int UNSET = -1;
        private long factorId = UNSET;
        private String packageName = null;
        private String activity = null;
        private int app = UNSET;
        private int status = UNSET;
        private String value = null;
        private Scene mScene = null;

        public Builder setFactorId(long factorId) {
            this.factorId = factorId;
            return this;
        }

        public Builder setPackageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Builder setActivity(String activity) {
            this.activity = activity;
            return this;
        }

        public Builder setApp(int app) {
            this.app = app;
            return this;
        }

        public Builder setStatus(int status) {
            this.status = status;
            return this;
        }

        public Builder setValue(String value) {
            this.value = value;
            return this;
        }

        public Builder copy(Scene scene) {
            this.mScene = scene;
            return this;
        }

        public Scene build() {
            if (mScene == null) {
                return new Scene(factorId, packageName, activity, app, status, value);
            } else {
                if (factorId == UNSET) {
                    factorId = mScene.factorId;
                }

                if (packageName == null) {
                    packageName = mScene.packageName;
                }

                if (activity == null) {
                    activity = mScene.activity;
                }

                if (app == UNSET) {
                    app = mScene.app;
                }

                if (status == UNSET) {
                    status = mScene.status;
                }

                if (value == null) {
                    value = mScene.value;
                }
                mScene = null;
                return new Scene(factorId, packageName, activity, app, status, value);
            }
        }
    }
}
