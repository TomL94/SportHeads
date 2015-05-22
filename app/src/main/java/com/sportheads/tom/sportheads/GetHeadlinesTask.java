package com.sportheads.tom.sportheads;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tom on 25/04/2015.
 */
public class GetHeadlinesTask extends AsyncTask<String, Integer, JSONArray> {

    // <editor-fold desc="Final Members">

//    private final String SERVICE_URL              = "http://sportheads.ddns.net/SportheadsService.php";
    private final String SERVICE_URL              = "http://10.0.0.5/SportheadsService.php";
    private final String REQ_METHOD               = "POST";
    private final String ACCEPT_LANG              = "en-US,en;q=0.8,he;q=0.6";
    private final String CONTENT_TYPE             = "application/x-www-form-urlencoded";
    private final String SERVICE_NAME_PARAM       = "serv";
    private final String SERVICE_NUM_OF_REQ_PARAM = "nor";

    // </editor-fold>

    // <editor-fold desc="Data Members">

    private TaskCallback mCallback;

    // </editor-fold>

    // <editor-fold desc="Ctors">

    public GetHeadlinesTask (TaskCallback callback) {
        mCallback = callback;
    }

    // </editor-fold>

    // <editor-fold desc="Class Methods">

    @Override
    protected void onPreExecute() {
        // Publishes progress of 0% (start of downloading)
        publishProgress(0);

        if (mCallback != null) {
            mCallback.onPreExecute();
        }
    }

    @Override
    protected JSONArray doInBackground(String... params) {
        // Getting the headlines from the web service
        String jsonHeads = sendPost(params[0], params[1]);
        ////////////////////////////////////FOR DEBUG///////////////////////////////////////////////
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        ////////////////////////////////////FOR DEBUG///////////////////////////////////////////////
        try {
            return new JSONArray(jsonHeads);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (mCallback != null) {
            mCallback.onProgressUpdate(values[0]);
        }
    }

    @Override
    protected void onPostExecute(JSONArray jsonHeads) {
        if (mCallback != null) {
            mCallback.onPostExecute(jsonHeads);
        }

        // Publishes progress of 100% (end of downloading)
        publishProgress(100);
    }

    private String sendPost(String serviceName, String numOfRequests) {
        try {
            // Opening a connection
            URL url = new URL(SERVICE_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // Adding request header
            con.setRequestMethod(REQ_METHOD);
            con.setRequestProperty("Accept-Language", ACCEPT_LANG);
            con.setRequestProperty("Content-Type", CONTENT_TYPE);

            // Adding request body
            String urlParameters = SERVICE_NAME_PARAM + "=" + serviceName + "&" +
                                   SERVICE_NUM_OF_REQ_PARAM + "=" + numOfRequests;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            // Getting the response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            return (response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // </editor-fold>

    // <editor-fold desc="Interfaces">

    public interface TaskCallback {
        void onPreExecute();
        void onCancelled();
        void onPostExecute(JSONArray jsonHeads);
        void onProgressUpdate(Integer progress);
        void onNoResults();
    }

    // </editor-fold>
}
