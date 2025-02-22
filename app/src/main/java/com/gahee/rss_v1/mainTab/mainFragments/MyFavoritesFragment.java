package com.gahee.rss_v1.mainTab.mainFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gahee.rss_v1.R;
import com.gahee.rss_v1.mainTab.recyclerViewAdapters.MyFavoritesAdapter;
import com.gahee.rss_v1.roomDatabase.FavEntities;
import com.gahee.rss_v1.roomDatabase.ViewModelRoom;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MyFavoritesFragment extends Fragment {
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.

    private RecyclerView myFavoritesRecyclerView;
    private RecyclerView.Adapter adapter;
    private FloatingActionButton shareFAB;
    private List<FavEntities> favEntities;
    private FirebaseAnalytics mFirebaseAnalytics;


    public MyFavoritesFragment(){
        //required public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(Objects.requireNonNull(getContext()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //defines the xml file for the fragment
        View view = inflater.inflate(R.layout.fragment_my_favorites, container, false);

        myFavoritesRecyclerView = view.findViewById(R.id.my_favorites_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        myFavoritesRecyclerView.setLayoutManager(layoutManager);

        ViewModelRoom viewModelRoom = ViewModelProviders.of(this).get(ViewModelRoom.class);
        viewModelRoom.getFavoriteNews().observe(this, new Observer<List<FavEntities>>() {
            @Override
            public void onChanged(List<FavEntities> favEntities) {
                adapter = new MyFavoritesAdapter(getContext(), favEntities);
                myFavoritesRecyclerView.setAdapter(adapter);
                getMyFavorites(favEntities);
            }
        });

        shareFAB = view.findViewById(R.id.fab_my_fav_tab);
        setShareButton();

        return view;
    }

    private void getMyFavorites(List<FavEntities> favEntities){
        this.favEntities = favEntities;
    }


    private final StringBuilder stringBuilder = new StringBuilder();
    private void setShareButton(){

        shareFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!stringBuilder.toString().equals("")){
                    stringBuilder.setLength(0);
                }
                for(int i =0; i < favEntities.size(); i++){
                    if(i > 5){
                        // to only get top 5 articles
                        break;
                    }
                    String linkToShare = favEntities.get(i).getArticleLink();
                    int starCount = favEntities.get(i).getCount();

                    firebaseLogEvent(linkToShare);

                    //sharing format
                    String shareContentStars = String.format(Locale.US, "MY STARS : %d", starCount);
                    String shareContentArticleLink = String.format(Locale.US, " - ARTICLE LINK : %s\n\n", linkToShare);

                    stringBuilder.append(shareContentStars)
                            .append(shareContentArticleLink);
                }
            shareTopFiveArticles(stringBuilder.toString(), getString(R.string.share_title));
            }
        });
    }

    private void shareTopFiveArticles(String content, String articleTitle){
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, articleTitle);
        share.putExtra(Intent.EXTRA_TEXT, content);
        Objects.requireNonNull(getContext()).startActivity(Intent.createChooser(share, getString(R.string.share_top_five)));
    }

    private void firebaseLogEvent(String link){
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, link);
        mFirebaseAnalytics.logEvent("share_articles", params);
    }


}
