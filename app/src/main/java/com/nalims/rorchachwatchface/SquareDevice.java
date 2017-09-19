package com.nalims.rorchachwatchface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
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
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SquareDevice {

    private final Context context;
    private final Paint textPaint;
    private final Paint datePaint;
    private final Paint secondsPaint;
    private final float secondsPadding;
    private final float centerX;
    private final float centerY;
    private final float strokeWidth;

    public SquareDevice(Context context, Paint textPaint, Paint datePaint, Paint secondsPaint, float centerX,
        float centerY, float strokeWidth) {
        this.context = context;
        this.textPaint = textPaint;
        this.datePaint = datePaint;
        this.secondsPaint = secondsPaint;
        this.centerX = centerX;
        this.centerY = centerY;
        this.strokeWidth = strokeWidth;
        secondsPadding = context.getResources().getDimension(R.dimen.paddingTop);
    }

    public void onDraw(Canvas canvas, Rect bounds, boolean isInAmbientMode, int hour, int minute, int seconds) {
        Digit hourLeft;
        Digit hourRight;
        Digit minuteLeft;
        Digit minuteRight;

        if (hour < 10) {
            hourLeft = new Zero(context);
        } else if (hour < 20) {
            hourLeft = new One(context);
        } else {
            hourLeft = new Two(context);
        }

        int hourRightUnit = hour % 10;

        switch (hourRightUnit) {
            case 0:
                hourRight = new Zero(context);
                break;
            case 1:
                hourRight = new One(context);
                break;
            case 2:
                hourRight = new Two(context);
                break;
            case 3:
                hourRight = new Three(context);
                break;
            case 4:
                hourRight = new Four(context);
                break;
            case 5:
                hourRight = new Five(context);
                break;
            case 6:
                hourRight = new Six(context);
                break;
            case 7:
                hourRight = new Seven(context);
                break;
            case 8:
                hourRight = new Height(context);
                break;
            case 9:
            default:
                hourRight = new Nine(context);
                break;
        }

        boolean shouldTweakFive = false;
        int minuteRightUnit = minute % 10;
        switch (minuteRightUnit) {
            case 0:
                minuteRight = new Zero(context);
                break;
            case 1:
                minuteRight = new One(context);
                shouldTweakFive = true;
                break;
            case 2:
                minuteRight = new Two(context);
                break;
            case 3:
                minuteRight = new Three(context);
                break;
            case 4:
                minuteRight = new Four(context);
                break;
            case 5:
                minuteRight = new Five(context);
                break;
            case 6:
                minuteRight = new Six(context);
                break;
            case 7:
                minuteRight = new Seven(context);
                break;
            case 8:
                minuteRight = new Height(context);
                break;
            case 9:
            default:
                minuteRight = new Nine(context);
                break;
        }

        if (minute < 10) {
            minuteLeft = new Zero(context);
        } else if (minute < 20) {
            minuteLeft = new One(context);
        } else if (minute < 30) {
            minuteLeft = new Two(context);
        } else if (minute < 40) {
            minuteLeft = new Three(context);
        } else if (minute < 50) {
            minuteLeft = new Four(context);
        } else {
            minuteLeft = new Five(context);
            ((Five) minuteLeft).setArrangeWithOne(shouldTweakFive);
        }

        if ((minuteLeft instanceof Three) && (minuteRight instanceof Zero)) {
            minuteLeft.setSubtility(true);
        } else {
            minuteLeft.setSubtility(false);
        }

        Bitmap hourBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas hourCanvas = new Canvas(hourBitmap);
        Bitmap secondsBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas secondsCanvas = new Canvas(secondsBitmap);

        // Draw the background.
        if (isInAmbientMode) {
            canvas.drawColor(Color.BLACK);
            textPaint.setColor(Color.WHITE);
            datePaint.setColor(Color.WHITE);
            secondsPaint.setColor(Color.WHITE);
        } else {
            canvas.drawColor(Color.WHITE);
            textPaint.setColor(Color.BLACK);
            datePaint.setColor(Color.BLACK);
            secondsPaint.setColor(Color.BLACK);
        }

        drawDate(canvas);

        if (!isInAmbientMode) {
            secondsPaint.setColor(Color.BLACK);
            //secondsPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
            secondsCanvas.drawArc(secondsPadding, secondsPadding, canvas.getWidth() - secondsPadding,
                canvas.getHeight() - secondsPadding, -90, 6 * seconds, false, secondsPaint);

            secondsPaint.setColor(Color.WHITE);
            secondsPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
            hourCanvas.drawArc(0, 0, canvas.getWidth(), canvas.getHeight(), -90, 6 * seconds, false, secondsPaint);
            secondsPaint.setXfermode(null);

            datePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            drawDate(secondsCanvas);
            datePaint.setXfermode(null);
            canvas.drawBitmap(secondsBitmap, 0, 0, new Paint());
        }

        hourCanvas.drawLines(hourLeft.getLeftHour(centerX, centerY, strokeWidth), textPaint);
        hourCanvas.drawLines(hourRight.getRightHour(centerX, centerY, strokeWidth), textPaint);
        hourCanvas.drawLines(minuteLeft.getLeftMinute(centerX, centerY, strokeWidth), textPaint);
        hourCanvas.drawLines(minuteRight.getRightMinute(centerX, centerY, strokeWidth), textPaint);

        if (!isInAmbientMode) {
            secondsPaint.setColor(Color.WHITE);
            secondsPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
            hourCanvas.drawArc(secondsPadding, secondsPadding, canvas.getWidth() - secondsPadding,
                canvas.getHeight() - secondsPadding, -90, 6 * seconds, false, secondsPaint);
            secondsPaint.setXfermode(null);
        }

        if (isInAmbientMode) {
            datePaint.setColor(Color.BLACK);
        } else {
            datePaint.setColor(Color.WHITE);
        }
        datePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        drawDate(hourCanvas);
        datePaint.setXfermode(null);

        canvas.drawBitmap(hourBitmap, 0, 0, new Paint());
    }

    private void drawDate(Canvas canvas) {

        String date =
            DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(Calendar.getInstance().getTime());

        Rect bounds = new Rect();
        datePaint.getTextBounds(date, 0, date.length(), bounds);
        int x = (canvas.getWidth() / 2) - (bounds.width() / 2);
        int y = bounds.height();
        canvas.drawText(date, x, y, datePaint);
    }
}
