package ir.royapajoohesh.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/* Created by DrTJ @ April 2015 */

public class ActivityUtils {
	private static final String TAG = "ActivityUtils";

	public static void SetOrientation(Activity context, Orientations tabletOrientation, Orientations phoneOrientation) {
		Orientations orientation = (isTablet(context)) ? tabletOrientation : phoneOrientation;
		int Orientation = orientation == Orientations.Portrait
				? ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
				: ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;

		context.setRequestedOrientation(Orientation);
	}

	public static boolean isTablet(Context context) {
		int screenLayout = context.getResources().getConfiguration().screenLayout;
		return (screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	public static Orientations CurrentOrientation(Activity context) {
		int tmpCurrentOrientation = context.getResources().getConfiguration().orientation;
		Log.d(TAG, tmpCurrentOrientation + "");
		Orientations result = Orientations.Unspecified;

		if (tmpCurrentOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ||
				tmpCurrentOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE ||
				tmpCurrentOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
			result = Orientations.Landscape;
		} else if (tmpCurrentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ||
				tmpCurrentOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT ||
				tmpCurrentOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT) {
			result = Orientations.Portrait;
		}

		return result;
	}

	public static void UnsetOrientation(Activity context) {
		context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	}
	
	public static int getPixels(float dps, Context context){
		float scale = context.getResources().getDisplayMetrics().density;
		int pixels = (int) (dps * scale + 0.5f);
		
		return pixels;
	}


	public static Boolean isOnWifiNet(Context context) {
		boolean isWifi = false;
		
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connManager.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			isWifi = (netInfo.getType() == ConnectivityManager.TYPE_WIFI);
			Log.d("IsOnWifiNet", isWifi + "");
		}

		return isWifi;
	}

	public static Boolean isNetworkAvailable(Context context) {
		boolean isConnected = false;
		
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connManager.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			isConnected = true;
		}
		
		Log.d("isNetworkAvailable", isConnected + "");
		return isConnected;
	}
}
