package ir.royapajoohesh.itunesclient;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ir.royapajoohesh.itunesclient.adapters.AppCategoriesAdapter;
import ir.royapajoohesh.itunesclient.adapters.ViewType;
import ir.royapajoohesh.itunesclient.data.AppCategories;
import ir.royapajoohesh.utils.ActivityUtils;

public class MainActivity extends ActivityBase {
	protected static final String TAG = "MainActivity";
	String WebServiceBaseURL;
	Boolean AutoUpdate;
	Boolean IsFirstRun;
	public static ArrayList<AppCategories> CategoriesList;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		super.ShowBackButton = false;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				if (key == "serviceBaseURL") {
					WebServiceBaseURL = sharedPreferences.getString("serviceBaseURL", "");
				} else if (key == "AutoDownloadList") {
					AutoUpdate = sharedPreferences.getBoolean("AutoDownloadList", true);
				}
			}
		});

		this.WebServiceBaseURL = prefs.getString("serviceBaseURL", "");
		this.AutoUpdate = prefs.getBoolean("AutoDownloadList", true);
		this.IsFirstRun = prefs.getBoolean("IsFirstRun", true);

		if(this.WebServiceBaseURL == ""){
			prefs.edit().putString("serviceBaseURL", "https://itunes.apple.com/us/rss/topfreeapplications/limit=50/json").apply();
			this.WebServiceBaseURL = prefs.getString("serviceBaseURL", "");
		}

		Log.d(TAG, this.WebServiceBaseURL);

		prefs.edit()
			.putBoolean("IsFirstRun", false)

			// add categories icons English
			.putInt("Books", R.mipmap.book_128)
			.putInt("Business", R.mipmap.business01_128)
			.putInt("Catalogs", R.mipmap.brochure_600)
			.putInt("Education", R.mipmap.education_128)
			.putInt("Entertainment", R.mipmap.entertainment_128)
			.putInt("Finance", R.mipmap.finance_128)
			.putInt("Food & Drink", R.mipmap.chef_128)
			.putInt("Games", R.mipmap.games_128)
			.putInt("Health & Fitness", R.mipmap.health_512)
			.putInt("Lifestyle", R.mipmap.lifestyle_128)
			.putInt("Medical", R.mipmap.medical_512)
			.putInt("Music", R.mipmap.guitar_128)
			.putInt("Navigation", R.mipmap.navigation_128)
			.putInt("News", R.mipmap.news_128)
			.putInt("Magazines & Newspapers", R.mipmap.magazines_128)
			.putInt("Photo & Video", R.mipmap.picture_128)
			.putInt("Productivity", R.mipmap.productivity_128)
			.putInt("Reference", R.mipmap.quote_128)
			.putInt("Social Networking", R.mipmap.share_128)
			.putInt("Sports", R.mipmap.sports_128)
			.putInt("Travel", R.mipmap.suitcase_128)
			.putInt("Weather", R.mipmap.weather_128)
			.putInt("Utilities", R.mipmap.utility_128)
			.putInt("Shopping", R.mipmap.shop_128)

			// add categories icons Spanish
			.putInt("Libros", R.mipmap.book_128)
			.putInt("Economía y empresa", R.mipmap.business01_128)
			.putInt("Catálogos", R.mipmap.brochure_600)
			.putInt("Educación", R.mipmap.education_128)
			.putInt("Entretenimiento", R.mipmap.entertainment_128)
			.putInt("Finanzas", R.mipmap.finance_128)
			.putInt("Comida y bebidas", R.mipmap.chef_128)
			.putInt("Juegos", R.mipmap.games_128)
			.putInt("Salud y forma física", R.mipmap.health_512)
			.putInt("Estilo de vida", R.mipmap.lifestyle_128)
			.putInt("Medicina", R.mipmap.medical_512)
			.putInt("Música", R.mipmap.guitar_128)
			.putInt("Navegación", R.mipmap.navigation_128)
			.putInt("Noticias", R.mipmap.news_128)
			//.putInt("Magazines & Newspapers", R.mipmap.magazines_128)	// ??????????????????????
			.putInt("Foto y vídeo", R.mipmap.picture_128)
			.putInt("Productividad", R.mipmap.productivity_128)
			.putInt("Referencia", R.mipmap.quote_128)
			.putInt("Redes sociales", R.mipmap.share_128)
			.putInt("Deportes", R.mipmap.sports_128)
			.putInt("Viajes", R.mipmap.suitcase_128)
			.putInt("Tiempo", R.mipmap.weather_128)
			.putInt("Utilidades", R.mipmap.utility_128)
			.putInt("Compras", R.mipmap.shop_128)

			.apply();

		if (this.AutoUpdate || this.IsFirstRun) {
			super.UpdateAppsList(new onRefreshEventListener() {
				@Override
				public void RefreshTheList() {
					RefreshList();
				}
			});
		} else {
			RefreshList();
		}
	}

	// Refreshes the list of apps in the relevant view
	@Override
	public void RefreshList() {
		super.RefreshList();

		if (ActivityUtils.isTablet(getBaseContext())) {
			// show in the grid
			GridView itemsContainer = (GridView) findViewById(R.id.categoriesGridView);
			itemsContainer.setAdapter(new AppCategoriesAdapter(MainActivity.this, CategoriesList, ViewType.Grid));
			itemsContainer.setOnItemClickListener(listItemClicked);
		} else {
			// show in the list
			ListView itemsContainer = (ListView) findViewById(R.id.categoriesListView);
			itemsContainer.setAdapter(new AppCategoriesAdapter(MainActivity.this, CategoriesList, ViewType.List));
			itemsContainer.setOnItemClickListener(listItemClicked);
		}
	}

	OnItemClickListener listItemClicked = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.d("AppCategory", "Item " + id + " clicked!");
            AppCategories catData = CategoriesList.get(position);

			Intent appsListIntent = new Intent(MainActivity.this, AppsListActivity.class);
			appsListIntent.putExtra("catID", id);
			appsListIntent.putExtra("catTitle", catData.label);
            appsListIntent.putExtra("itemPosition", position);
			startActivity(appsListIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
	};
}
