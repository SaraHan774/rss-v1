package com.gahee.rss_v1.helpers;

public class Constants {
    public static final int TOP_STORIES = 0;
    public static final int WORLD = 1;
    public static final int AFRICA = 2;
    public static final int AMERICAS = 3;
    public static final int ASIA = 4;
    public static final int EUROPE = 5;
    public static final int MIDDLE_EAST = 6;
    public static final int US = 7;
    public static final int MONEY = 8;
    public static final int TECH = 9;
    public static final int SCIENCE = 10;
    public static final int ENTERTAINMENT = 11;
    public static final int WORLD_SPORT = 12;
    public static final int FOOTBALL = 13;
    public static final int GOLF = 14;
    public static final int MOTOR_SPORT = 15;
    public static final int TENNIS = 16;
    public static final int TRAVEL = 17;
    public static final int MOST_RECENT = 18;

    public static final String BASE_URL = "http://rss.cnn.com/rss/";

    //for saving user's name
    public static final String SHARED_PREF_USER_NAME = "user_name_sp";
    public static final String USER_NAME_KEY = "user_name_key";

    //for user's profile photo
    public static final String SHARED_PREF_USER_PIC = "user_pic_pref";
    public static final String USER_PIC_KEY = "user_pic_key";
    public static final int LOAD_IMAGE = 6676;

    //for sending search results via intent
    public static final String SEARCH_RESULT_INTENT = "search_result";

    //for links that gets passed over to ArticleDetailActivity
    public static final String ARTICLES = "articles_array_list";
    public static final String ADAPTER_POSITION = "link";

    //for invoking widget update
    public static final String UPDATE_WIDGET_INFO = "update_widget";
    public static final String FILLIN_INTENT_EXTRA = "fill in intent";
}
