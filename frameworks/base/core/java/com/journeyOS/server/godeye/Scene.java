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

    private final long factorId;
    private String packageName = "";
    private int type;
    private int status;

    public Scene(long factorId) {
        this.factorId = factorId;
    }

    protected Scene(Parcel in) {
        factorId = in.readLong();
        packageName = in.readString();
        type = in.readInt();
        status = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(factorId);
        dest.writeString(packageName);
        dest.writeInt(type);
        dest.writeInt(status);
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

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Scene{" +
                "factorId=" + factorId +
                ", packageName='" + packageName + '\'' +
                ", type=" + type +
                ", status=" + status +
                '}';
    }

    public static enum App {
        UNKNOWN(0),
        ALBUM(1),
        BROWSER(2),
        GAME(3),
        IM(4),
        MUSIC(5),
        NEWS(6),
        READER(7),
        VIDEO(8);

        public final int ordinal;

        private App(int ordinal) {
            this.ordinal = ordinal;
        }
    }
}
