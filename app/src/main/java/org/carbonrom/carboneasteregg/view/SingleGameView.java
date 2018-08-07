/*
 * Copyright (C) 2018 CarbonROM
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

package org.carbonrom.carboneasteregg.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Chronometer;

import org.carbonrom.carboneasteregg.Grids;
import org.carbonrom.carboneasteregg.R;

import java.util.Objects;

public class SingleGameView extends SurfaceView implements SurfaceHolder.Callback {
    private final Chronometer mStopwatch;

    private final int mScreenHeight;
    private final int mScreenWidth;

    private final Drawable mGreyDot;
    private final Drawable mBlueDot;
    private final Drawable mGreenDot;

    /**
     * Holds data of all dots on the grid.
     * Dot data array holds the following:
     * 0 - Color
     * 1 - Number in grid
     * 2 - x-coordinate
     * 3 - y-coordinate
     * 4 - Time the dot is tapped
     */
    private int[][] mActiveDots;

    private int mDotPressBuffer;
    private int mDotSize;

    /**
     * Time counter, shown above the grid
     */
    private Paint mTextPaint;

    /**
     * The next dot to be tapped.
     */
    private int mDotIndex;

    /**
     * Use this to handle showing blank field before game
     */
    private boolean mGameOn;

    /**
     * Use this to prevent further tap input
     */
    private boolean mGameOver;

    private String mCountdown;
    private String mTimeCount;
    private int mTimeY;
    private int mTimeX;

    public SingleGameView(Context context, int screenHeight, int screenWidth) {
        super(context);
        Log.d("GAME VIEW", "GAME VIEW INITIATED, YEAH!");
        getHolder().addCallback(this);
        mStopwatch = new Chronometer(context);
        mScreenHeight = screenHeight;
        mScreenWidth = screenWidth;
        mGreyDot = getResources().getDrawable(R.drawable.ic_dot_sprite_grey, null);
        mBlueDot = getResources().getDrawable(R.drawable.ic_dot_sprite_blue, null);
        mGreenDot = getResources().getDrawable(R.drawable.ic_dot_sprite_green, null);
        mCountdown = getResources().getString(R.string.game_waiting);
        setupGame();
    }

    private void setupGame() {
        int spaceConstant = mScreenWidth / 8;
        int startingY = (mScreenHeight / 2) - (mScreenWidth / 4);
        int endingY = (mScreenHeight / 2) + (mScreenWidth / 4);
        int startingX = (mScreenWidth / 2) - (mScreenWidth / 4);
        int endingX = (mScreenWidth / 2) + (mScreenWidth / 4);
        Grids.createPattern();
        mActiveDots = new int[Grids.generatedPattern.length][5];
        // On 1080px screen, dots ≈ 50px
        mDotSize = mScreenWidth / 22;
        // On 1080p screen, buffer = 20px
        mDotPressBuffer = mDotSize + (mScreenWidth / 45);
        mDotIndex = 0;
        int index = 0;
        for (int y = startingY; y <= endingY; y += spaceConstant) {
            for (int x = startingX; x <= endingX; x += spaceConstant) {
                if (Grids.generatedPattern[index] == 1) {
                    mActiveDots[Grids.generatedPattern[index] - 1]
                            = new int[]{1, (int) Grids.generatedPattern[index], x, y, 0};
                } else {
                    mActiveDots[Grids.generatedPattern[index] - 1]
                            = new int[]{0, (int) Grids.generatedPattern[index], x, y, 0};
                }
                index++;
            }

        }
        mTextPaint = new Paint();
        mTextPaint.setTextSize(mDotSize * 3);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTimeY = startingY - spaceConstant;
        mTimeX = mScreenWidth / 2;
        mTextPaint.setColor(Color.BLACK);
    }

    private void endGame() {
        mGameOver = true;
        mStopwatch.stop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int actionIndex = event.getActionIndex();
        int action = event.getActionMasked();
        int x = (int) event.getX(actionIndex);
        int y = (int) event.getY(actionIndex);
        if (mGameOn && !mGameOver && (action == MotionEvent.ACTION_DOWN
                || action == MotionEvent.ACTION_POINTER_DOWN)) {
            if (x >= (mActiveDots[mDotIndex][2] - mDotPressBuffer) &&
                    x <= (mActiveDots[mDotIndex][2] + mDotPressBuffer) &&
                    y >= (mActiveDots[mDotIndex][3] - mDotPressBuffer) &&
                    y <= (mActiveDots[mDotIndex][3] + mDotPressBuffer))
                pressedDot();
        }
        return true;
    }

    private void pressedDot() {
        performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        mActiveDots[mDotIndex][0] = 2;
        mActiveDots[mDotIndex][4] = (int) (SystemClock.elapsedRealtime() - mStopwatch.getBase());
        if (mDotIndex < mActiveDots.length - 1) {
            mDotIndex++;
            mActiveDots[mDotIndex][0] = 1;
        } else {
            endGame();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            canvas.drawText(mTimeCount, mTimeX, mTimeY, mTextPaint);
            if (mGameOn) {
                for (int[] activeDot : mActiveDots) {
                    getDotSprite(activeDot[0],
                            activeDot[2],
                            activeDot[3]).draw(canvas);
                }
            } else {
                for (int[] activeDot : mActiveDots) {
                    getDotSprite(0,
                            activeDot[2],
                            activeDot[3]).draw(canvas);
                }
            }
        }
    }

    public void update() {
        if (mGameOn) {
            if (!mGameOver)
                mTimeCount = Double.toString((double)
                        (SystemClock.elapsedRealtime() - mStopwatch.getBase()) / 1000.0);
            else
                mTimeCount = Double.toString(mActiveDots[mDotIndex][4] / 1000.0);
        } else {
            mTimeCount = mCountdown;
        }
    }

    private Drawable getDotSprite(int color, int x, int y) {
        Drawable dot;
        switch (color) {
            case 0:
                dot = mGreyDot;
                break;
            case 1:
                dot = mBlueDot;
                break;
            case 2:
                dot = mGreenDot;
                break;
            default:
                dot = null;
                break;
        }
        Objects.requireNonNull(dot).setBounds(x - mDotSize,
                y - mDotSize,
                x + mDotSize,
                y + mDotSize);
        return dot;
    }

    public void startTimer() {
        mStopwatch.setBase(SystemClock.elapsedRealtime());
        mStopwatch.start();
    }

    public void setCountdown(int countdown) {
        mCountdown = String.valueOf(countdown);
    }

    public void setGameOn(boolean gameOn) {
        mGameOn = gameOn;
    }
}