package com.lazday.youtubeplayer.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lazday.youtubeplayer.R;
import com.lazday.youtubeplayer.model.DetailModel;
import com.lazday.youtubeplayer.retrofit.Constant;
import com.lazday.youtubeplayer.retrofit.MovieService;
import com.lazday.youtubeplayer.retrofit.RetrofitInstance;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    private final String TAG = "DetailActivity";

    private MovieService service = RetrofitInstance.getUrl().create(MovieService.class);

    private ImageView imageView;
    private ProgressBar progressBar;
    private TextView txvTitle, txvVoteAverage, txvGenre, txvOverview;
    private FloatingActionButton fabTrailer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setView();

        if (Constant.MOVIE_ID != null) {
            getMovieDetail( Constant.MOVIE_ID );
        } else {
            finish();
        }

    }

    private void setView () {

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        txvTitle = findViewById(R.id.txvTitle);
        txvVoteAverage = findViewById(R.id.txvVoteAverage);
        txvGenre = findViewById(R.id.txvGenre);
        txvOverview = findViewById(R.id.txvOverview);
        fabTrailer = findViewById(R.id.fabTrailer);

        fabTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DetailActivity.this, TrailerActivity.class));
            }
        });

    }

    private void getMovieDetail(String movieId) {

        showLoading( true );

        Call<DetailModel> call = service.getDetail(movieId, Constant.KEY);
        call.enqueue(new Callback<DetailModel>() {
            @Override
            public void onResponse(Call<DetailModel> call, Response<DetailModel> response) {
                Log.e(TAG, response.toString());
                showLoading( false );
                if (response.isSuccessful()) {
                    DetailModel detailModel = response.body();
                    showDetail(detailModel);
                }
            }

            @Override
            public void onFailure(Call<DetailModel> call, Throwable t) {
                Log.e(TAG, t.toString());
                showLoading( false );
            }
        });
    }

    private void showDetail(DetailModel detail){

        txvTitle.setText( detail.getTitle() );
        txvVoteAverage.setText( detail.getVote_average().toString() );
        txvOverview.setText( detail.getOverview() );

        for (DetailModel.Genres genre: detail.getGenres() ) {
            txvGenre.setText( genre.getName() + " " );
        }

        Picasso.get()
                .load(Constant.BACKDROP_PATH + detail.getBackdrop_path() )
                .placeholder(R.drawable.placeholder_portrait)
                .error(R.drawable.placeholder_portrait)
                .fit().centerCrop()
                .into(imageView);
    }

    private void showLoading(Boolean loading) {
        if (loading) {
            progressBar.setVisibility( View.VISIBLE );
            fabTrailer.hide();
        } else {
            progressBar.setVisibility( View.GONE );
            fabTrailer.show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
