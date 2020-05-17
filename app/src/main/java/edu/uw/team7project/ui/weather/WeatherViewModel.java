package edu.uw.team7project.ui.weather;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.team7project.ui.messages.MessagePost;

public class WeatherViewModel extends AndroidViewModel {

    private MutableLiveData<Weather> mWeather;

    public WeatherViewModel(@NonNull Application application) {
        super(application);
        mWeather = new MutableLiveData<>();
    }

    public void addMessageListObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super Weather> observer) {
        mWeather.observe(owner, observer);
    }

    public Weather getCurrentWeather() {
        return mWeather.getValue();
    }

    public void connectGetCurrent( String city, String jwt){
        //need a endpoint
        String url = "https://mobile-app-spring-2020.herokuapp.com/weather/" + city;
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handleCurrentResult,
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);
    }

    private void handleCurrentResult(JSONObject jsonObject) {
        try {
            JSONArray weather = jsonObject.getJSONArray("weather");
            JSONObject currWeather = weather.getJSONObject(0);
            // = currWeather.getString("description");
            JSONObject main = jsonObject.getJSONObject("main");
            // = main.getString("temp");
            mWeather.setValue(new Weather(currWeather.getString("description"),
                    main.getDouble("temp"),
                    main.getDouble("temp_min"),
                    main.getDouble("temp_max"),
                    main.getDouble("humidity"),
                    main.getString("icon")));
        } catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ChatViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
    }

    private void handleError(final VolleyError error) {
        Log.e("CONNECTION ERROR", error.getLocalizedMessage());
        throw new IllegalStateException(error.getMessage());
    }
}
