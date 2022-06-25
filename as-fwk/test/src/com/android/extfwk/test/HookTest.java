package com.android.extfwk.test;

import system.ext.hook.Inject;

public interface HookTest {
    static HookTest get() {
//        return (HooTest) Inject.getInstance().getInject(HooTest.class, "com.android.extfwk.test.fake.ExtTest");
        return (HookTest) Inject.getInstance().getInject(HookTest.class);
    }

    default void test(int uid) {
    }
}
