package com.sportheads.tom.sportheads;

/**
 * Created by Tom on 24/03/2015.
 */
public class Item {

    private int    mGuid;
    private String mTitle;
    private String mDesc;
    private String mImageLink;
    private String mImageDesc;
    private String mLink;
    private String mPubDate;

    public Item(int guid,
                String title,
                String desc,
                String imageLink,
                String imageDesc,
                String link,
                String pubDate) {
        this.mGuid = guid;
        this.mTitle = title;
        this.mDesc = desc;
        this.mImageLink = imageLink;
        this.mImageDesc = imageDesc;
        this.mLink = link;
        this.mPubDate = pubDate;
    }

    public int getmGuid() {
        return mGuid;
    }

    public String getmDesc() {
        return mDesc;
    }

    public String getmImageDesc() {
        return mImageDesc;
    }

    public String getmImageLink() {
        return mImageLink;
    }

    public String getmLink() {
        return mLink;
    }

    public String getmPubDate() {
        return mPubDate;
    }

    public String getmTitle() {
        return mTitle;
    }
}
