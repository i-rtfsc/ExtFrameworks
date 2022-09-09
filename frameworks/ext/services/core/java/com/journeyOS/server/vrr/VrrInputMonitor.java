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
import android.hardware.input.InputManager;
import android.os.Looper;
import android.util.Singleton;
import android.view.Display;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.InputMonitor;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import system.ext.utils.JosLog;

public class VrrInputMonitor {
    private static final String TAG = VrrInputMonitor.class.getSimpleName();
    private static final String INPUT_MONITOR_NAME = "vrr-monitor";
    private static final boolean DEBUG = false;

    private static final Singleton<VrrInputMonitor> gDefault = new Singleton<VrrInputMonitor>() {
        @Override
        protected VrrInputMonitor create() {
            return new VrrInputMonitor();
        }
    };

    private InputEventReceiver mInputEventReceiver = null;
    private VelocityTracker mVelocityTracker;
    private OnGestureEvent mOnGestureEvent;

    public VrrInputMonitor() {
    }

    public static VrrInputMonitor getDefault() {
        return gDefault.get();
    }

    public void startTouchMonitoring(Context context, Looper looper) {
        if (mInputEventReceiver == null) {
            //InputMonitor inputMonitor = InputManager.getInstance().monitorGestureInput(INPUT_MONITOR_NAME, Display.DEFAULT_DISPLAY);
            InputManager inputManager = (InputManager) context.getSystemService(Context.INPUT_SERVICE);
            InputMonitor inputMonitor = inputManager.monitorGestureInput(INPUT_MONITOR_NAME, Display.DEFAULT_DISPLAY);
            mInputEventReceiver = new TouchReceiver(inputMonitor.getInputChannel(), looper);
        }
    }

    public void registerGestureEventListener(OnGestureEvent callback) {
        mOnGestureEvent = callback;
    }

    private void onInputEvent(InputEvent ev) {
        if (ev instanceof MotionEvent) {
            onMotionEvent((MotionEvent) ev);
        }
    }

    private boolean onMotionEvent(MotionEvent event) {
        if (DEBUG) {
            JosLog.d(VRRManager.VRR_TAG, TAG, "masked action = [" + event.getActionMasked() + "]");
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (mOnGestureEvent != null) {
                    mOnGestureEvent.onTouch();
                }
                if (mVelocityTracker == null) {
                    initVelocityTracker();
                }
                trackMovement(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                endMotionEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                trackMovement(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                trackMovement(event);
                endMotionEvent(event);
        }
        return true;
    }

    private void initVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
        }
        mVelocityTracker = VelocityTracker.obtain();
    }

    private void trackMovement(MotionEvent event) {
        if (mVelocityTracker != null) {
            mVelocityTracker.addMovement(event);
        }
    }

    private void endMotionEvent(MotionEvent event) {
        if (DEBUG) {
            JosLog.d(VRRManager.VRR_TAG, TAG, "endMotionEvent, masked action = [" + event.getActionMasked() + "]");
        }
        if (mVelocityTracker != null) {
            mVelocityTracker.computeCurrentVelocity(1000);
            float xVelocity = Math.abs(mVelocityTracker.getXVelocity());
            float yVelocity = Math.abs(mVelocityTracker.getYVelocity());
            JosLog.d(VRRManager.VRR_TAG, TAG, "computed x velocity = [" + xVelocity + "]");
            JosLog.d(VRRManager.VRR_TAG, TAG, "computed y velocity = [" + yVelocity + "]");
            if (mOnGestureEvent != null) {
                mOnGestureEvent.onSpeed(xVelocity, yVelocity);
            }
        }

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public interface OnGestureEvent {
        void onTouch();

        void onSpeed(float xVelocity, float yVelocity);
    }

    /**
     * Receiver of all touch events. This receiver filters out all events except {@link
     * MotionEvent#ACTION_UP} events.
     */
    private class TouchReceiver extends InputEventReceiver {

        /**
         * Creates an input event receiver bound to the specified input channel.
         *
         * @param inputChannel The input channel.
         * @param looper       The looper to use when invoking callbacks.
         */
        TouchReceiver(InputChannel inputChannel, Looper looper) {
            super(inputChannel, looper);
        }

        @Override
        public void onInputEvent(InputEvent event) {
            VrrInputMonitor.this.onInputEvent(event);
            finishInputEvent(event, true);
        }
    }

}
