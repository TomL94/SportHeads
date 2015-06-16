package com.sportheads.tom.sportheads;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;


public class BaseActivity extends ActionBarActivity
                          implements ItemListFragment.OnFragmentInteractionListener,
                                     ItemListFragment.HeadlinesFragmentCallback {

    // <editor-fold desc="Data Members">

    private View mFragment;
    private View mProgressBar;

    // </editor-fold>

    // <editor-fold desc="Class Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mFragment = findViewById(R.id.items_list_fragment);
        mProgressBar = findViewById(R.id.loading_panel);

        if (ItemsContent.ITEMS.isEmpty()) {
            mFragment.setVisibility(View.GONE);
        }

        initImageLoader();
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

    private void initImageLoader() {
        DisplayImageOptions displayOptions = new DisplayImageOptions.Builder().
                cacheInMemory(true).
                showImageOnLoading(R.mipmap.trans_block).
                imageScaleType(ImageScaleType.EXACTLY_STRETCHED).
                displayer(new FadeInBitmapDisplayer(600)).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).
                defaultDisplayImageOptions(displayOptions).build();
        ImageLoader.getInstance().init(config);
    }

    // </editor-fold>

    // <editor-fold desc="FragmentInteractionListener Methods"

    @Override
    public void onFragmentInteraction(String id) {
        Intent goToArticleIntent = new Intent(this, ArticleActivity.class);
        ItemsContent.Item selectedItem = ItemsContent.ITEM_MAP.get(Integer.parseInt(id));
        goToArticleIntent.putExtra("tom", "http://m.one.co.il/iPhone/Articles/Article.aspx?id=" + selectedItem.getmGuid());
        startActivity(goToArticleIntent);
    }

    // </editor-fold>

    // <editor-fold desc="Fragment Callback Methods">

    @Override
    public void onDownloadFinish() {
        if (mFragment.getVisibility() == View.GONE &&
            !ItemsContent.ITEMS.isEmpty()) {
            mFragment.setVisibility(View.VISIBLE);

            if (mProgressBar.getVisibility() == View.VISIBLE) {
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }

    // </editor-fold>
}
