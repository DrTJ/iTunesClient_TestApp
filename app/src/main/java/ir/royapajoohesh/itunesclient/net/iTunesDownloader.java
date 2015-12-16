package ir.royapajoohesh.itunesclient.net;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import ir.royapajoohesh.itunesclient.R;
import ir.royapajoohesh.itunesclient.data.AppCategories;
import ir.royapajoohesh.itunesclient.data.AppCategoriesDataSource;
import ir.royapajoohesh.itunesclient.data.AppIcons;
import ir.royapajoohesh.itunesclient.data.AppIconsDataSource;
import ir.royapajoohesh.itunesclient.data.Apps;
import ir.royapajoohesh.itunesclient.data.AppsDataSource;
import ir.royapajoohesh.utils.net.DownloadEventsListener;

public class iTunesDownloader /* extends AsyncTask<String, String, Boolean> */{
	private static String TAG = "iTunesDownloader";
	private Context context;
	private String rawData;
	private String dataURL;
	private ArrayList<AppCategories> categoriesData;

	private static String IncreaseValue = "IncreaseValue";
	private static String SetValue = "SetValue";
	private static String SetMax = "SetMax";
	private static String SetError = "SetError";

	private boolean res;

	// events
	public DownloadEventsListener<ArrayList<AppCategories>, String> onDownloadProgressEvents;
	private static boolean isRunning;

	public iTunesDownloader(Context context) {	//, LoadingFragment dialogRef
		this.dataURL = PreferenceManager.getDefaultSharedPreferences(context).getString("serviceBaseURL", "");
		this.context = context;
		this.rawData = "";
		iTunesDownloader.isRunning = false;
	}

	public static String downloadAppIcon(Context context, long appID, String iconURL, int iconHeight, boolean preventIfExists) {

		// generate local path
		String fileName = appID + "_" + iconHeight + "_" + iconURL.substring(iconURL.lastIndexOf("/") + 1);
		File dir = new File(context.getFilesDir().getPath() + "/images");
		if (!dir.exists()) {
			dir.mkdirs();
		} else if (!dir.isDirectory() && dir.canWrite()) {
			dir.delete();
			dir.mkdirs();
		}

		File imageFile = new File(dir, fileName);
		String localPath = imageFile.getPath();

		if(preventIfExists) {
			if(imageFile.exists())
				return localPath;
		}

		// download it
		AppIcons.DownloadAndSaveImage(context, iconURL, dir.getPath(), fileName);

		return localPath;
	}

	public static void InsertToDatabase(final ArrayList<AppCategories> downloadedList, final Context context,
			final OperationListener operationEvents) {
		AsyncTask<String, String, String> insertTask = new AsyncTask<String, String, String>() {

			@Override
			protected String doInBackground(String... params) {

				ArrayList<Apps> appsList = new ArrayList<Apps>();
				if (isRunning == false)
					return context.getString(R.string.aborted_by_user);

				// delete all categories
				AppCategoriesDataSource.DeleteAll(context);

				// add categories
				for (AppCategories item : downloadedList) {
					AppCategoriesDataSource.AddIfNotExists(context, item.id, item.term, item.scheme, item.label);
					appsList.addAll(item.AppsList);
				}

				if (isRunning == false)
					return context.getString(R.string.aborted_by_user);

				// delete all Apps and their Icons
				AppsDataSource.DeleteAll(context);
				AppIconsDataSource.DeleteAll(context);

				// add Apps
				for (Apps appItem : appsList) {
					// add App Icons first!
					for (AppIcons iconItem : appItem.IconsList) {
						AppIconsDataSource.Insert(context, iconItem);
					}

					// then add the updated app
					AppsDataSource.Insert(context, appItem);
				}

				return "";
			}

			@Override
			protected void onPostExecute(String result) {
				operationEvents.onSucceed();
			}

		};

		insertTask.execute();
	}

