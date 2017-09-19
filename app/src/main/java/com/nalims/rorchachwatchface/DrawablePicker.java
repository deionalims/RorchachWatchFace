package com.nalims.rorchachwatchface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DrawablePicker {
    public static List<Integer> getDrawablesIds(Calendar calendar) {
        List<Integer> ids = new ArrayList<>();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if (hour < 10) {
            ids.add(R.drawable.zero_up_left);
        } else if (hour < 20) {
            ids.add(R.drawable.one_up_left);
        } else {
            ids.add(R.drawable.two_up_left);
        }

        int hourRightUnit = hour % 10;

        switch (hourRightUnit) {
            case 0:
                ids.add(R.drawable.zero_up_right);
                break;
            case 1:
                ids.add(R.drawable.one_up_right);
                break;
            case 2:
                ids.add(R.drawable.two_up_right);
                break;
            case 3:
                ids.add(R.drawable.three_up_right);
                break;
            case 4:
                ids.add(R.drawable.four_up_right);
                break;
            case 5:
                ids.add(R.drawable.five_up_right);
                break;
            case 6:
                ids.add(R.drawable.six_up_right);
                break;
            case 7:
                ids.add(R.drawable.seven_up_right);
                break;
            case 8:
                ids.add(R.drawable.height_up_right);
                break;
            case 9:
            default:
                ids.add(R.drawable.nine_up_right);
                break;
        }

        boolean shouldTweakBecauseOfOne = false;
        boolean shouldTweakTwo = false;
        int minuteRightUnit = minute % 10;
        switch (minuteRightUnit) {
            case 0:
                ids.add(R.drawable.zero_down_right);
                break;
            case 1:
                ids.add(R.drawable.one_down_right);
                shouldTweakBecauseOfOne = true;
                break;
            case 2:
                ids.add(R.drawable.two_down_right);
                break;
            case 3:
                ids.add(R.drawable.three_down_right);
                break;
            case 4:
                ids.add(R.drawable.four_down_right);
                break;
            case 5:
                ids.add(R.drawable.five_down_right);
                break;
            case 6:
                ids.add(R.drawable.six_down_right);
                break;
            case 7:
                shouldTweakTwo = true;
                ids.add(R.drawable.seven_down_right);
                break;
            case 8:
                ids.add(R.drawable.height_down_right);
                break;
            case 9:
            default:
                ids.add(R.drawable.nine_down_right);
                break;
        }

        if (minute < 10) {
            ids.add(R.drawable.zero_down_left);
        } else if (minute < 20) {
            ids.add(R.drawable.one_down_left);
        } else if (minute < 30) {
            ids.add(shouldTweakTwo ? R.drawable.two_down_left_short : R.drawable.two_down_left_long);
        } else if (minute < 40) {
            ids.add(R.drawable.three_down_left);
        } else if (minute < 50) {
            ids.add(R.drawable.four_down_left);
        } else {
            ids.add(shouldTweakBecauseOfOne ? R.drawable.five_down_left_long : R.drawable.five_down_left_short);
        }

        return ids;
    }
}
