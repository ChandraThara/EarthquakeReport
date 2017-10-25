package com.example.android.quakereport;

import java.util.List;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by thara on 10/20/17.
 */

public class EarthquakeLoader extends AsyncTaskLoader<List<EarthQuake>> {
    private static final String LOG_TAG = EarthquakeLoader.class.getName();
    /** Query URL */
    private String mUrl;

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    public EarthquakeLoader(Context context, String url){
        super(context);
        mUrl = url;
    }
    @Override
    public List<EarthQuake> loadInBackground() {
        if(mUrl == null){
            return null;
        }

        List<EarthQuake> earthQuakeList = QueryUtils.fetchEarthQuakeData(mUrl);
        return earthQuakeList;
    }

}
