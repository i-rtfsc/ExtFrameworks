package com.android.extfwk.test;

import android.content.Context;

import system.ext.hook.Inject;

public interface HookTest {
    static HookTest get() {
        return (HookTest) Inject.getInstance().getInject(HookTest.class);
    }

    default void test(Context context) {
    }
}
