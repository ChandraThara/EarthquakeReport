/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static android.view.View.GONE;


public class EarthquakeActivity extends AppCompatActivity implements LoaderCallbacks<List<EarthQuake>> {
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";
    private static final int EARTHQUAKE_LOADER_ID = 1;
    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private EarthQuakeAdapter mAdapter;
    private TextView mEmptyTextView;
    private ProgressBar mProgressView;
    private boolean isConnected;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        //checking internet connectivity
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        //get empty text view
        mEmptyTextView = (TextView) findViewById(R.id.emptyView);
        earthquakeListView.setEmptyView(mEmptyTextView);
        //get progress loading view
        mProgressView = (ProgressBar) findViewById(R.id.loading_spinner);
        // Create a new {@link ArrayAdapter} of earthquakes
        mAdapter = new EarthQuakeAdapter(
                this, new ArrayList<EarthQuake>());
        //initialise loader only if there is internet connection
        if (activeNetwork != null && activeNetwork.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            //initialize loader
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this).forceLoad();
        }else{
            mProgressView.setVisibility(GONE);
            mEmptyTextView.setText(R.string.no_internet);
        }



        earthquakeListView.setAdapter(mAdapter);
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                EarthQuake currentEarthQuake = mAdapter.getItem(position);
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentEarthQuake.getURL());
                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);
                startActivity(websiteIntent);
            }
        });
    }

    @Override
    public Loader<List<EarthQuake>> onCreateLoader(int id, Bundle args) {
        //create new loader if not existing already
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );
        Date currentDate = new Date();
        //get current date
        String endtimeVal = dateFormat.format(currentDate);
        //get start date = current date - 30 days
        Date startDate = new Date(currentDate.getTime() - 30 * 24 * 3600 * 1000l ); //Subtract 30 days
        String starttimeVal = dateFormat.format(startDate);

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);
        uriBuilder.appendQueryParameter("starttime", starttimeVal);
        uriBuilder.appendQueryParameter("endtime", endtimeVal);

        return new EarthquakeLoader(this, uriBuilder.toString());

    }

    @Override
    public void onLoadFinished(android.content.Loader<List<EarthQuake>> loader, List<EarthQuake> earthquakesData) {
        mProgressView.setVisibility(GONE);
        mEmptyTextView.setText(R.string.no_earthquakes);
        // Clear the adapter of previous earthquake data
        mAdapter.clear();
        if (earthquakesData != null && !earthquakesData.isEmpty()) {
            //add all earthquake data to the adapter
            mAdapter.addAll(earthquakesData);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<List<EarthQuake>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
