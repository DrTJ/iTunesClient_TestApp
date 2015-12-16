package ir.royapajoohesh.itunesclient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import ir.royapajoohesh.utils.ActivityUtils;
import ir.royapajoohesh.utils.Orientations;

public class SplashScreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash_screen);

		ActivityUtils.SetOrientation(this, Orientations.Landscape, Orientations.Landscape);


		TextView appNameTextView = (TextView) findViewById(R.id.appNameTextView);
		TextView subTitle1TextView = (TextView) findViewById(R.id.subTitle1TextView);
		TextView subTitle2TextView = (TextView) findViewById(R.id.subTitle2TextView);

		// set fonts
		Typeface font = Typeface.createFromAsset(getAssets(), "fonts/tusj.ttf");
		appNameTextView.setTypeface(font);

		font = Typeface.createFromAsset(getAssets(), "fonts/CaviarDreams.ttf");
		subTitle1TextView.setTypeface(font);
		subTitle2TextView.setTypeface(font);


// animate the view
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_anim);
		animation.reset();
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
				finish();
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});

		findViewById(R.id.contentRelativeLayout).startAnimation(animation);

	}
}
