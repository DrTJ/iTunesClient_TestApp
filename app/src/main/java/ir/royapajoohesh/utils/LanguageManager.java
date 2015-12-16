package ir.royapajoohesh.utils;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

public class LanguageManager {

	public static void ChangeTo(String newLanguage, Activity baseContext, boolean refresh) {
		// set the new language
		ChangeLanguage(newLanguage, baseContext);		

		// save it to preferences
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(baseContext);
		pref.edit().putString("Language", newLanguage).commit();
		
		// refresh the page
		if(refresh){
			Orientations currentOrientation = ActivityUtils.CurrentOrientation(baseContext);
			
			Orientations tmpOrientation = currentOrientation == Orientations.Landscape ? Orientations.Portrait : Orientations.Landscape;
			
			ActivityUtils.SetOrientation(baseContext, tmpOrientation, tmpOrientation);
			//ActivityUtils.SetOrientation(baseContext, currentOrientation, currentOrientation);
			ActivityUtils.UnsetOrientation(baseContext);
		}	
	}

	
	public static void ChangeLanguage(String languageCode, Context baseContext){
	
		Locale locale = new Locale(languageCode); 
	    Locale.setDefault(locale);
	    Configuration config = new Configuration();
	    config.locale = locale;
	    baseContext.getResources().updateConfiguration(config, baseContext.getResources().getDisplayMetrics());
	    
		    /*
	    // Code 2
	    Resources res = baseContext.getResources();
	    DisplayMetrics dm = res.getDisplayMetrics();
	    android.content.res.Configuration conf = res.getConfiguration();
	    conf.locale = new Locale(languageCode);
	    res.updateConfiguration(conf, dm);*/
	}
	
}
