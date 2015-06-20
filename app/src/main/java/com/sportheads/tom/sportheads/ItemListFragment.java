package com.sportheads.tom.sportheads;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.TooManyListenersException;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ItemListFragment extends Fragment implements AbsListView.OnItemClickListener,
                                                          GetHeadlinesTask.TaskCallback {
    // <editor-fold desc="Data Members">

    private OnFragmentInteractionListener mListener;
    private View                          mFragmentView;
    private AbsListView                   mListView;
    private ItemListAdapter               mAdapter;
    private SwipeRefreshLayout            mSwipeRefreshLayout;
    private HeadlinesFragmentCallback     mCallback;
    private HeadlineDownloader            mHeadsDownloader;
    private boolean                       mGotMoreHeads;
    private boolean                       mCurrentlyDownloading;

    // </editor-fold>

    // <editor-fold desc="Interfaces">

    // Container Activity must implement this interface
    public interface OnItemSelectedListener {
        public void onItemSelected(int position);
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String id);
    }

    public interface HeadlinesFragmentCallback {
        public void onDownloadFinish();
    }

    // </editor-fold>

    // <editor-fold desc="Ctors">

    public ItemListFragment() {
        mHeadsDownloader = new HeadlineDownloader(this);
        mGotMoreHeads = true;
        mCurrentlyDownloading = false;
    }

    public static ItemListFragment newInstance() {
        ItemListFragment fragment = new ItemListFragment();

        return fragment;
    }

    // </editor-fold>

    // <editor-fold desc="Class Methods">

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new ItemListAdapter(getActivity(), R.layout.item_layout, ItemsContent.ITEMS);

        // Retain this fragment across configuration changes
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        mListView = (AbsListView) mFragmentView.findViewById(R.id.list);
        mListView.setAdapter(mAdapter);

        // Set the loading circle footer
        ((ListView) mListView).addFooterView(inflater.inflate(R.layout.loading_panel,
                mListView,
                false));

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        initSwipeRefreshLayout();

        initEndlessScrolling();

        return mFragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        try {
            mCallback = (HeadlinesFragmentCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TaskCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mCallback = null;
    }

    public ItemListAdapter getAdapter() {
        return mAdapter;
    }

    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    private void initSwipeRefreshLayout() {
        // Finds the SwipeRefreshLayout
        mSwipeRefreshLayout = ((SwipeRefreshLayout)
                        mFragmentView.findViewById(R.id.swipe_refresh_item_list));

        // Sets an onRefresh listener to it
        mSwipeRefreshLayout.setOnRefreshListener(new ItemListRefreshListener());
    }

    private void initEndlessScrolling() {
        mListView.setOnScrollListener(new ItemScrollListener());
    }

    // </editor-fold>

    // <editor-fold desc="OnItemClickListener Methods">

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction
                    (((Integer) ItemsContent.ITEMS.get(position).getmGuid()).toString());
        }
    }

    // </editor-fold>

    // <editor-fold desc="GetHeadlinesTask Methods">

    @Override
    public void onPreExecute() {
        // Checks if SwipeToRefresh is enabled, so the user won't be able to refresh
        // multiple times at once
        if (mSwipeRefreshLayout.isEnabled()) {
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    @Override
    public void onProgressUpdate(Integer progress) {
        // Checking and changes accordingly whether currently downloading or not
        if (progress == 0) {
            mCurrentlyDownloading = true;
        }
        else if (progress == 100) {
            mCurrentlyDownloading = false;
        }
    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExecute(JSONArray jsonHeads, GetHeadlinesTask.RequestType requestType) {
        // Parsing the headlines json we got
        if (parseHeadlines(jsonHeads) == 0 && requestType == GetHeadlinesTask.RequestType.getNewHeadlines) {
            //Toast.makeText(getActivity(), "Everything is up to date", Toast.LENGTH_SHORT).show();
            // TODO: Put this string to resources
            Snackbar.make(mListView, "Everything is up to date.", Snackbar.LENGTH_SHORT).show();
        } else {
            // Refreshing the ListView
            mAdapter.notifyDataSetChanged();
        }

        // Checking if swipe to refresh is currently enabled
        if (!mSwipeRefreshLayout.isEnabled()) {
            // Enabling back the swipe to refresh function
            mSwipeRefreshLayout.setEnabled(true);

            // Checks if the list is currently refreshing
            if (mSwipeRefreshLayout.isRefreshing()) {
                // Returns the SwipeToRefresh back to normal (enabling refresh triggering)
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }

//        // Checks if the list is visible
//        if (mListView.getVisibility() == View.GONE) {
//            mListView.setVisibility(View.VISIBLE);
//        }

        mCallback.onDownloadFinish();
        //mCurrentlyDownloading = false;
    }

    @Override
    public void onNoResults() {
        mGotMoreHeads = false;
    }

    private int parseHeadlines(JSONArray jsonHeads) {
        // Parsing the JSON, creating Items for each JSON row
        for (int index = 0; index < jsonHeads.length(); index ++) {
            JSONObject currItem;

            try {
                currItem = jsonHeads.getJSONObject(index);

                // Adds the item to the main list
                try {
                    ItemsContent.addItem(currItem.getInt("item_guid"),
                            currItem.getString("item_title"),
                            currItem.getString("item_desc"),
                            currItem.getString("img_link"),
                            currItem.getString("img_desc"),
                            currItem.getString("item_link"),
                            DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).parse(currItem.getString("format_item_date")),
                            DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).parse(currItem.getString("format_date_entered")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonHeads.length();
    }

    // </editor-fold>

    // <editor-fold desc="Swipe To Refresh">

    private class ItemListRefreshListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {
            //resetAll();
            //mListView.setVisibility(View.GONE);
            String mostRecentDate = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.MEDIUM).format(mAdapter.getItem(0).getmEnteredDate());
            String help = mostRecentDate.substring(4, 7);
            mostRecentDate = help.substring(1) + "/" + mostRecentDate.replace(help, "");
            mHeadsDownloader.getNewHeadlines(mostRecentDate);
            //mCurrentlyDownloading = true;
            //String c = b.substring(1) + "/" + a;
            //mHeadsDownloader.getNextHeadlines();
            //mAdapter.notifyDataSetChanged();
        }
    }

    private void resetAll() {
        mHeadsDownloader.reset();
        ItemsContent.eraseAll();
    }

    // </editor-fold>

    // <editor-fold desc="Endless Scrolling">

    private class ItemScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            // Checking if the item before last in the list is visible, if it's not currently
            // downloading and if it's still got more headlines to download
            if ((firstVisibleItem + visibleItemCount) >= (totalItemCount - 1) &&
                    !mCurrentlyDownloading &&
                    mGotMoreHeads) {
                // Downloading more headlines
                mHeadsDownloader.getNextHeadlines();
                //mCurrentlyDownloading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }

    // </editor-fold>
}
