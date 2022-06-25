package com.android.extfwk.test.fake;

import android.util.Log;

import com.android.extfwk.test.HookTest;

public class ExtTest implements HookTest {
    private static final String TAG = ExtTest.class.getSimpleName();

    public ExtTest() {
    }

    @Override
    public void test(int uid) {
        Log.d(TAG, "test() called with: uid = [" + uid + "]");
    }
}
