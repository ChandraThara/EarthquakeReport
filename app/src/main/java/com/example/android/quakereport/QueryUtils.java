package com.example.android.quakereport;

/**
 * Created by thara on 10/13/17.
 */

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.net.URL;

import static com.example.android.quakereport.EarthquakeActivity.LOG_TAG;
import static com.example.android.quakereport.R.id.date;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");



    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }


    public static List<EarthQuake> fetchEarthQuakeData(String requestURL){
        // Create URL object
        URL url = createURL(requestURL);
        String jsonResponse = "";
        try {
            jsonResponse  = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG,"Error closing input stream",e);
        }
        List<EarthQuake> earthQuakeList = extractEarthquakes(jsonResponse);
        return earthQuakeList;
    }

    private static URL createURL(String stringURL){
        URL url = null;
        Uri uri = null;
        Date currentDate = new Date();
        //get current date
        String endtimeVal = dateFormat.format(currentDate);
        //get start date = current date - 30 days
        Date startDate = new Date(currentDate.getTime() - 30 * 24 * 3600 * 1000l ); //Subtract 30 days
        String starttimeVal = dateFormat.format(startDate);

        try {
            //append start and end times to the base URL
            uri = Uri.parse(stringURL)
                    .buildUpon()
                    .appendQueryParameter("starttime",starttimeVal)
                    .appendQueryParameter("endtime",endtimeVal)
                    .build();
            url = new URL(uri.toString());
        }
        catch(MalformedURLException e){
            Log.e(LOG_TAG,"Problem building url",e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if( url == null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.connect();
            if( urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else{
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        }catch(IOException e){
            Log.e(LOG_TAG,"Problem retrieving the earthquake results",e);
        }finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream( InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while( line != null){
                output.append(line);
                line = bufferedReader.readLine();
            }
        }

        return output.toString();
    }

    /**
         * Return a list of {@link EarthQuake} objects that has been built up from
         * parsing a JSON response.
         */
    public static List<EarthQuake> extractEarthquakes(String earthquakeJSON) {
    // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding earthquakes to
        List<EarthQuake> earthquakes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            JSONObject earthQuakeStr = new JSONObject(earthquakeJSON);
            JSONArray features = earthQuakeStr.getJSONArray("features");
            for( int i = 0; i < features.length(); i++) {
                JSONObject featureEle = features.getJSONObject(i);
                JSONObject propertiesObj = featureEle.getJSONObject("properties");
                double magnitude = propertiesObj.getDouble("mag");
                String location = propertiesObj.getString("place");
                long timeinMilliSecs = propertiesObj.getLong("time");
                String URL = propertiesObj.getString("url");
                EarthQuake earthquake = new EarthQuake(magnitude, location, timeinMilliSecs, URL);
                earthquakes.add(earthquake);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }

}
