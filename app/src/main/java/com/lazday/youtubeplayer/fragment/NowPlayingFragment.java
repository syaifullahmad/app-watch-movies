package com.lazday.youtubeplayer.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.lazday.youtubeplayer.R;
import com.lazday.youtubeplayer.activity.DetailActivity;
import com.lazday.youtubeplayer.adapter.MainAdapter;
import com.lazday.youtubeplayer.databinding.FragmentNowPlayingBinding;
import com.lazday.youtubeplayer.model.MovieModel;
import com.lazday.youtubeplayer.retrofit.RetrofitInstance;
import com.lazday.youtubeplayer.retrofit.MovieService;
import com.lazday.youtubeplayer.retrofit.Constant;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class NowPlayingFragment extends Fragment {
    private static final String TAG = "PopularFragment";

    private FragmentNowPlayingBinding binding;

    MovieService service = RetrofitInstance.getUrl().create(MovieService.class);
    private List<MovieModel.Results> movies = new ArrayList<>();
    private MainAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);
        binding = FragmentNowPlayingBinding.inflate(inflater, container, false);
        setupRecyclerView();
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        getMovieNowPlaying();
    }

    private void setupRecyclerView() {

        adapter = new MainAdapter(getContext(), movies, new MainAdapter.AdapterListener() {
            @Override
            public void onClick() {
                startActivity(new Intent(getContext(), DetailActivity.class));
            }
        });

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter( adapter );
    }

    private void getMovieNowPlaying(){

        showLoading(true);

        Call<MovieModel> call = service.getNowPlaying(Constant.KEY, Constant.LANGUAGE, "1");
        call.enqueue(new Callback<MovieModel>() {
            @Override
            public void onResponse(Call<MovieModel> call, Response<MovieModel> response) {
                showLoading( false );
                if (response.isSuccessful()) {
                    MovieModel movie = response.body();
                    showMovie( movie );
                }

            }

            @Override
            public void onFailure(Call<MovieModel> call, Throwable t) {
                showLoading( false );
                Log.d(TAG, t.toString());
            }
        });
    }

    private void showMovie(MovieModel movie) {
        movies = movie.getResults();
        adapter.setData(movies);
    }

    private void showLoading(Boolean loading) {
        if (loading) {
            binding.progressBar.setVisibility( View.VISIBLE );
        } else {
            binding.progressBar.setVisibility( View.GONE );
        }
    }
}
