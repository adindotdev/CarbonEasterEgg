/*
 * Copyright (C) 2018 Adin Kwok (adinkwok)
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

package org.carbonrom.carboneasteregg;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.carbonrom.carboneasteregg.thread.GameThread;
import org.carbonrom.carboneasteregg.view.SingleGameView;


public class CarbonEasterEgg extends Activity {
    private LinearLayout mLinearLayout;
    private SingleGameView mSingleGameView;
    private GameThread mGameThread;

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("GAME", "GAME CREATED, YEAH!");
        super.onCreate(savedInstanceState);
        enableImmersiveMode();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int screenHeight = size.y;
        int screenWidth = size.x;
        if (screenWidth > screenHeight) {
            screenHeight = size.x;
            screenWidth = size.y;
        }
        mLinearLayout = new LinearLayout(this);
        mLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        mLinearLayout.setBackgroundColor(Color.WHITE);
        mSingleGameView = new SingleGameView(this, screenHeight, screenWidth);
        mGameThread = new GameThread(mSingleGameView.getHolder(), mSingleGameView);
        mGameThread.setRunning(true);
        mGameThread.start();
        mLinearLayout.addView(mSingleGameView);
        setContentView(mLinearLayout);

        new Handler().postDelayed(() -> {
            startCountdown(3);
        }, 1250);
    }

    private void startCountdown(int countdown) {
        if (mSingleGameView != null) {
            mSingleGameView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            mSingleGameView.setCountdown(countdown);
            new Handler().postDelayed(() -> {
                if (mSingleGameView != null) {
                    if (countdown - 1 > 0) {
                        startCountdown(countdown - 1);
                    } else {
                        mSingleGameView.setGameOn(true);
                        mSingleGameView.startTimer();
                    }
                } else {
                    finish();
                }
            }, 1000);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            enableImmersiveMode();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        while (true) {
            try {
                mGameThread.setRunning(false);
                mGameThread.join();
                mGameThread = null;
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d("GAME", "GAME PAUSED, YEAH!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGameThread == null) {
            mGameThread = new GameThread(mSingleGameView.getHolder(), mSingleGameView);
            mGameThread.setRunning(true);
            mGameThread.start();
            Log.d("GAME", "GAME RESUMED, YEAH!");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("GAME", "GAME DESTROYED, aw...");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("GAME", "BACK PRESSED");
    }

    private void enableImmersiveMode() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}