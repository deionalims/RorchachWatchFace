package com.nalims.rorchachwatchface;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Device {

    private final Context context;
    private final float centerX;
    private final float centerY;
    private Paint datePaint;
    private Paint secondsPaint;
    private Bitmap hourBitmap;
    private Canvas hourCanvas;


    public Device(Context context, float centerX, float centerY) {
        this.context = context;
        this.centerX = centerX;
        this.centerY = centerY;
        datePaint = new Paint();
        datePaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
        datePaint.setAntiAlias(true);
        datePaint.setTextSize(24);
        datePaint.setColor(Color.BLACK);
        hourBitmap = Bitmap.createBitmap((int) centerX * 2, (int) centerY * 2, Bitmap.Config.ARGB_8888);
        hourCanvas = new Canvas(hourBitmap);
        secondsPaint = new Paint();
        secondsPaint.setStyle(Paint.Style.STROKE);
        secondsPaint.setAntiAlias(true);
        secondsPaint.setStrokeWidth(16);
    }

    public void onDraw(Canvas canvas, Rect bounds, Calendar calendar, boolean isInAmbientMode, int leftHourResId, int rightHourResId, int leftMinuteResId, int rightMinuteResId) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        hourCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.drawColor(isInAmbientMode ? Color.BLACK : Color.WHITE);

        Resources resources = context.getResources();

        Drawable upLeftDigit = resources.getDrawable(leftHourResId, null);
        upLeftDigit.setBounds(bounds);

        Drawable upRightDigit = resources.getDrawable(rightHourResId, null);
        upRightDigit.setBounds(bounds);

        Drawable downLeftDigit = resources.getDrawable(leftMinuteResId, null);
        downLeftDigit.setBounds(bounds);

        Drawable downRightDigit = resources.getDrawable(rightMinuteResId, null);
        downRightDigit.setBounds(bounds);

        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{upLeftDigit, upRightDigit, downLeftDigit, downRightDigit});
        if (isInAmbientMode){
            DrawableCompat.setTint(layerDrawable, ContextCompat.getColor(context, R.color.white));
        } else {
            DrawableCompat.setTint(layerDrawable, ContextCompat.getColor(context, R.color.black));
        }
        layerDrawable.draw(hourCanvas);

        hourCanvas.save();
        hourCanvas.scale(-1, 1, centerX, centerY);
        layerDrawable.draw(hourCanvas);
        hourCanvas.restore();

        if (!isInAmbientMode){
            int seconds = calendar.get(Calendar.SECOND);
            drawDate(canvas, calendar.getTime());
            datePaint.setColor(Color.WHITE);
            datePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            drawDate(hourCanvas, calendar.getTime());
            datePaint.setXfermode(null);
            datePaint.setColor(Color.BLACK);

            secondsPaint.setColor(Color.BLACK);
            canvas.drawArc(0, 0, canvas.getWidth(), canvas.getHeight(), -90, 6 * seconds, false, secondsPaint);

            secondsPaint.setColor(Color.WHITE);
            secondsPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            hourCanvas.drawArc(0, 0, canvas.getWidth(), canvas.getHeight(), -90, 6 * seconds, false, secondsPaint);
            secondsPaint.setXfermode(null);

        }

        canvas.drawBitmap(hourBitmap, 0, 0, new Paint());
    }

    public void drawDate(Canvas canvas, Date date) {

        String dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(date);

        Rect bounds = new Rect();
        datePaint.getTextBounds(dateFormat, 0, dateFormat.length(), bounds);
        int x = (canvas.getWidth() / 2) - (bounds.width() / 2);
        int y = canvas.getHeight() - bounds.height();
        canvas.drawText(dateFormat, x, y, datePaint);
    }
}
