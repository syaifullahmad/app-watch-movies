package com.lazday.youtubeplayer.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lazday.youtubeplayer.R;
import com.lazday.youtubeplayer.adapter.MainAdapter;
import com.lazday.youtubeplayer.model.MovieModel;
import com.lazday.youtubeplayer.retrofit.RetrofitInstance;
import com.lazday.youtubeplayer.retrofit.MovieService;
import com.lazday.youtubeplayer.retrofit.Constant;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressBar progressBar, progressBarNextPage;
    private NestedScrollView scrollView;

    private MovieService service = RetrofitInstance.getUrl().create(MovieService.class);
    private MainAdapter adapter;
    private List<MovieModel.Results> movies = new ArrayList<>();

    private int currentPage = 1;
    private int totalPages  = 0;
    private boolean isScrolling  = false;
    private String movieCategory  = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupView();
        setupListener();
        setupRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (movieCategory.equals("")) {
            movieCategory = Constant.POPULAR;
            getMovie();
            showLoadingNextPage(false);
        }
    }

    private void setupView() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        progressBarNextPage = findViewById(R.id.progressBarNextPage);
        scrollView = findViewById(R.id.scrollView);
    }

    private void setupListener(){

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    if (!isScrolling) {
                        if (currentPage <= totalPages) {
                            getMovieNextPage();
                        }

                    }
                }
            }
        });
    }

    private void setupRecyclerView(){

        adapter = new MainAdapter(this, movies, new MainAdapter.AdapterListener() {
            @Override
            public void onClick() {
                startActivity(new Intent(MainActivity.this, DetailActivity.class));
            }
        });

        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter( adapter );
    }

    private void getMovie() {

        scrollView.scrollTo(0, 0);
        currentPage = 1;
        showLoading( true );

        Call<MovieModel> call = null;
        switch (movieCategory) {
            case Constant.POPULAR:
                call = service.getPopular(Constant.KEY, Constant.LANGUAGE,
                        String.valueOf(currentPage) );
                break;
            case Constant.NOW_PLAYING:
                call = service.getNowPlaying(Constant.KEY, Constant.LANGUAGE,
                        String.valueOf(currentPage) );
                break;
        }

        call.enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                Log.d(TAG, response.toString());
                showLoading( false );
                if (response.isSuccessful()) {
                    MovieModel movie = response.body();
                    showMovie( movie );
                }
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {
                Log.d(TAG, t.toString());
                showLoading( false );
            }
        });

    }

    private void getMovieNextPage() {

        currentPage += 1;
        showLoadingNextPage( true );

        Call<MovieModel> call = null;
        switch (movieCategory) {
            case Constant.POPULAR:
                call = service.getPopular(Constant.KEY, Constant.LANGUAGE,
                        String.valueOf(currentPage) );
                break;
            case Constant.NOW_PLAYING:
                call = service.getNowPlaying(Constant.KEY, Constant.LANGUAGE,
                        String.valueOf(currentPage) );
                break;
        }

        call.enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                Log.d(TAG, response.toString());
                showLoadingNextPage( false );
                if (response.isSuccessful()) {
                    MovieModel movie = response.body();
                    showMovieNextPage( movie );
                }
            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {
                Log.d(TAG, t.toString());
                showLoadingNextPage( false );
            }
        });

    }

    private void showLoading(Boolean loading) {
        if (loading) {
            progressBar.setVisibility( View.VISIBLE );
        } else {
            progressBar.setVisibility( View.GONE );
        }
    }

    private void showLoadingNextPage(Boolean loading) {
        if (loading) {
            isScrolling = true;
            progressBarNextPage.setVisibility( View.VISIBLE );
        } else {
            isScrolling = false;
            progressBarNextPage.setVisibility( View.GONE );
        }
    }

    private void showMovie(MovieModel movie) {
        totalPages = movie.getTotal_pages();
        movies = movie.getResults();
        adapter.setData(movies);
    }

    private void showMovieNextPage(MovieModel movie) {
        totalPages = movie.getTotal_pages();
        movies = movie.getResults();
        adapter.setDataNextPage(movies);
        showMessage( "Page " + currentPage + " loaded");
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_popular) {

            getSupportActionBar().setTitle("Popular");
            movieCategory = Constant.POPULAR;
            getMovie();

            return true;

        } else if (id == R.id.action_now_playing) {

            getSupportActionBar().setTitle("Now Playing");
            movieCategory = Constant.NOW_PLAYING;
            getMovie();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
