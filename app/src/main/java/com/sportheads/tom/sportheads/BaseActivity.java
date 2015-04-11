package com.sportheads.tom.sportheads;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;


public class BaseActivity extends ActionBarActivity
                          implements ItemListFragment.OnFragmentInteractionListener{

    private static Boolean isFirstTimeCreated = true;
    private static Integer numOfRequests = 0;
    private static boolean downloading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        ((ListView) findViewById(R.id.list)).setOnScrollListener(new ItemScrollListener());

        if (isFirstTimeCreated) {
            new GetHeads().execute("get_heads", numOfRequests.toString());
            isFirstTimeCreated = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_general_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    private class GetHeads extends AsyncTask<String, Void, JSONArray> {

        //private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (ItemsContent.ITEMS.isEmpty()) {
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            }
            else {
//                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                RelativeLayout rl;// = (RelativeLayout) findViewById(R.id.progress_bar_footer);
//                rl.setHorizontalGravity(Gravity.CENTER);
//                rl.setVerticalGravity(Gravity.BOTTOM);
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rl = (RelativeLayout) inflater.inflate(R.layout.loading_panel, (ViewGroup) findViewById(R.id.list), false);
                ((ListView) findViewById(R.id.list)).addFooterView(rl);
            }
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            downloading = true;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String heads = sendPost(params[0], params[1]);

            try {
                return new JSONArray(heads);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            for (int index = 0; index < result.length(); index ++) {
                JSONObject currItem;
                try {
                    currItem = result.getJSONObject(index);
                    ItemsContent.addItem(currItem.getInt("item_guid"),
                            currItem.getString("item_title"),
                            currItem.getString("item_desc"),
                            currItem.getString("img_link"),
                            currItem.getString("img_desc"),
                            currItem.getString("item_link"),
                            currItem.getString("item_date"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (((ListView) findViewById(R.id.list)).getFooterViewsCount() != 0) {
                ((ListView) findViewById(R.id.list)).
                        removeFooterView(findViewById(R.id.progress_bar_footer));
            }

            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            ItemListFragment f = (ItemListFragment) getFragmentManager().findFragmentById(R.id.items_list_fragment);
            f.getAdapter().notifyDataSetChanged();
            numOfRequests++;
            downloading = false;
        }

        private String sendPost(String param, String numOfRequests) {
            try {
                // Opening a connection
                URL url = new URL("http://sportheads.ddns.net/SportheadsService.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                // Adding request header
                con.setRequestMethod("POST");
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.8,he;q=0.6");
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Adding request body
                String urlParameters = "serv=" + param + "&nor=" + numOfRequests;
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
    }

    private class ItemScrollListener implements AbsListView.OnScrollListener {

        private int previousTotal = 0;
        private boolean loading = true;

        public ItemScrollListener() {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (loading) {
                if (totalItemCount > previousTotal && !downloading) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            //if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleItemCount)) {
            if (!loading && (firstVisibleItem + visibleItemCount) >= (totalItemCount - 1)) {
                // I load the next page of gigs using a background task,
                // but you can call any function here.
                new GetHeads().execute("get_heads", numOfRequests.toString());
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }
}
