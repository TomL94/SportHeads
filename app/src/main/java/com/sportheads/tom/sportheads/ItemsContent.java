package com.sportheads.tom.sportheads;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
                               Date   pubDate,
                               Date   enteredDate) {
        // TODO: Already exist? delete the old one
        Item newItem = new Item(guid, title, desc, imageLink, imageDesc, link, pubDate, enteredDate);

        // If the Item already exists
        if (ITEM_MAP.containsKey(newItem.getmGuid())) {
            ITEMS.remove(ITEM_MAP.get(newItem.getmGuid()));
            ITEM_MAP.remove(newItem.getmGuid());
        }

        ITEMS.add(newItem);
        Collections.sort(ITEMS, new PublishDateComparator());
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
        private Date   mPubDate;
        private Date   mEnteredDate;

        public Item(int guid,
                    String title,
                    String desc,
                    String imageLink,
                    String imageDesc,
                    String link,
                    Date   pubDate,
                    Date   enteredDate) {
            this.mGuid = guid;
            this.mTitle = title;
            this.mDesc = desc;
            this.mImageLink = imageLink;
            this.mImageDesc = imageDesc;
            this.mLink = link;
            this.mPubDate = pubDate;
            this.mEnteredDate = enteredDate;
        }

        public int    getmGuid() {
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

        public Date   getmPubDate() {
            return mPubDate;
        }

        public String getmTitle() {
            return mTitle;
        }

        public Date   getmEnteredDate() {
            return mEnteredDate;
        }
    }

    //region Comparator

    public static class PublishDateComparator implements Comparator<Item> {
        /**
         * Compares the two specified objects to determine their relative ordering. The ordering
         * implied by the return value of this method for all possible pairs of
         * {@code (lhs, rhs)} should form an <i>equivalence relation</i>.
         * This means that
         * <ul>
         * <li>{@code compare(a,a)} returns zero for all {@code a}</li>
         * <li>the sign of {@code compare(a,b)} must be the opposite of the sign of {@code
         * compare(b,a)} for all pairs of (a,b)</li>
         * <li>From {@code compare(a,b) > 0} and {@code compare(b,c) > 0} it must
         * follow {@code compare(a,c) > 0} for all possible combinations of {@code
         * (a,b,c)}</li>
         * </ul>
         *
         * @param firstItem an {@code Object}.
         * @param secondItem a second {@code Object} to compare with {@code firstItem}.
         * @return an integer < 0 if {@code firstItem} is less than {@code secondItem}, 0 if they are
         * equal, and > 0 if {@code firstItem} is greater than {@code secondItem}.
         * @throws ClassCastException if objects are not of the correct type.
         */
        @Override
        public int compare(Item firstItem, Item secondItem) {
            return secondItem.getmEnteredDate().compareTo(firstItem.getmEnteredDate());
        }
    }

    //endregion
}
