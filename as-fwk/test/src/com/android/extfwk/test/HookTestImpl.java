package com.android.extfwk.test;

import android.util.Log;

public class HookTestImpl implements HookTest {
    private static final String TAG = HookTestImpl.class.getSimpleName();

    public HookTestImpl() {
    }

    @Override
    public void test(int uid) {
        Log.d(TAG, "test() called with: uid = [" + uid + "]");
    }
}
