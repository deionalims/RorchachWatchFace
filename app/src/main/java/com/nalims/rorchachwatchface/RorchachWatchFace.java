/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nalims.rorchachwatchface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.SurfaceHolder;
import android.view.WindowInsets;
import android.widget.Toast;
import com.nalims.rorchachwatchface.Digits.Digit;
import com.nalims.rorchachwatchface.Digits.Five;
import com.nalims.rorchachwatchface.Digits.Four;
import com.nalims.rorchachwatchface.Digits.Height;
import com.nalims.rorchachwatchface.Digits.Nine;
import com.nalims.rorchachwatchface.Digits.One;
import com.nalims.rorchachwatchface.Digits.Seven;
import com.nalims.rorchachwatchface.Digits.Six;
import com.nalims.rorchachwatchface.Digits.Three;
import com.nalims.rorchachwatchface.Digits.Two;
import com.nalims.rorchachwatchface.Digits.Zero;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Digital watch face with seconds. In ambient mode, the seconds aren't displayed. On devices with
 * low-bit ambient mode, the text is drawn without anti-aliasing in ambient mode.
 */
public class RorchachWatchFace extends CanvasWatchFaceService {
    private static final Typeface NORMAL_TYPEFACE = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
    private static final Typeface BOLD_TYPEFACE = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);

    /**
     * Update rate in milliseconds for interactive mode. We update once a second since seconds are
     * displayed in interactive mode.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<RorchachWatchFace.Engine> mWeakReference;

        public EngineHandler(RorchachWatchFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            RorchachWatchFace.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine {
        final Handler mUpdateTimeHandler = new EngineHandler(this);
        boolean mRegisteredTimeZoneReceiver = false;
        Paint mBackgroundPaint;
        Paint mTextPaint;
        Paint mDatePaint;

        boolean mAmbient;
        Calendar mCalendar;
        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }
        };
        float mXOffset;
        float mYOffset;

        float mStrokeWidth;
        float mSpace;
        float mCenterX;
        float mCenterY;

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(RorchachWatchFace.this).setCardPeekMode(
                WatchFaceStyle.PEEK_MODE_VARIABLE)
                .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                .setShowSystemUiTime(false)
                .setAcceptsTapEvents(true)
                .build());
            Resources resources = RorchachWatchFace.this.getResources();
            mYOffset = resources.getDimension(R.dimen.digital_y_offset);

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(resources.getColor(R.color.background));

            mTextPaint = createTextPaint(resources.getColor(R.color.digital_text));

            mDatePaint = createDatePaint();

            mSpace = resources.getDimension(R.dimen.space);

            mCalendar = Calendar.getInstance();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mCenterX = width / 2;
            mCenterY = height / 2;

            mStrokeWidth = ((mCenterX / 2) - 4 * mSpace) / 2;

            mTextPaint.setStrokeWidth(mStrokeWidth);

            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        private Paint createTextPaint(int textColor) {
            Paint paint = new Paint();
            paint.setColor(textColor);
            paint.setTypeface(NORMAL_TYPEFACE);
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.ROUND);

            return paint;
        }

        private Paint createDatePaint(){
            Paint paint = new Paint();
            paint.setTypeface(BOLD_TYPEFACE);
            paint.setAntiAlias(true);
            paint.setTextSize(24);
            return paint;
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            } else {
                unregisterReceiver();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            RorchachWatchFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            RorchachWatchFace.this.unregisterReceiver(mTimeZoneReceiver);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);

            // Load resources that have alternate values for round watches.
            Resources resources = RorchachWatchFace.this.getResources();
            boolean isRound = insets.isRound();
            mXOffset = resources.getDimension(isRound ? R.dimen.digital_x_offset_round : R.dimen.digital_x_offset);
            float textSize = resources.getDimension(isRound ? R.dimen.digital_text_size_round : R.dimen.digital_text_size);

            mTextPaint.setTextSize(textSize);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;
                if (mLowBitAmbient) {
                    mTextPaint.setAntiAlias(!inAmbientMode);
                }
                invalidate();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        /**
         * Captures tap event (and tap type) and toggles the background color if the user finishes
         * a tap.
         */
        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            switch (tapType) {
                case TAP_TYPE_TOUCH:
                    // The user has started touching the screen.
                    break;
                case TAP_TYPE_TOUCH_CANCEL:
                    // The user has started a different gesture or otherwise cancelled the tap.
                    break;
                case TAP_TYPE_TAP:
                    // The user has completed the tap gesture.
                    // TODO: Add code to handle the tap gesture.
                    Toast.makeText(getApplicationContext(), R.string.message, Toast.LENGTH_SHORT).show();
                    break;
            }
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {


            // Draw H:MM in ambient mode or H:MM:SS in interactive mode.
            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);

            Digit hourLeft;
            Digit hourRight;
            Digit minuteLeft;
            Digit minuteRight;

            int hour = 13; mCalendar.get(Calendar.HOUR_OF_DAY);
            int minute  = 37; mCalendar.get(Calendar.MINUTE);

            if (hour < 10){
                hourLeft = new Zero(getApplicationContext());
            } else if (hour < 20){
                hourLeft = new One(getApplicationContext());
            } else {
                hourLeft = new Two(getApplicationContext());
            }

            int hourRightUnit = hour % 10;

            switch (hourRightUnit){
                case 0:
                    hourRight = new Zero(getApplicationContext());
                    break;
                case 1:
                    hourRight = new One(getApplicationContext());
                    break;
                case 2:
                    hourRight = new Two(getApplicationContext());
                    break;
                case 3:
                    hourRight = new Three(getApplicationContext());
                    break;
                case 4:
                    hourRight = new Four(getApplicationContext());
                    break;
                case 5:
                    hourRight = new Five(getApplicationContext());
                    break;
                case 6:
                    hourRight = new Six(getApplicationContext());
                    break;
                case 7:
                    hourRight = new Seven(getApplicationContext());
                    break;
                case 8:
                    hourRight = new Height(getApplicationContext());
                    break;
                case 9:
                default:
                    hourRight = new Nine(getApplicationContext());
                    break;
            }

            boolean shouldTweakFive = false;
            int minuteRightUnit = minute % 10;
            switch (minuteRightUnit){
                case 0:
                    minuteRight = new Zero(getApplicationContext());
                    break;
                case 1:
                    minuteRight = new One(getApplicationContext());
                    shouldTweakFive = true;
                    break;
                case 2:
                    minuteRight = new Two(getApplicationContext());
                    break;
                case 3:
                    minuteRight = new Three(getApplicationContext());
                    break;
                case 4:
                    minuteRight = new Four(getApplicationContext());
                    break;
                case 5:
                    minuteRight = new Five(getApplicationContext());
                    break;
                case 6:
                    minuteRight = new Six(getApplicationContext());
                    break;
                case 7:
                    minuteRight = new Seven(getApplicationContext());
                    break;
                case 8:
                    minuteRight = new Height(getApplicationContext());
                    break;
                case 9:
                default:
                    minuteRight = new Nine(getApplicationContext());
                    break;
            }

            if (minute < 10){
                minuteLeft = new Zero(getApplicationContext());
            } else if (minute < 20){
                minuteLeft = new One(getApplicationContext());
            } else if (minute < 30){
                minuteLeft = new Two(getApplicationContext());
            } else if (minute < 40){
                minuteLeft = new Three(getApplicationContext());
            } else if (minute < 50){
                minuteLeft = new Four(getApplicationContext());
            } else {
                minuteLeft = new Five(getApplicationContext());
                ((Five) minuteLeft).setArrangeWithOne(shouldTweakFive);
            }

            if ((minuteLeft instanceof Three) && (minuteRight instanceof Zero)){
                minuteLeft.setSubtility(true);
            } else {
                minuteLeft.setSubtility(false);
            }

            Bitmap hourBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(),
                Bitmap.Config.ARGB_8888);
            Canvas hourCanvas = new Canvas(hourBitmap);

            // Draw the background.
            if (isInAmbientMode()) {
                canvas.drawColor(Color.BLACK);
                mTextPaint.setColor(Color.WHITE);
                mDatePaint.setColor(Color.WHITE);
            } else {
                mTextPaint.setColor(Color.BLACK);
                mDatePaint.setColor(Color.BLACK);
                canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
            }

            drawDate(canvas);


            hourCanvas.drawLines(hourLeft.getLeftHour(mCenterX, mCenterY, mStrokeWidth), mTextPaint);
            hourCanvas.drawLines(hourRight.getRightHour(mCenterX, mCenterY, mStrokeWidth), mTextPaint);
            hourCanvas.drawLines(minuteLeft.getLeftMinute(mCenterX, mCenterY, mStrokeWidth), mTextPaint);
            hourCanvas.drawLines(minuteRight.getRightMinute(mCenterX, mCenterY, mStrokeWidth), mTextPaint);
            if (isInAmbientMode()){
                mDatePaint.setColor(Color.BLACK);
            } else {
                mDatePaint.setColor(Color.WHITE);
            }
            mDatePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            drawDate(hourCanvas);

            canvas.drawBitmap(hourBitmap, 0, 0, new Paint());
            mDatePaint.setXfermode(null);
        }

        private void drawDate(Canvas canvas){

            String date = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(mCalendar.getTime());

            Rect bounds = new Rect();
            mDatePaint.getTextBounds(date, 0, date.length(), bounds);
            int x = (canvas.getWidth() / 2) - (bounds.width() / 2);
            int y = bounds.height() + 4;
            canvas.drawText(date, x, y, mDatePaint);
        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }
    }
}
