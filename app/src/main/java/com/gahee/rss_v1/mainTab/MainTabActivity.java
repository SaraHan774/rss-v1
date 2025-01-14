package com.gahee.rss_v1.mainTab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.gahee.rss_v1.news.model.Article;
import com.gahee.rss_v1.R;
import com.gahee.rss_v1.mainTab.pagerAdapters.MainFragmentPagerAdapter;
import com.gahee.rss_v1.remoteDataSource.ViewModelRemote;
import com.gahee.rss_v1.roomDatabase.TopicStrings;
import com.gahee.rss_v1.roomDatabase.ViewModelRoom;
import com.gahee.rss_v1.searchResult.SearchResultActivity;
import com.gahee.rss_v1.topicSelection.TopicSelectionActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.gahee.rss_v1.helpers.Constants.LOAD_IMAGE;
import static com.gahee.rss_v1.helpers.Constants.SEARCH_RESULT_INTENT;
import static com.gahee.rss_v1.helpers.Constants.SHARED_PREF_USER_NAME;
import static com.gahee.rss_v1.helpers.Constants.USER_NAME_KEY;


public class MainTabActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = MainTabActivity.class.getSimpleName();
    private ArrayList<ArrayList<Article>> arrayLists = new ArrayList<>();
    private ArrayList<Article> auxiliaryListForSearch = new ArrayList<>();
    private NavigationView navigationView;
    private List<TopicStrings> topicStrings;
    private ImageView profilePhoto;
    private View headerView;
    private Uri userPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_and_navigate_activity);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            Log.d(TAG, "initialization complete for ad mob");
            }
        });


        Log.d(TAG, "on create () ");
        //connect adapter to the view pager
        ViewPager viewPager = findViewById(R.id.main_news_view_pager);
        TabLayout tabLayout = findViewById(R.id.tabs);

        PagerAdapter adapter = new MainFragmentPagerAdapter(
                getSupportFragmentManager()
        );

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        ViewModelRemote viewModelRemote = ViewModelProviders.of(this).get(ViewModelRemote.class);
        viewModelRemote.getMutableLiveDataForSearch().observe(this, new Observer<ArrayList<Article>>() {
            @Override
            public void onChanged(ArrayList<Article> articles) {
                MainTabActivity.this.auxiliaryListForSearch = articles;
            }
        });



        //set up navigation drawer
        Toolbar toolbar = findViewById(R.id.nav_toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //get user name
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_USER_NAME, MODE_PRIVATE);
        String userNameString = sharedPreferences.getString(USER_NAME_KEY, getResources().getString(R.string.user_name_default));

        //set user name in the header of the navigation drawer
        headerView = navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.tv_nav_user_name);
        userName.append(userNameString);

        //observing the topic string list from the database
        ViewModelRoom viewModelRoom = ViewModelProviders.of(this).get(ViewModelRoom.class);
        viewModelRoom.getTopicStrings().observe(this, new Observer<List<TopicStrings>>() {
            @Override
            public void onChanged(List<TopicStrings> topicStrings) {
                MainTabActivity.this.topicStrings = topicStrings;
                Log.d(TAG, "topic strings observed : " + topicStrings);
                //adding menu on the fly
                addMenuItemInNavDrawer(topicStrings);

            }
        });
        //enabling update profile photo function
        enableProfilePhotoUpdate();

        //setting up ad view
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "on start () ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "on resume () ");
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //list to store search results
    private final ArrayList<Article> searchResultList = new ArrayList<>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.news_search_icon).getActionView();
        searchView.setFocusable(false);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //make an array list to store search results
                if(searchResultList != null){
                    Log.d(TAG, "search list cleared ");
                    //clear the list before another search
                    searchResultList.clear();
                }
                checkThroughArticles(query);

                if(searchResultList == null){
                    Toast.makeText(MainTabActivity.this, R.string.no_search_result, Toast.LENGTH_SHORT).show();
                }else{
                    checkDuplicates();
                    Intent intent = new Intent(MainTabActivity.this, SearchResultActivity.class);
                    intent.putParcelableArrayListExtra(SEARCH_RESULT_INTENT, searchResultList);
                    intent.setAction(Intent.ACTION_SEARCH);
                    startActivity(intent);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return true;
    }

    synchronized private void checkThroughArticles(String query){
        for(int i =0; i < auxiliaryListForSearch.size(); i++){
            if(auxiliaryListForSearch.get(i).getArticleTitle() != null &&
                    auxiliaryListForSearch.get(i).getArticleTitle().contains(query)){
                searchResultList.add(auxiliaryListForSearch.get(i));

            } else if(auxiliaryListForSearch.get(i).getArticleDescription() != null &&
                    auxiliaryListForSearch.get(i).getArticleDescription().contains(query)) {
                searchResultList.add(auxiliaryListForSearch.get(i));
            }
        }
    }

    synchronized private void checkDuplicates(){
        Set<Article> setOfArticles= new HashSet<>(searchResultList);
        ArrayList<Article> toRemove = new ArrayList<>();

        for(Article article : setOfArticles){
            if(Collections.frequency(searchResultList, article) > 1){
                toRemove.add(article);
            }
        }
        searchResultList.removeAll(toRemove);
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
         int id = item.getItemId();
          if (id == R.id.action_settings) {
              Intent intent = new Intent(this, TopicSelectionActivity.class);
              startActivity(intent);
              finish();
                return true;
            }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int item = menuItem.getItemId();
        if(item == R.id.nav_tools) {
            Intent intent = new Intent(this, TopicSelectionActivity.class);
            startActivity(intent);
            finish();
        }
        return false;
    }

    //generating menu items dynamically
    private void addMenuItemInNavDrawer(List<TopicStrings> topicStrings){
        Menu menu = navigationView.getMenu();
        Menu subMenu = menu.addSubMenu("TOPICS");

            for(int i =0 ; i < topicStrings.size(); i++){
            subMenu.add(topicStrings.get(i).getTopicString());
        }
        navigationView.invalidate();
    }

    private void enableProfilePhotoUpdate(){
        profilePhoto = headerView.findViewById(R.id.img_nav_user_photo);
        if(userPhoto != null){
            profilePhoto.setImageURI(userPhoto);
        }
        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSetProfilePic = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI
                );
                startActivityForResult(intentSetProfilePic, LOAD_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "on activity result : " + requestCode + ", " + resultCode + ", " + data);
        if(requestCode == LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            userPhoto = data.getData();
            profilePhoto.setImageURI(userPhoto);
        }
    }
}