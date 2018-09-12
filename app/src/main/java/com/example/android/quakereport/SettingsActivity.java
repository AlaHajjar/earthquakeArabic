package com.example.android.quakereport;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);


    }
    /*implement the OnPreferenceChangeListener interface to get notified when a preference changes.
     Then when a single Preference has been changed by the user
     عسبيل المثال اذا غيرنا القوة بدل ما ادخل لاشوفها بقدر من بررا شوفها وكل ما تغيرت تتغير لحالها */
    public static  class EarthquakePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener{
        /*override the onCreate() method to use the settings_main XML resource that we defined earlier.*/
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            /* update the preference summary when the settings activity is launched.  get the Preference object
             by Given the key of a preference, */
            Preference minMagnitude = findPreference(getString(R.string.settings_min_magnitude_key));
            /*setup the preference using a helper method called bindPreferenceSummaryToValue().*/
            bindPreferenceSummaryToValue(minMagnitude);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);
        }


        @Override
        /*The code in this method takes care of updating the displayed preference summary after it has been changed.
        *  update the onPreferenceChange() method
        *  to properly update the summary of a ListPreference (using the label, instead of the key).*/
        public boolean onPreferenceChange(Preference preference, Object value) {
           // String stringValue = value.toString();
           // preference.setSummary(stringValue);
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
        /*Now we need to define the bindPreferenceSummaryToValue() helper method to set
         the current EarthquakePreferenceFragment instance as the listener on each preference.
         We also read the current value of the preference stored in the SharedPreferences on the device,
          and display that in the preference summary (so that the user can see the current value of the preference).*/
        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }
}
