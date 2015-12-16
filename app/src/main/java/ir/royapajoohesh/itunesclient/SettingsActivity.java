package ir.royapajoohesh.itunesclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

import ir.royapajoohesh.utils.ActivityUtils;
import ir.royapajoohesh.utils.Orientations;

public class SettingsActivity extends PreferenceActivity {
    public static String updateOnChangingLanguage = "updateOnChangingLanguage";
    public static String downloadOnlyOnWIFI = "DownloadOnlyOnWIFI";

    public static boolean getBoolean(String key, boolean defaultValue, Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(key, defaultValue);
    }

    public static String getString(String key, String defaultValue, Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(key, defaultValue);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.app_prefs);
        ActivityUtils.SetOrientation(this, Orientations.Landscape, Orientations.Portrait);
    }

    /*
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean res = super.onPreferenceTreeClick(preferenceScreen, preference);

        String key = preference.getKey();
        Log.d("preference", key);

        if (key.equals("updateMechanism") || key.equals("updateAppsCount")) {
            String appsCount = getString("updateAppsCount", "0", this);
            String mechanism = getString("updateMechanism", getResources().getString(R.string.UpdateMechanism_TopFreeiPadApplications), this);

            Log.d("updateAppsCount", appsCount);
            Log.d("updateMechanism", mechanism);
        }

        return res;
    }
    */


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
