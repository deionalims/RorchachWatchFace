package com.nalims.rorchachwatchface.Digits;

/**
 * Created by FX on 24/09/15.
 */
public interface DigitInterface {

    float[] getLeftMinute(float centerX, float centerY, float strokeWidth);
    float[] getRightMinute(float centerX, float centerY, float strokeWidth);
    float[] getLeftHour(float centerX, float centerY, float strokeWidth);
    float[] getRightHour(float centerX, float centerY, float strokeWidth);

}
