package com.example.android.quakereport;
import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.quakereport.Earthquake;
import com.example.android.quakereport.QueryUtils;

import java.util.List;
/**
 * Loads a list of earthquakes by using an AsyncTask to perform the
 * network request to the given URL.
 * يasynctaskloader اته جيد للمهمات القصيرة النفردة اذا في عمليات تحتاج وقت نحن بحاجة لشي معقد اكتر
 * async task is abstract class
 * نحن بحاجة ل عمل كلاس فرعي لان لازم نسوي override for method load in back ground
 */
public class EarthquakeAsyncTaskLoader extends AsyncTaskLoader<List<Earthquake>> {
    // tag for LOG message
    private static final  String LOG_TAG = EarthquakeAsyncTaskLoader.class.getName();
    String mUrl;
    /**
     * Constructs a new {@link EarthquakeAsyncTaskLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public  EarthquakeAsyncTaskLoader (Context context,String url){
        super(context);
        mUrl= url;
    }
      // نستدعي هذه لتفعيل اال loadbackground
    //in main activity initLoader() when called ,it will call on start loading
    @Override
    protected void onStartLoading() {
        // is important to starting loader
        forceLoad();
    }

    @Override
    //هنا الغمل في الخلية
    public List<Earthquake> loadInBackground() {
        if (mUrl== null ){return null;}
        // Perform the network request, parse the response, and extract a list of earthquakes.
        /*هون بالخلفية بواسطة ميثود ى fetchEarthquakeData رح يصسر اول شي url بعدا رح نسوي  http request بعدا رح نطالع النص من stream ونخزنه في jsoResponse بعدا رح نطالع االبيانات اللي بدنا هي من json response واخر شي نرجع arrayList<earthquake>*/
        List<Earthquake> eartquakes= QueryUtils.fetchEarthquakeData(mUrl);
        return eartquakes;
    }
}
