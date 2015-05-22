package com.sportheads.tom.sportheads;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;


public class ArticleActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        WebView v = (WebView) findViewById(R.id.article_page);
        WebSettings settings = v.getSettings();
        settings.setJavaScriptEnabled(true);

        final String s = getIntent().getStringExtra("tom");
        AsyncTask<String, Void, Document> a = new AsyncTask<String, Void, Document>() {
            @Override
            protected Document doInBackground(String... params) {
                try {
                    Document doc = Jsoup.connect(params[0]).get();

                    return  doc;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Document doc) {
                Elements element = doc.select("div#ctl00_pnlGoogleAdBottom");
                element.remove();
                Elements ad = doc.select("div[style=width:100%;text-align:center;padding:auto;margin:auto;]");
                ad.remove();
                Elements ad2 = doc.select("div.BannerBlat");
                ad2.remove();
                Elements nav = doc.select("nav#topMenu");
                nav.remove();
                Elements logo1 = doc.select("a#hlRightLogo");
                logo1.remove();
                Elements logo2 = doc.select("a[href*=m.winner.co.il]");
                logo2.remove();
                Elements mainlogo = doc.select("a[href=/Iphone/default.aspx]");
                mainlogo.removeAttr("href");
                Elements footer = doc.select("div#footer");
                footer.remove();
                String html = doc.toString();
                String mime = "text/html; charset=utf-8";
                String encoding = "UTF-8";
                WebView v = (WebView) findViewById(R.id.article_page);
                //v.loadData(html, mime, encoding);
                v.loadDataWithBaseURL(s, html, mime, encoding, null);
            }
        };

        a.execute(s);
//        v.setHorizontalScrollbarOverlay(true);
//        v.getSettings().setUseWideViewPort(true);
//        v.loadUrl(getIntent().getStringExtra("tom"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_article, menu);
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
}
