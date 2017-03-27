package com.nalims.rorchachwatchface.Digits;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by FX on 24/09/15.
 */
public class One extends Digit {

    public One(Context context) {
        super(context);
    }

    @Override
    public float[] getLeftMinute(float centerX, float centerY, float strokeWidth) {
        List<Float> points = new ArrayList<>();

        points.add(centerX / 2 - strokeWidth / 2 - space);
        points.add(centerY - (strokeWidth / 2));
        points.add(centerX / 2 - strokeWidth / 2 - space);
        points.add(2 * centerY - strokeWidth / 2 - space);

        points.addAll(getHoursMirrorPoints(points, centerX));

        return convertFromList(points);
    }

    @Override
    public float[] getRightMinute(float centerX, float centerY, float strokeWidth) {
        List<Float> points = new ArrayList<>();

        points.add(centerX - strokeWidth / 2 - space);
        points.add(centerY - 2 * space);
        points.add(centerX - strokeWidth / 2 - space);
        points.add(2 * centerY - strokeWidth / 2 - space);

        points.addAll(getHoursMirrorPoints(points, centerX));

        return convertFromList(points);
    }

    @Override
    public float[] getLeftHour(float centerX, float centerY, float strokeWidth) {
        List<Float> points = new ArrayList<>();

        points.add((centerX + (centerX / 2)) - (strokeWidth / 2) - space);
        points.add(paddingTop);
        points.add((centerX + (centerX / 2)) - (strokeWidth / 2) - space);
        points.add(centerY);

        points.addAll(getHoursMirrorPoints(points, centerX));

        return convertFromList(points);
    }

    @Override
    public float[] getRightHour(float centerX, float centerY, float strokeWidth) {
        List<Float> points = new ArrayList<>();

        points.add(2 * centerX - space - strokeWidth / 2);
        points.add(centerY - centerY / 2 - (strokeWidth / 2) +  2 * space);
        points.add(2 * centerX - space - strokeWidth / 2);
        points.add(centerY - (strokeWidth / 2) - space);

        points.addAll(getHoursMirrorPoints(points, centerX));

        return convertFromList(points);
    }
}
