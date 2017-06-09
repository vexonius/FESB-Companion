package com.tstudioz.fax.fme.weather;


import com.tstudioz.fax.fme.R;


public class Forecast {
    private Current mCurrent;


    public Current getCurrent() {
        return mCurrent;
    }

    public void setCurrent(Current current) {
        mCurrent = current;
    }


    public static int getIconId(String iconString) {
        // clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day, or partly-cloudy-night
        int iconId;

        switch (iconString) {
            case "clear-day":
                iconId = R.drawable.sun;
                break;
            case "clear-night":
                iconId = R.drawable.moon;
                break;
            case "rain":
                iconId = R.drawable.rain;
                break;
            case "snow":
                iconId = R.drawable.clouds_with_snow;
                break;
            case "sleet":
                iconId = R.drawable.rain;
                break;
            case "wind":
                iconId = R.drawable.windy;
                break;
            case "fog":
                iconId = R.drawable.sun_and_fog;
                break;
            case "cloudy":
                iconId = R.drawable.clouds;
                break;
            case "partly-cloudy-day":
                iconId = R.drawable.semi_cloudly;
                break;
            case "partly-cloudy-night":
                iconId = R.drawable.moon_with_clouds;
                break;
            default:
                iconId = R.drawable.sun;
                break;
        }

        return iconId;
    }
}
