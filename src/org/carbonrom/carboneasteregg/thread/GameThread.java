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

package org.carbonrom.carboneasteregg.thread;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import org.carbonrom.carboneasteregg.view.SingleGameView;

public class GameThread extends Thread {

    private final SurfaceHolder mSurfaceHolder;
    private final SingleGameView mSingleGameView;
    private boolean mIsRunning;

    public GameThread(SurfaceHolder surfaceHolder, SingleGameView singleGameView) {
        super();
        mSurfaceHolder = surfaceHolder;
        mSingleGameView = singleGameView;
    }

    @Override
    public void run() {
        while (mIsRunning) {
            Canvas canvas = this.mSurfaceHolder.lockCanvas();
            if (canvas != null) {
                synchronized (mSurfaceHolder) {
                    assert this.mSingleGameView != null;
                    this.mSingleGameView.update();
                    this.mSingleGameView.draw(canvas);
                }
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setRunning(boolean isRunning) {
        mIsRunning = isRunning;
    }
}