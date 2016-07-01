/*********************************************************************************
 *  DocScan is a Android app for document scanning.
 *
 *  Author:         Fabian Hollaus, Florian Kleber, Markus Diem
 *  Organization:   TU Wien, Computer Vision Lab
 *  Date created:   16. June 2016
 *
 *  This file is part of DocScan.
 *
 *  DocScan is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Foobar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *********************************************************************************/

package at.ac.tuwien.caa.docscan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import at.ac.tuwien.caa.docscan.cv.Patch;

/**
 * Based on Lunar Lander example:
 * https://github.com/Miserlou/Android-SDK-Samples/blob/master/LunarLander
 */

public class DrawView extends SurfaceView implements SurfaceHolder.Callback {

    class DrawerThread extends Thread {

        private SurfaceHolder mSurfaceHolder;
        private boolean mIsRunning;
        private int mCanvasWidth, mCanvasHeight;

        private Paint mRectPaint;
        private Paint mTextPaint;
        private Patch[] mFocusPatches;

        public DrawerThread(SurfaceHolder surfaceHolder) {


            mSurfaceHolder = surfaceHolder;

//            Initialize drawing stuff:
            mRectPaint = new Paint();

            mTextPaint = new Paint();

            final float testTextSize = 48f;
            mTextPaint.setTextSize(testTextSize);

        }

        private void setFocusPatches(Patch[] focusPatches) {

            mFocusPatches = focusPatches;

        }

        @Override
        public void run() {
            while (mIsRunning) {
                Canvas canvas = null;
                try {
                    canvas = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        draw(canvas);
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (canvas != null) {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }


        /* Callback invoked when the surface dimensions change. */
        public void setSurfaceSize(int width, int height) {
            // synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {
                mCanvasWidth = width;
                mCanvasHeight = height;

            }
        }

        public void setRunning(boolean b) {

            mIsRunning = b;

        }

        private void draw(Canvas canvas) {

            if (canvas == null) {
                return;
            }

            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

//            canvas.drawRect(0, 0, 100, 100, mRectPaint);

            if (mFocusPatches == null)
                return;

            Patch patch;

            for (int i = 0; i < mFocusPatches.length; i++) {

                patch = mFocusPatches[i];
                String fValue = String.format("%.2f", patch.getFM());
                canvas.drawText(fValue, patch.getPX(), patch.getPY()+ 50, mTextPaint);

            }


//            canvas.save();
        }

    }


//    private SurfaceHolder mSurfaceHolder;
    private DrawerThread mDrawerThread;

    public DrawView(Context context, AttributeSet attrs) {

        super(context, attrs);

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);



    }

    public void setFocusPatches(Patch[] focusPatches) {

        mDrawerThread.setFocusPatches(focusPatches);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {

        mDrawerThread = new DrawerThread(holder);
        mDrawerThread.setRunning(true);
        // TODO: check why the thread is already started - if the app is restarted. The thread should be dead!
        if (mDrawerThread.getState() == Thread.State.NEW)
            mDrawerThread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mDrawerThread.setSurfaceSize(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        mDrawerThread.setRunning(false);
        while (retry) {
            try {
                mDrawerThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }

    }



//    @Override
//    public void onWindowFocusChanged(boolean hasWindowFocus) {
//        if (!hasWindowFocus) mDrawerThread.setRunning(false);
//    }


}