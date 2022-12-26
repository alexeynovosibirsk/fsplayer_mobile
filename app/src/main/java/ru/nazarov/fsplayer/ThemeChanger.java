package ru.nazarov.fsplayer;

import android.app.Activity;
import android.content.Intent;
public class ThemeChanger {

    private static int  sTheme;
    private int i = 0;
    public final static int THEME_DEFAULT = 0;
    public final static int THEME_APERTURERAINBOW = 1;
    public final static int THEME_APERTURERAINBOWG = 2;

    /**
     * Set the theme of the Activity, and restart it by creating a new Activity of the same type.    *
     *
     */

    public static void changeToTheme(Activity activity, int theme)
    {
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }
    /** Set the theme of the activity, according to the configuration. */
    public static void onActivityCreateSetTheme(Activity activity) {

        switch (sTheme)
        {
            default:
            case THEME_DEFAULT:
                activity.setTheme(R.style.Theme_FSPlayer);
                break;
            case THEME_APERTURERAINBOW:
                activity.setTheme(R.style.Theme_ApertureRainbow);
                break;
            case THEME_APERTURERAINBOWG:
                activity.setTheme(R.style.Theme_ApertureRainbowG);
                break;
        }
    }
}