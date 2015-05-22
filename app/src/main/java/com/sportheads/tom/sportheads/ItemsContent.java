package com.sportheads.tom.sportheads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tom on 04/04/2015.
 */
public class ItemsContent {

    public static List<Item> ITEMS = new ArrayList<>();

    public static Map<Integer, Item> ITEM_MAP = new HashMap<>();

    public static void addItem(int guid,
                               String title,
                               String desc,
                               String imageLink,
                               String imageDesc,
                               String link,
                               String pubDate) {
        Item newItem = new Item(guid, title, desc, imageLink, imageDesc, link, pubDate);
        ITEMS.add(newItem);
        ITEM_MAP.put(newItem.getmGuid(), newItem);
    }

    public static void eraseAll() {
        ITEMS.clear();
        ITEM_MAP.clear();
    }

    public static class Item {

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
}
