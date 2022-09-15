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

import com.journeyOS.server.godeye.IGodEyeListener;

interface IGodEyeService {
    void registerListener(in IGodEyeListener listener);
    void unregisterListener(in IGodEyeListener listener);
    void setFactor(long factors);
    void updateFactor(long factors);
    void removeFactor(long factors);
    void activityResumed(String packageName);
    void onCameraConnected(int cameraId, String clientPackageName);
    void onCameraDisconnected(int cameraId, String clientPackageName);
    void onVideoStarted(int callingPid);
    void onVideoStopped(int callingPid);
    void onAudioStarted(int callingPid, int stream, long track);
    void onAudioStopped(int callingPid, int stream, long track);
}
