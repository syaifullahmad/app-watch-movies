package com.lazday.youtubeplayer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.lazday.youtubeplayer.R;
import com.lazday.youtubeplayer.adapter.TrailerAdapter;
import com.lazday.youtubeplayer.model.TrailerModel;
import com.lazday.youtubeplayer.retrofit.RetrofitInstance;
import com.lazday.youtubeplayer.retrofit.MovieService;
import com.lazday.youtubeplayer.retrofit.Constant;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrailerActivity extends AppCompatActivity {
    private final String TAG = "TrailerActivity";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private YouTubePlayer youTubePlayer;
    private TrailerAdapter trailerAdapter;
    private List<TrailerModel.Results> trailerModels = new ArrayList<>();
    private String cueKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);

        trailerAdapter = new TrailerAdapter(this, trailerModels, new TrailerAdapter.OnAdapterListener() {
            @Override
            public void OnClick(String key) {
                if (youTubePlayer != null) {
                    youTubePlayer.loadVideo( key, 0 );
                }
            }

            @Override
            public void OnVideo(String key) {
                cueKey = key;
            }
        });


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter( trailerAdapter );

        getVideos();

        YouTubePlayerView youTubePlayerView = findViewById(R.id.youTubePlayerView);
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer player) {
                youTubePlayer = player;
                if (cueKey != null) {
                    youTubePlayer.cueVideo( cueKey, 0);
                }
            }
        });

        getSupportActionBar().setTitle( Constant.MOVIE_TITLE );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void getVideos(){

        MovieService apiInterface = RetrofitInstance.getUrl().create(MovieService.class);
        Call<TrailerModel> call = apiInterface.getTrailer( String.valueOf(Constant.MOVIE_ID), Constant.KEY);
        call.enqueue(new Callback<TrailerModel>() {
            @Override
            public void onResponse(Call<TrailerModel> call, Response<TrailerModel> response) {
                Log.e(TAG, response.toString());
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    TrailerModel video = response.body();
                    List<TrailerModel.Results> results = video.getResults();
                    trailerAdapter.setData( results );
                }

            }

            @Override
            public void onFailure(Call<TrailerModel> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }
}
