package ir.royapajoohesh.itunesclient;

import ir.royapajoohesh.itunesclient.data.AppCategories;
import ir.royapajoohesh.itunesclient.net.NetworkingOptions;
import ir.royapajoohesh.itunesclient.net.OperationListener;
import ir.royapajoohesh.itunesclient.net.iTunesDownloader;
import ir.royapajoohesh.utils.ActivityUtils;
import ir.royapajoohesh.utils.net.DownloadEventsListener;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LoadingFragment extends DialogFragment {
	protected static final String TAG = "LoadingFragment";
	public iTunesDownloader itDownloader;
	private Context context;
	View popupView;
	ProgressBar downloadProgressBar;
	TextView progressTitleTextView;

	OperationListener onOperationFinishedListener;

	private AlertDialog loadingDialog;

	public LoadingFragment(final Context context) {
		this.context = context;
	}

	public void StartUpdate() {
		// this.onCreateDialog(getArguments());
		this.itDownloader = new iTunesDownloader(context);
		this.itDownloader.onDownloadProgressEvents = new DownloadEventsListener<ArrayList<AppCategories>, String>() {
			String errorMessage = "";

			@Override
			public void onInitProgress(String title, int maxValue) {
				if (popupView == null) {
					return;
				}

				if (downloadProgressBar != null) {
					downloadProgressBar.setMax(maxValue);
					downloadProgressBar.setProgress(0);
				} else
					Log.d(TAG, "downloadProgressBar == null");

				if (progressTitleTextView != null) {
					progressTitleTextView.setText(title);
				} else
					Log.d(TAG, "progressTitleTextView == null");
			}

			@Override
			public void onSetProgressValue(String title, int val) {
				// if (loadingDialog == null)
				// return;
				if (popupView == null) {
					Log.d(TAG, "popupView == null");
					return;
				}

				if (downloadProgressBar != null) {
					downloadProgressBar.setProgress(val);
				} else
					Log.d(TAG, "downloadProgressBar == null");

				if (progressTitleTextView != null) {
					progressTitleTextView.setText(title);
				} else
					Log.d(TAG, "progressTitleTextView == null");
			}

			@Override
			public void onIncreaseProgressValue(String title, int val) {
				// if (loadingDialog == null)
				// return;
				if (popupView == null) {
					Log.d(TAG, "popupView == null");
					return;
				}

				if (downloadProgressBar != null) {
					int currentVal = downloadProgressBar.getProgress();
					currentVal += val;
					downloadProgressBar.setProgress(currentVal);
				} else
					Log.d(TAG, "downloadProgressBar == null");

				if (progressTitleTextView != null) {
					progressTitleTextView.setText(title);
				} else
					Log.d(TAG, "progressTitleTextView == null");
			}

			@Override
			public void onError(String data) {
				errorMessage = data;
			}

			@Override
			public void onSucceed(ArrayList<AppCategories> data, boolean hasError) {
				if (hasError) {
					if (errorMessage.length() > "Error: ".length())
						Log.d(TAG, "Download finished with some errors!\n" + errorMessage.substring("Error: ".length()));

					if (onOperationFinishedListener != null)
						onOperationFinishedListener.onError(errorMessage);
					else
						Log.d(TAG, "onOperationFinishedListener == null");

				} else {
					Log.d(TAG, "Download finished!");
					Log.d(TAG, "InsertToDatabase();");

					onIncreaseProgressValue(context.getString(R.string.saving_processed_data), 0);

					iTunesDownloader.InsertToDatabase(data, context, new OperationListener() {
						@Override
						public void onSucceed() {
							Log.d(TAG, "Totally done!");
							onOperationFinishedListener.onSucceed();
						}

						@Override
						public void onError(String data) {
							Log.d(TAG, "Totally done with errors while inserting data to DB!");
							onOperationFinishedListener.onError(data);
						}
					});
				}
			}

			@Override
			public void onAbort() {
				dismiss();
				Toast.makeText(context, context.getResources().getString(R.string.update_aborted_message),
						Toast.LENGTH_LONG).show();
			}

		};

		// this.itDownloader.execute(updateParameters);
		this.itDownloader.StartDownload();

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this.context);

		LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		popupView = inflater.inflate(R.layout.activity_loading_layout, null);
		downloadProgressBar = (ProgressBar) popupView.findViewById(R.id.downloadProgressBar);
		progressTitleTextView = (TextView) popupView.findViewById(R.id.currentProgressTitleTextView);
		
		builder.setView(popupView);
		builder.setTitle(context.getResources().getString(R.string.download_dialog_title));
		builder.setIcon(R.mipmap.download_128);
		builder.setNegativeButton(context.getString(R.string.cancel_update_button), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				StopUpdating();
			}
		});

		this.loadingDialog = builder.create();
		return this.loadingDialog;
	}

	public void StopUpdating() {
		this.itDownloader.Stop();
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		this.context = context;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public void onDestroyView() {
		popupView = null;
		downloadProgressBar = null;
		progressTitleTextView = null;

		super.onDestroyView();
	}

	public NetworkingOptions CheckInternetConnection() {
		Boolean downloadOnlyOnWIFI = SettingsActivity.getBoolean(SettingsActivity.downloadOnlyOnWIFI, false , context);
		Boolean isOnWifiNet = ActivityUtils.isOnWifiNet(this.context);
		Boolean isConnected = ActivityUtils.isNetworkAvailable(this.context);

		if (!isConnected)
			return NetworkingOptions.NoConnection;

		if (downloadOnlyOnWIFI && !isOnWifiNet)
			return NetworkingOptions.JustWiFiNetSupported;

		return NetworkingOptions.Available;
	}
}