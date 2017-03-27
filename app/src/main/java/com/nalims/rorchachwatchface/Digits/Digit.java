package com.nalims.rorchachwatchface.Digits;

import android.content.Context;
import com.nalims.rorchachwatchface.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by FX on 24/09/15.
 */
public abstract class Digit implements DigitInterface{

    protected float space;
    protected float paddingTop;
    protected boolean subtility = false;

    public Digit(Context context){
        space = context.getResources().getDimension(R.dimen.space);
        paddingTop = context.getResources().getDimension(R.dimen.paddingTop);
    }

    protected List<Float> getHoursMirrorPoints(List<Float> points, float centerX){
        List<Float> returnPoints = new ArrayList<>(points.size());

        int i = 0;
        while (i < points.size()){
            returnPoints.add(centerX - (points.get(i) - centerX));
            i++;
            returnPoints.add(points.get(i));
            i++;
            returnPoints.add(centerX - (points.get(i) - centerX));
            i++;
            returnPoints.add(points.get(i));
            i++;
        }

        return returnPoints;
    }

    protected List<Float> getMinutesMirrorPoints(List<Float> points, float centerX){
        List<Float> returnPoints = new ArrayList<>(points.size());

        int i = 0;
        while (i < points.size()){
            returnPoints.add(centerX + (centerX - points.get(i)));
            i++;
            returnPoints.add(points.get(i));
            i++;
            returnPoints.add(centerX + (centerX - points.get(i)));
            i++;
            returnPoints.add(points.get(i));
            i++;
        }

        return returnPoints;
    }

    protected float[] convertFromList(List<Float> points){
        float[] returnPoints = new float[points.size()];
        for (int i = 0; i < points.size(); i++){
            returnPoints[i] = points.get(i);
        }
        return returnPoints;
    }

    public void setSubtility(boolean subtility) {
        this.subtility = subtility;
    }
}
