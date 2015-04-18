package com.sportheads.tom.sportheads;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class BaseActivity extends ActionBarActivity
                          implements ItemListFragment.OnFragmentInteractionListener{

    private static Boolean isFirstTimeCreated = true;
    private static Integer numOfRequests = 0;
    private static boolean downloading = true;
    private static boolean hasMoreItems = true;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Setting a listener for the endless scrolling of the main ListView
        ((ListView) findViewById(R.id.list)).setOnScrollListener(new ItemScrollListener());

        mSwipeRefreshLayout = ((SwipeRefreshLayout) findViewById(R.id.swipe_refresh_item_list));
        mSwipeRefreshLayout.setOnRefreshListener(new ItemListRefreshListener());

        // Checks if the activity is created for the first time, so i won't load items every
        // orientation change/etc..
        if (isFirstTimeCreated) {
            // Getting items and sets them in the main ListView
            new GetHeads().execute("get_heads", numOfRequests.toString());
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

    public void resetAll() {
        numOfRequests = 0;
        downloading = true;
        hasMoreItems = true;
        ((ListView) findViewById(R.id.list)).setOnScrollListener(new ItemScrollListener());
        ItemsContent.eraseAll();
        ItemListFragment listFragment = (ItemListFragment) getFragmentManager().
                findFragmentById(R.id.items_list_fragment);
        listFragment.getAdapter().notifyDataSetChanged();
    }

    private class GetHeads extends AsyncTask<String, Void, JSONArray> {

        ListView list = (ListView) findViewById(R.id.list);

        // Removes the loading circle for footer and puts the "no more items" TextView
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout rl = (RelativeLayout) inflater.inflate(R.layout.loading_panel,
                (ViewGroup) findViewById(R.id.list),
                false);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mSwipeRefreshLayout.setEnabled(false);

            // Checks if it's the first time this task is called, so it'll show the
            // central progress circle
            if (isFirstTimeCreated) {
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            }
            else if(!ItemsContent.ITEMS.isEmpty()) {
                findViewById(R.id.progress_bar_footer).findViewById(R.id.prog_bar).setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected JSONArray doInBackground(String... params) {

            // Currently downloading - so this task won't be called again when already downloading
            downloading = true;

            //////////////// JUST FOR DEBUG PURPOSES!!!!! ////////////////
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            //////////////// JUST FOR DEBUG PURPOSES!!!!! ////////////////

            // Sending the post request, get the items in the form of JSON
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

            // Parsing the JSON, creating Items for each JSON row
            for (int index = 0; index < result.length(); index ++) {
                JSONObject currItem;

                try {
                    currItem = result.getJSONObject(index);

                    // Adds the item to the main list
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

            // Getting the list adapter so it'll notify the changes and update the list
            ItemListFragment listFragment = (ItemListFragment) getFragmentManager().
                                                    findFragmentById(R.id.items_list_fragment);
            listFragment.getAdapter().notifyDataSetChanged();

            // If it's the first item download just finished - remove the central loading cirecle
            // and put a loading circle at the footer of the list
            if (isFirstTimeCreated) {
                // Removes the central loading circle
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                // Puts a loading circle at the footer of the list
                list.addFooterView(rl, null, false);
                isFirstTimeCreated = false;
            }
            else if (result.length() == 0) {
                hasMoreItems = false;

                // Removes the loading circle for footer and puts the "no more items" TextView
                list.removeFooterView(findViewById(R.id.progress_bar_footer));

                // Adds the no_more_items label to list footer
                RelativeLayout textLayout = (RelativeLayout) inflater.inflate(R.layout.no_more_items_layout,
                                                       (ViewGroup) findViewById(R.id.list),
                                                       false);

                list.addFooterView(textLayout, null, false);
            }

            numOfRequests++;

            // Finished downloading - it can start this task again
            downloading = false;

            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);
        }

        private String sendPost(String param, String numOfRequests) {
            try {
                // Opening a connection
                URL url = new URL("http://10.0.0.5/SportheadsService.php");
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
            // If it was previously loading
            if (loading) {
                // Is it not currently downloading and the total item count is bigger than before
                if (totalItemCount > previousTotal && !downloading) {
                    // Obviously it's not loading anymore
                    loading = false;

                    previousTotal = totalItemCount;
                }
            }

            // If it's not currently loading and the one before last item is visible
            if (!loading &&
                hasMoreItems &&
                (firstVisibleItem + visibleItemCount) >= (totalItemCount - 1)) {
                // get new items from server
                new GetHeads().execute("get_heads", numOfRequests.toString());
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }

    private class ItemListRefreshListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {
            findViewById(R.id.progress_bar_footer).findViewById(R.id.prog_bar).setVisibility(View.GONE);
            resetAll();
            new GetHeads().execute("get_heads", numOfRequests.toString());
        }
    }
}
