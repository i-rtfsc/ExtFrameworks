package com.android.extfwk.test;

import android.content.Context;
import android.os.RemoteException;

import com.journeyOS.server.godeye.GodEyeManager;
import com.journeyOS.server.godeye.GodEyeObserver;
import com.journeyOS.server.godeye.Scene;
import com.journeyOS.server.vrr.VRRManager;
import com.journeyOS.server.vrr.VrrInputMonitor;
import com.journeyOS.server.vrr.VrrSurfaceControlProxy;
import com.journeyOS.server.vrr.VrrThread;

import system.ext.utils.JosLog;

public class HookTestImpl implements HookTest {
    private static final String TAG = HookTestImpl.class.getSimpleName();

    public HookTestImpl() {
    }

    @Override
    public void test(Context context) {
        JosLog.d(VRRManager.VRR_TAG, TAG, "test() called with: context = [" + context + "]");

        VrrInputMonitor.getDefault().startTouchMonitoring(context, VrrThread.getDefault().getLooper());
        VrrInputMonitor.getDefault().registerGestureEventListener(new VrrInputMonitor.OnGestureEvent() {
            @Override
            public void onTouch() {
                JosLog.d(VRRManager.VRR_TAG, TAG, "onTouch() called");
            }

            @Override
            public void onSpeed(float xVelocity, float yVelocity) {
                JosLog.d(VRRManager.VRR_TAG, TAG, "onSpeed() called with: xVelocity = [" + xVelocity + "], yVelocity = [" + yVelocity + "]");
            }
        });


        VrrSurfaceControlProxy.getDefault().getActiveDisplayModeId();

        GodEyeManager.getDefault().subscribeObserver(new GodEyeObserver() {
            @Override
            public void onSceneChanged(Scene scene) throws RemoteException {
                JosLog.d(GodEyeManager.GOD_EYE_TAG, TAG, "onSceneChanged() called with: scene = [" + scene + "]");
            }
        });
        GodEyeManager.getDefault().setFactor(GodEyeManager.SCENE_FACTOR_APP);
    }

}
