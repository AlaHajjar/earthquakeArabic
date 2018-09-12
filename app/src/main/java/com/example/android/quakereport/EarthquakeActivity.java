package com.example.android.quakereport;

import android.app.LoaderManager;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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
// القوسين بعد LoaderCallbacks يحتويان علا شو بدو رجعلي من العمل في doInBackground
public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {

    private static final String LOG_TAG = EarthquakeActivity.class.getName();
    /**
     * URL for earthquake data from the USGS dataset
     */
    //if we want adding our preferences wu should modify url without "?format=geojson&orderby=time&minmag=6&limit=10";"
    private static final String USGS_REQUEST_URL ="https://earthquake.usgs.gov/fdsnws/event/1/query";

    //"https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=6&limit=10";
    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int EARTHQUAKE_LOADER_ID = 1;
    /**
     * Adapter for the list of earthquakes
     */
    private EarthquakeAdapter mAdapter;
    private TextView mEmpteyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);
        // ArrayList<Earthquake> earthquakes = QueryUtils.extractEarthquakes();
        // Start the AsyncTask to fetch the earthquake data
       // EarthquakeAsyncTask task = new EarthquakeAsyncTask();
        //task.execute(USGS_REQUEST_URL);
        /** Create a fake list of earthquake locations.
         ArrayList<Earthquake> earthquakes = new ArrayList<Earthquake>();
         earthquakes.add(new Earthquake("2.5","San Francisco","NOV 12/8/2018" ));
         earthquakes.add(new Earthquake("1.8","London","MAy13/8/2018"));
         earthquakes.add(new Earthquake("4.5","Tokyo","June,2018"));
         earthquakes.add(new Earthquake("3.8","Mexico City","1/8/2018"));
         earthquakes.add(new Earthquake("2.3","Moscow","25/6/2018"));
         earthquakes.add(new Earthquake("2.5","Rio de Janeiro","3/10/2018"));
         earthquakes.add(new Earthquake("1.1","Paris","6/9/2018"));**/

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        mEmpteyStateTextView = (TextView)findViewById(R.id.empty_view);


        // Create a new {@link ArrayAdapter} of earthquakes
        //final EarthquakeAdapter adapter = new EarthquakeAdapter(this,earthquakes);
        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);
        //set empty screen state
        earthquakeListView.setEmptyView(mEmpteyStateTextView);
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Earthquake currentEarthquake = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
        //in mainfast add "access network state "
        //below code for check networking in device before doing request  by loader
        ConnectivityManager connectmng = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ntwInnfo = connectmng.getActiveNetworkInfo();
        //اذا في شبكة ابدا عمل loader
        if (ntwInnfo!=null &&ntwInnfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        }

        else {
            // Hide loading indicator because the data has been loaded
            // indicator (progress)
            //اذا مافي شبكة اخفي ال progress
            View loadingIndicator = findViewById(R.id.loading_spinner);
            //ضبط غير مرئي
            loadingIndicator.setVisibility(View.GONE);
            // show message "no internet connection "
            mEmpteyStateTextView.setText(R.string.no_internet_connection);


        }

    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
       /*return new EarthquakeAsyncTaskLoader(this, USGS_REQUEST_URL);*/
       /*replace the body  to read the user’s latest preferences for the minimum magnitude,
        construct a proper URI with their preference, and then create a new Loader for that URI.*/
        //"PreferenceManager"Used to help create Preference hierarchies from activities or XML

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // نحدد مفتاح وقيمته افتراضية ()

        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);

        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        //uriBuilder.appendQueryParameter("orderby", "time");
        uriBuilder.appendQueryParameter("orderby", orderBy);


        return new EarthquakeAsyncTaskLoader(this, uriBuilder.toString());
    }

    @Override
    /* -why we are using List instead of (arrayLIST,linked list)The reason why you'd ever want to do such a thing is for flexibility:
     *-difference between List and arrayList is List is a interface,so we cannot create any object
     *- arrayList is concrete class  you CAN create an object instance of ArrayList and
      * specify a generic parameter for E, because it is a concrete class.
     */
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {
        // Hide loading indicator because the data has been loaded
        // set progress
        View loadingIndicator = findViewById(R.id.loading_spinner);
        //ضبط غير مرئي
        loadingIndicator.setVisibility(View.GONE);
        // Set empty state text to display "No earthquakes found."
        mEmpteyStateTextView.setText(R.string.no_earthquake);

        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        mAdapter.clear();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id ==R.id.action_settings){
            Intent settingsIntent= new Intent(this,SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


// Creat Inner Class
//فايدتو نريد تحديث ui في onPostExecute لذلك نحتاج اشارة الها من ui فافضل طريق عنل inner class  لان بهي طيريقة بامكاننا نصل لكل متغيرات هذا الاكتيفيتي
// background thread by AsyncTask
//مشطلة asynctask  انو كل مرة بصير فيها اعادة بناء لل activity  بترجع تبدأ من اول وهاد عالفاضي لان عم تروح ذاكرة المفروض تم محتفظة من اولمرة بال   sync task الجل هو loader
      /*  private  class EarthquakeAsyncTask extends AsyncTask<String,Void,List<Earthquake>>{
                                                               string بدها تشتغل بالخلفية وهي بارام
                                                               void is progress data type
                                                               result and in this case is List
                                                               النتيجة بده ترجع بعد انهاء عملية doinBackground

            @Override
            protected List<Earthquake> doInBackground(String... urls) {
                // Don't perform the request if there are no URLs, or the first URL is null.
                if (urls.length < 1 || urls[0] == null) {
                    return null;
                }

                List<Earthquake> result = QueryUtils.fetchEarthquakeData(urls[0]);
                return result;
            }
            */

/**
 * This method runs on the main UI thread after the background work has been
 * completed. This method receives as input, the return value from the doInBackground()
 * method. First we clear out the adapter, to get rid of earthquake data from a previous
 * query to USGS. Then we update the adapter with the new list of earthquakes,
 * which will trigger the ListView to re-populate its list items.
 *
 * @Override protected void onPostExecute(List<Earthquake> data) {
 * // Clear the adapter of previous earthquake data
 * mAdapter.clear();
 * <p>
 * // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
 * // data set. This will trigger the ListView to update.
 * if (data != null && !data.isEmpty()) {
 * mAdapter.addAll(data);
 * }
 */
/** @Override protected void onPostExecute(List<Earthquake> data) {
// Clear the adapter of previous earthquake data
mAdapter.clear();

// If there is a valid list of {@link Earthquake}s, then add them to the adapter's
// data set. This will trigger the ListView to update.
if (data != null && !data.isEmpty()) {
mAdapter.addAll(data);
}
 */


