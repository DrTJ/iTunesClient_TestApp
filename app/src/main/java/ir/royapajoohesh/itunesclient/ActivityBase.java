package ir.royapajoohesh.itunesclient;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import ir.royapajoohesh.itunesclient.data.AppCategoriesDataSource;
import ir.royapajoohesh.itunesclient.net.NetworkingOptions;
import ir.royapajoohesh.itunesclient.net.OperationListener;
import ir.royapajoohesh.utils.ActivityUtils;
import ir.royapajoohesh.utils.LanguageManager;
import ir.royapajoohesh.utils.Languages;
import ir.royapajoohesh.utils.Orientations;

public class ActivityBase extends AppCompatActivity {
    private static final String TAG = "ActivityBase";
    public boolean ShowBackButton = true;
    public View toolbarShadow;
    Toolbar toolbar;
    LoadingFragment loadingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            loadingFragment = (LoadingFragment) getSupportFragmentManager().findFragmentByTag("LoadingFragment");
        }

        // Check the orientation
        ActivityUtils.SetOrientation(this, Orientations.Landscape, Orientations.Portrait);
    }

    @Override
    protected void onResume() {
        super.onResume();

        CreateToolbar();
    }

    public void CreateToolbar() {
        this.toolbar = (Toolbar) this.findViewById(R.id.pageToolbar).findViewById(R.id.toolbarWidget);
        if (toolbar == null) {
            return;
        }

        this.toolbarShadow = this.findViewById(R.id.pageToolbar).findViewById(R.id.toolbar_shadow);

        setSupportActionBar(toolbar);

        ActionBar tmpActionBar = getSupportActionBar();

        if (ShowBackButton) {
            tmpActionBar.setHomeButtonEnabled(true);
            tmpActionBar.setDisplayHomeAsUpEnabled(true);
        }

        tmpActionBar.setDisplayUseLogoEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.updateList) {
            UpdateMenuItem_Click();
            return true;
        } else if (id == R.id.EnglishLanguage) {
            LanguageManager.ChangeTo(Languages.English, this, true);
            pref.edit().putString("Language", Languages.English).commit();
            UpdateMenuItem_Click();
            return true;
        } else if (id == R.id.SpanishLanguage) {
            LanguageManager.ChangeTo(Languages.Spanish, this, true);
            pref.edit().putString("Language", Languages.Spanish).commit();
            UpdateMenuItem_Click();
            return true;
        } else if (id == R.id.PersianLanguage) {
            LanguageManager.ChangeTo(Languages.Persian, this, true);
            pref.edit().putString("Language", Languages.Persian).commit();

            UpdateMenuItem_Click();
            return true;
        } else if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return true;
        } else if (id == R.id.action_search) {
            onSearchRequested();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void UpdateMenuItem_Click() {
        UpdateAppsList(new onRefreshEventListener() {
            @Override
            public void RefreshTheList() {
                RefreshList();
            }
        });
    }

    public void UpdateAppsList(final onRefreshEventListener onRefresh) {
        loadingFragment = new LoadingFragment(this);

        loadingFragment.onOperationFinishedListener = new OperationListener() {
            @Override
            public void onSucceed() {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.update_finished_successfuly), Toast.LENGTH_LONG).show();
                Log.d(TAG, "onSucceed!");

                onRefresh.RefreshTheList();
                loadingFragment.dismiss();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                Log.d("Downloading Data", message);
                Log.d(TAG, "onError!");

                onRefresh.RefreshTheList();
                loadingFragment.dismiss();
            }
        };

        // Test Internet connection and type before trying to download
        NetworkingOptions netCheckResult = loadingFragment.CheckInternetConnection();

        if (netCheckResult == NetworkingOptions.Available) {
            loadingFragment.show(getSupportFragmentManager(), "LoadingFragment");
            loadingFragment.StartUpdate();
            Log.d(TAG, "LoginFragment StartUpdate has been Called!");
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(ActivityBase.this).setPositiveButton(getString(R.string.ok), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            if (netCheckResult == NetworkingOptions.JustWiFiNetSupported) {
                alert.setMessage(R.string.only_wifi_net_allowed_description);
                alert.setTitle(R.string.only_wifi_net_allowed_title);
                alert.setNeutralButton(R.string.change_settings, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(ActivityBase.this, SettingsActivity.class));
                    }
                });
                alert.show();
            } else if (netCheckResult == NetworkingOptions.NoConnection) {
                alert.setMessage(R.string.no_connection_error_description);
                alert.setTitle(R.string.no_connection_error_title);
                alert.show();
            }
        }
    }


    // Refreshes the list of categories and applications
    public void RefreshList() {
        AppCategoriesDataSource dsCategories = new AppCategoriesDataSource(this);
        dsCategories.Open();
        MainActivity.CategoriesList = dsCategories.SelectAll();
        dsCategories.Close();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // animation for going back from the current activity
        // if the user is pressing the BackButton in MainActivity, just close the app simply
        if (this.getClass().getName() != MainActivity.class.getName()) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

}
