package edu.uw.team7project.ui.contacts;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A find contact view model
 *
 * @author Trevor Nichols
 */
public class FindContactViewModel extends AndroidViewModel {

    private MutableLiveData<List<Contact>> mContactList;
    private final MutableLiveData<JSONObject> mResponse;


    /**
     * The constructor for the contact list view model.
     *
     * @param application the application.
     */
    public FindContactViewModel(@NonNull Application application) {
        super(application);
        mContactList = new MutableLiveData<>(new ArrayList<>());
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
    }

    /**
     * Add an observer to the find contact view model.
     *
     * @param owner the owner
     * @param observer the observer
     */
    public void addContactListObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super List<Contact>> observer){
        mContactList.observe(owner, observer);
    }

    /**
     * Connects to webservice endpoint to retrieve a list of contacts.
     *
     * @param jwt a valid jwt.
     */
    public void connectGet (String jwt, int memberID){
        String url = "https://mobile-app-spring-2020.herokuapp.com/contacts/getAll/"+memberID;
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handleSuccess,
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

    /**
     * Connects to webservice endpoint to add a contact.
     *
     * @param jwt a valid jwt.
     */
    public void addContact (String jwt, int memberID){
        String url = "https://mobile-app-spring-2020.herokuapp.com/contacts";


        JSONObject body = new JSONObject();

        try {
            body.put("memberId", memberID);
            body.put("verified", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                mResponse::setValue,
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

    /**
     * Handles a successful connection with the webservice.
     *
     * @param result result from webservice.
     */
    private void handleSuccess(final JSONObject result) {
        ArrayList<Contact> temp = new ArrayList<>();
        try {
            JSONArray contacts = result.getJSONArray("listOfUnFriend");
            for (int i = 0; i < contacts.length(); i++) {
                JSONObject contact = contacts.getJSONObject(i);
                JSONObject currUser = contact.getJSONObject("entry");
                String firstName= currUser.getString("firstname");
                String lastName= currUser.getString("lastname");
                int memberID = currUser.getInt("memberid");

                Contact entry = new Contact("", firstName, lastName, "", memberID);
                temp.add(entry);
        }
        } catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ContactViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
        mContactList.setValue(temp);
    }

    /**
     * Handles the error
     * @param error a VolleyError
     */
    private void handleError(final VolleyError error) {
        Log.e("CONNECTION ERROR", "Oooops no contacts");
        //throw new IllegalStateException(error.getMessage());
    }
}