	public void StartDownload() {
		isRunning = true;

		categoriesData = new ArrayList<AppCategories>();

		RequestQueue queue = Volley.newRequestQueue(context);
		res = true;

		Log.d("URL", this.dataURL);

		onDownloadProgressEvents.onIncreaseProgressValue(context.getString(R.string.download_started), 0);

		StringRequest request = new StringRequest(Request.Method.GET, this.dataURL, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				rawData = response;
				Log.d(TAG, "JSON data downloaded!");
				AsyncTask<String, String, String> parseTask = new AsyncTask<String, String, String>() {
					@Override
					protected void onProgressUpdate(String... values) {
						if (values[0] == IncreaseValue) {
							int val = Integer.parseInt(values[2]);
							onDownloadProgressEvents.onIncreaseProgressValue(values[1], val);
							return;
						}

						if (values[0] == SetValue) {
							int currentValue = Integer.parseInt(values[2]);
							onDownloadProgressEvents.onSetProgressValue(values[1], currentValue);
							return;
						}

						if (values[0] == SetMax) {
							int maxValue = Integer.parseInt(values[1]);
							onDownloadProgressEvents.onInitProgress(context.getString(R.string.preparing_to_update_message), maxValue);
							return;
						}

						if (values[0] == SetError) {
							onDownloadProgressEvents.onError(values[1]);
							return;
						}
						// except, just set the title
						onDownloadProgressEvents.onIncreaseProgressValue(values[0], 0);
					}

					@Override
					protected String doInBackground(String... params) {
						if (isRunning == false)
							return context.getString(R.string.aborted_by_user);

						// parse data
						JSONArray entriesArray;
						try {
							entriesArray = new JSONObject(rawData).getJSONObject("feed").getJSONArray("entry");

							int maxValue = entriesArray.length();
							int currentValue = (int) (maxValue * 0.1);

							publishProgress(SetMax, maxValue + currentValue + "");
							publishProgress(SetValue, context.getString(R.string.data_downloaded), currentValue + "");

							for (int i = 0; i < entriesArray.length(); i++) {
								if (isRunning == false)
									return context.getString(R.string.aborted_by_user);

								JSONObject entryItem;
								try {
									// parsing Apps and its Icons
									entryItem = entriesArray.getJSONObject(i);
									Apps tmpApp = Apps.FromJSon(entryItem, context);

									publishProgress(IncreaseValue, context.getString(R.string.parsing_item) + tmpApp.title, "1");

									// downloading icons
									for (AppIcons newIcon : tmpApp.IconsList) {
										if (isRunning == false)
											return context.getString(R.string.aborted_by_user);

										publishProgress(String.format(context.getString(R.string.downloading_item_image), tmpApp.title, newIcon.ImagePath));

										newIcon.ImagePath = downloadAppIcon(context, tmpApp.id, newIcon.ImagePath,
												newIcon.Height, true);
									}

									if (isRunning == false)
										return context.getString(R.string.aborted_by_user);

									// parse the category details
									AppCategories tmpCat = AppCategories.FromJSon(entryItem);
									AppCategories catItem = null;

									// update progress
									publishProgress(String.format(context.getString(R.string.adding_item_category), tmpCat.label));

									// find the category in the list, if exists
									for (AppCategories cat : categoriesData) {
										if (cat.id == tmpCat.id) {
											catItem = cat;
											break;
										}
									}

									if (isRunning == false)
										return context.getString(R.string.aborted_by_user);

									// if not exist, set to new one and add it
									if (catItem == null) {
										catItem = tmpCat;
										catItem.AppsList.add(tmpApp);
										categoriesData.add(catItem);
									} else {
										catItem.AppsList.add(tmpApp);
									}
								} catch (JSONException e) {
									e.printStackTrace();
									rawData = "ItemError:" + e.getMessage();
									publishProgress(SetError, rawData);

									res = false;
								}
							}
						} catch (JSONException e1) {
							e1.printStackTrace();
							rawData = "Error:" + e1.getMessage();
							publishProgress(SetError, rawData);

							res = false;
						}

						return null;
					}

					@Override
					protected void onPostExecute(String result) {
						if (result == context.getString(R.string.aborted_by_user))
							onDownloadProgressEvents.onAbort();
						else
							onDownloadProgressEvents.onSucceed(categoriesData, !res);
					}
				};

				parseTask.execute();
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				rawData = "Error:" + error.getMessage();
				onDownloadProgressEvents.onError(rawData);
			}
		});

		Log.d(TAG, "Started!");
		request.setTag(TAG);
		queue.add(request);
		queue.start();
	}

	public void Stop() {
		iTunesDownloader.isRunning = false;
		
		AppIcons.isRunning = false;
	}
}
