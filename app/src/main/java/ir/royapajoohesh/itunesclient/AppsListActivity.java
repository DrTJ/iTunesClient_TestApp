package ir.royapajoohesh.itunesclient;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ir.royapajoohesh.itunesclient.adapters.AppCategoriesAdapter;
import ir.royapajoohesh.itunesclient.adapters.AppsAdapter;
import ir.royapajoohesh.itunesclient.adapters.ViewType;
import ir.royapajoohesh.itunesclient.data.AppCategories;
import ir.royapajoohesh.itunesclient.data.AppCategoriesDataSource;
import ir.royapajoohesh.itunesclient.data.Apps;
import ir.royapajoohesh.itunesclient.data.AppsDataSource;
import ir.royapajoohesh.utils.ActivityUtils;

public class AppsListActivity extends ActivityBase {
    public static final String TAG = "AppsListActivity";
    public AppsAdapter adapter;
    public String WindowTitle;
    private ListView leftPanelListView;
    private DrawerLayout drawer;
    private LinearLayout leftPanelContainer;
    private AppCategoriesAdapter drawerAdapter;
    private boolean isSearching;
    private ActionBarDrawerToggle drawerListener;

    private OnItemClickListener appItemClicked = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "Item " + id + " clicked!");

            Intent appDetailsIntent = new Intent(AppsListActivity.this, AppDetailsActivity.class);
            appDetailsIntent.putExtra("appID", id);
            startActivity(appDetailsIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    };

    private OnItemClickListener leftPanelItemClicked = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // set page title
            AppCategories cat = AppCategoriesDataSource.findCategoryByID(id, MainActivity.CategoriesList);

            WindowTitle = cat.label;

            LoadAppsByCategory(id, AppsListActivity.this);

            drawerAdapter.setSelectedByID(id);
            drawerAdapter.notifyDataSetChanged();

            drawer.closeDrawer(leftPanelContainer);
        }
    };

    // used to store search query asked by the user in order to refresh the list in case of pressing update option
    private String searchQuery;

    // used to store category ID asked by the user in order to refresh the list in case of pressing update option
    private long catID;
    private String artistName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            setContentView(R.layout.activity_search_result);

            searchQuery = intent.getStringExtra(SearchManager.QUERY);
            doSearch(searchQuery);

            WindowTitle = getResources().getString(R.string.search_result_window_title);
            this.isSearching = true;
        } else {
            this.isSearching = false;
            setContentView(R.layout.activity_apps_list);

            // setting up the drawer and the toolbar
            drawer = (DrawerLayout) findViewById(R.id.navigationDrawerLayout);
            leftPanelContainer = (LinearLayout) findViewById(R.id.navigation_drawer_apps_list);
            leftPanelListView = (ListView) leftPanelContainer.findViewById(R.id.leftPanelListView);

            if (this.toolbar == null)
                this.toolbar = (Toolbar) this.findViewById(R.id.pageToolbar).findViewById(R.id.toolbarWidget);

            drawerListener = new ActionBarDrawerToggle(this, drawer, this.toolbar, R.string.app_list_drawer_title, R.string.app_list_drawer_title) {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    super.onDrawerSlide(drawerView, slideOffset);
                }

                @Override
                public void onDrawerOpened(View arg0) {
                    WindowTitle = getTitle().toString();
                    getSupportActionBar().setTitle(getResources().getString(R.string.app_list_drawer_title));
                }

                @Override
                public void onDrawerClosed(View arg0) {
                    setTitle(WindowTitle);
                    getSupportActionBar().setTitle(WindowTitle);
                }
            };

            drawer.setDrawerListener(drawerListener);
            leftPanelListView.setOnItemClickListener(leftPanelItemClicked);


            // Load apps by category ID
            Bundle extras = this.getIntent().getExtras();
            if (extras.containsKey("catID")) {
                // Load apps list by category ID
                catID = extras.getLong("catID");
                int itemPosition = extras.getInt("itemPosition");

                if (extras.containsKey("catTitle")) {
                    WindowTitle = extras.getString("catTitle");
                } else {
                    // find it in the list
                    AppCategories cat = MainActivity.CategoriesList.get(itemPosition);
                    if (cat != null) WindowTitle = cat.label;
                    else WindowTitle = getTitle().toString();
                }

                LoadSideBarCategories(catID, this, MainActivity.CategoriesList);
                LoadAppsByCategory(catID, this);
            } else {
                // load apps list by developer name
                artistName = extras.getString("artistName");
                WindowTitle = String.format(getString(R.string.apps_by_developer_window_title), artistName);
                catID = -1;

                LoadSideBarCategories(-1, this, MainActivity.CategoriesList);
                LoadAppsByDeveloper(artistName, this);
            }
        }

        setTitle(WindowTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (this.isSearching == false) toolbarShadow.setVisibility(View.GONE);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (!isSearching) drawerListener.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerListener.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean res = super.onCreateOptionsMenu(menu);
        if (isSearching) menu.removeItem(R.id.action_search);
        return res;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!isSearching) {
            if (drawerListener.onOptionsItemSelected(item)) return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void LoadSideBarCategories(long currentCategroyID, Context context, ArrayList<AppCategories> catsList) {
        this.drawerAdapter = new AppCategoriesAdapter(AppsListActivity.this, catsList, ViewType.Drawer);
        if (currentCategroyID != -1) drawerAdapter.setSelectedByID(currentCategroyID);
        leftPanelListView.setAdapter(this.drawerAdapter);
    }


    public void LoadAppsByCategory(long catID, Context context) {
        ArrayList<Apps> list = AppsDataSource.GetByCategoryID(catID, context, 0);
        SetAppsList(list);
    }

    private void LoadAppsByDeveloper(String artistName, Context context) {
        ArrayList<Apps> list = AppsDataSource.GetByProducer(artistName, -1, context, 0);
        SetAppsList(list);
    }

    private void SetAppsList(ArrayList<Apps> list) {
        boolean isTablet = ActivityUtils.isTablet(this);
        adapter = new AppsAdapter(AppsListActivity.this, list, isTablet);

        if (isTablet) {
            // show items in the grid
            GridView gv = (GridView) findViewById(R.id.appsGridView);
            gv.setAdapter(adapter);
            gv.setOnItemClickListener(appItemClicked);
        } else {
            // show items in the list
            ListView lv = (ListView) findViewById(R.id.appsListView);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(appItemClicked);
        }
    }


    private void doSearch(String query) {
        String whereClause = "";
        String[] queryByWords = query.toLowerCase().split("\\s+");
        for (String word : queryByWords) {
            whereClause += String.format("or (lower(%1$s) like '%%%2$s%%')", Apps.Column_name, word);
        }

        whereClause = whereClause.substring("or ".length());
        Log.d(TAG, "whereClause = '" + whereClause + "'");

        AppsDataSource dsApps = new AppsDataSource(this);
        dsApps.Open();
        ArrayList<Apps> list = dsApps.Select(this, whereClause, Apps.Column_name, true, 0);
        dsApps.Close();

        TextView recordsCountTextView = (TextView) findViewById(R.id.recordsCountTextView);
        recordsCountTextView.setText(String.format(getResources().getString(R.string.result_description), list.size(), query));

        SetAppsList(list);
    }


    // Refreshes the list of apps in the relevant view
    @Override
    public void RefreshList() {
        super.RefreshList();

        if (this.isSearching) {
            doSearch(searchQuery);
        } else {
            if (catID != -1) {
                // Load apps list by category ID
                // find it in the list
                AppCategories cat = AppCategoriesDataSource.findCategoryByID(catID, MainActivity.CategoriesList);
                if (cat != null) WindowTitle = cat.label;
                else WindowTitle = getTitle().toString();

                LoadSideBarCategories(catID, this, MainActivity.CategoriesList);
                LoadAppsByCategory(catID, this);
            } else {
                // load apps list by developer name
                WindowTitle = String.format(getString(R.string.apps_by_developer_window_title), artistName);
                LoadSideBarCategories(-1, this, MainActivity.CategoriesList);
                LoadAppsByDeveloper(artistName, this);
            }
        }
    }

}
