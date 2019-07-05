package com.gahee.rss_v1.startApp;

import com.gahee.rss_v1.R;

import java.util.Arrays;

public class PhotoUtils{

    private String []  Topics
            = {
            "Top Stories", "World", "Africa", "Americas", "Asia",
            "Europe", "Middle East", "U.S.", "Money", "Technology",
            "Science & Space", "Entertainment", "World Sport", "Football",
            "Golf", "Motorsport", "Tennis", "Travel", "Video", "Most Recent"
    };

    private int [] Photos = {
            R.drawable.top_stories, R.drawable.world, R.drawable.africa,
            R.drawable.americas, R.drawable.asia, R.drawable.europe, R.drawable.middle_east,
            R.drawable.us, R.drawable.money, R.drawable.technology, R.drawable.science,
            R.drawable.entertainment, R.drawable.world_sport, R.drawable.football,
            R.drawable.golf, R.drawable.motorsport, R.drawable.tennis,
            R.drawable.travel, R.drawable.video, R.drawable.latest_news
    };

    public String [] getTopicsOfPhotos(){
        return Topics;
    }

    public int [] getPhotos(){return Photos; }

    public int getPhotoByTopic(String topic){
        int index = Arrays.binarySearch(Topics, topic);
        int photo = Photos[index];
        return photo;
    }

}