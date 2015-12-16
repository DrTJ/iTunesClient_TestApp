package ir.royapajoohesh.itunesclient;

import ir.royapajoohesh.itunesclient.adapters.AppsAdapter;
import ir.royapajoohesh.itunesclient.data.AppCategories;
import ir.royapajoohesh.itunesclient.data.AppCategoriesDataSource;
import ir.royapajoohesh.itunesclient.data.AppIconsDataSource;
import ir.royapajoohesh.itunesclient.data.Apps;
import ir.royapajoohesh.itunesclient.data.AppsDataSource;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AppDetailsActivity extends ActivityBase {

	public static final String TAG = "AppDetailsActivity";
	
	private TextView AppTitleTextView = null;
	private ImageView appIconImageView = null;
	private TextView AppProducerTextView = null;
	private TextView AppCategoryTitleTextView = null;
	private TextView AppSummeryTextView = null;

	private LinearLayout similarAppsLinearLayout = null;
	private Button moreSimilarAppsButton = null;

	private LinearLayout developerAppsLinearLayout = null;
	private Button moreDeveloperAppsButton = null;

	private Apps currentAppItem = null;
    private TextView producerNameTextView = null;
    private TextView producerWebsiteTextView = null;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_details);

		this.AppTitleTextView = (TextView) findViewById(R.id.AppTitleTextView);
		this.appIconImageView = (ImageView) findViewById(R.id.appIconImageView);
		this.AppProducerTextView = (TextView) findViewById(R.id.AppProducerTextView);
		this.AppCategoryTitleTextView = (TextView) findViewById(R.id.categoryNameTextView);
		this.AppSummeryTextView = (TextView) findViewById(R.id.AppSummeryTextView);
		this.similarAppsLinearLayout = (LinearLayout)findViewById(R.id.similarAppsLinearLayout);
		this.moreSimilarAppsButton = (Button) findViewById(R.id.moreSimilarAppsButton);
		this.moreSimilarAppsButton.setOnClickListener(moreSimilarAppsButton_Clicked);

		this.developerAppsLinearLayout = (LinearLayout)findViewById(R.id.developerAppsLinearLayout);
		this.moreDeveloperAppsButton = (Button) findViewById(R.id.moreDeveloperAppsButton);
		this.moreDeveloperAppsButton.setOnClickListener(moreDeveloperAppsButton_Clicked);

        this.producerNameTextView =  (TextView) findViewById(R.id.producerNameTextView);
        this.producerWebsiteTextView =  (TextView) findViewById(R.id.producerWebsiteTextView);
        this.producerWebsiteTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (producerWebsiteTextView.getTag() == null)
                    return;

                String webAddress = producerWebsiteTextView.getTag().toString();
                if (webAddress == "")
                    return;

                if (!webAddress.startsWith("http://") && !webAddress.startsWith("https://"))
                    webAddress = "http://" + webAddress;

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webAddress)));
            }
        });

		long appID = this.getIntent().getExtras().getLong("appID");
		LoadAppDetails(appID, this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			onBackPressed();
			return true;
		} else
			return super.onOptionsItemSelected(item);
	}

	OnClickListener moreSimilarAppsButton_Clicked = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent appsListIntent = new Intent(AppDetailsActivity.this, AppsListActivity.class);
			appsListIntent.putExtra("catID", currentAppItem.categoryID);
			startActivity(appsListIntent);
			finish();
		}
	};

	OnClickListener moreDeveloperAppsButton_Clicked = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent appsListIntent = new Intent(AppDetailsActivity.this, AppsListActivity.class);
			appsListIntent.putExtra("artistName", currentAppItem.artistName);
			startActivity(appsListIntent);
			finish();
		}
	};

	private void LoadAppDetails(long appID, Context context) {
		AppsDataSource dsApps = new AppsDataSource(context);
		dsApps.Open();
		ArrayList<Apps> apps = dsApps.Select(context, Apps.Column_id + " = " + appID, Apps.Column_id, true, 1);
		dsApps.Close();

		if (apps.size() == 0) {
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}

		currentAppItem = apps.get(0);
		AppCategories cat = AppCategoriesDataSource.GetCategoryByID(currentAppItem.categoryID, context);

		// set values
		this.AppTitleTextView.setText(currentAppItem.title);
		this.AppProducerTextView.setText(currentAppItem.artistName);
		this.AppCategoryTitleTextView.setText(cat.label);

		if(currentAppItem.summary.trim() == "")
			this.AppSummeryTextView.setVisibility(View.GONE);
		else
			this.AppSummeryTextView.setText(currentAppItem.summary);


        this.producerNameTextView.setText(currentAppItem.artistName);
        this.producerWebsiteTextView.setTag(currentAppItem.artistLink);

		Bitmap appIconBitmap = AppIconsDataSource.GetLargestIcon(currentAppItem.id, context);
		this.appIconImageView.setImageBitmap(appIconBitmap);

		// set similar apps list
		ArrayList<Apps> similarAppsList = AppsDataSource.GetByCategoryID(currentAppItem.categoryID, currentAppItem.id, context, 10);

		LayoutInflater inflater = getLayoutInflater();

		if (similarAppsList.size() == 0) {
			findViewById(R.id.moreSimilarAppsTitleLayout).setVisibility(View.GONE);
			findViewById(R.id.moreSimilarAppsListLayout).setVisibility(View.GONE);
		} else {
			for (Apps appItem : similarAppsList) {
				View newItem = getNewThumbnailAppItem(appItem, context, inflater, this.similarAppsLinearLayout);
				this.similarAppsLinearLayout.addView(newItem);
			}
		}

		// set producer other apps list
		ArrayList<Apps> developerOtherAppsList  = AppsDataSource.GetByProducer(currentAppItem.artistName, currentAppItem.id, context, 10);

		if (developerOtherAppsList.size() == 0) {
			findViewById(R.id.moreDeveloperAppsTitleLayout).setVisibility(View.GONE);
			findViewById(R.id.moreDeveloperAppsListLayout).setVisibility(View.GONE);
		} else {
			for (Apps appItem : developerOtherAppsList) {
				View newItem = getNewThumbnailAppItem(appItem, context, inflater, this.developerAppsLinearLayout);
				this.developerAppsLinearLayout.addView(newItem);
			}
		}
	}

	private View getNewThumbnailAppItem(Apps appItem, Context context, LayoutInflater inflater, ViewGroup root) {
		// create new item
		View newItem = inflater.inflate(R.layout.apps_grid_layout, root, false);

		AppsAdapter.SetViewData(newItem, appItem, true, context);

		LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) newItem.getLayoutParams();
		lp.rightMargin = 15;
		newItem.setLayoutParams(lp);

		newItem.setTag(appItem);
		newItem.setOnClickListener(appItemClicked);

		return newItem;
	}

	private OnClickListener appItemClicked = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getTag() == null) {
				Log.d(TAG, "NULL TAG!");
				return;
			}

			Apps item = (Apps) v.getTag();

			Log.d(TAG, "Item " + item.id + " clicked!");

			Intent appDetailsIntent = new Intent(AppDetailsActivity.this, AppDetailsActivity.class);
			appDetailsIntent.putExtra("appID", item.id);
			startActivity(appDetailsIntent);
			finish();

			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		}
	};
}
