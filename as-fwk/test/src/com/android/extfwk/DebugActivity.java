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

package com.android.extfwk;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.extfwk.test.HookTest;


/**
 * @author anqi.huang@outlook.com
 */
public class DebugActivity extends Activity {
    private static final String TAG = DebugActivity.class.getSimpleName();
    private LinearLayout mLayout;
    private Context mContext;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        mLayout = new LinearLayout(this);
        mLayout.setOrientation(LinearLayout.VERTICAL);
        initView();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        TextView textView = new TextView(this);
        mLayout.addView(textView);

        Button button = new Button(this);
        button.setText("Set");
        button.setOnClickListener(v -> {
            Log.d(TAG, "set button click");
        });
        mLayout.addView(button);

        button = new Button(this);
        button.setText("Get");
        button.setOnClickListener(v -> {
            Log.d(TAG, "get button click");
        });
        mLayout.addView(button);


        button = new Button(this);
        button.setText("Test");
        button.setOnClickListener(v -> {
            Log.d(TAG, "test button click");
            test();
        });
        mLayout.addView(button);

        ScrollView sv = new ScrollView(this);
        sv.addView(mLayout);
        setContentView(sv);
    }

    private void test() {
        HookTest.get().test(12);
    }
}
