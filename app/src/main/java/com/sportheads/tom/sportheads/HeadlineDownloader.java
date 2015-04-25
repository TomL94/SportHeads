package com.sportheads.tom.sportheads;

import android.os.AsyncTask;

/**
 * Created by Tom on 25/04/2015.
 */
public class HeadlineDownloader {

    // <editor-fold desc="Final Members">

    final String SERVICE_NAME = "get_heads";

    // </editor-fold>

    // <editor-fold desc="Data Members">

    private GetHeadlinesTask.TaskCallback mClient;
    private Integer                       mNumOfRequests;
    private GetHeadlinesTask              mDownTask;

    // </editor-fold>

    // <editor-fold desc="Ctors">

    public HeadlineDownloader (GetHeadlinesTask.TaskCallback client) {
        mClient = client;
        mNumOfRequests = 0;
    }

    // </editor-fold>

    // <editor-fold desc="Class Methods">

    public void getNextHeadlines() {
        // Cancelling currently running task
        if ((mDownTask != null) &&
            (mDownTask.getStatus() != AsyncTask.Status.FINISHED)) {
            mDownTask.cancel(false);
        }

        // Starting the downloading task
        mDownTask = new GetHeadlinesTask(mClient);
        mDownTask.execute(SERVICE_NAME, mNumOfRequests.toString());

        // Increments the requests counter
        mNumOfRequests++;
    }

    // </editor-fold>
}