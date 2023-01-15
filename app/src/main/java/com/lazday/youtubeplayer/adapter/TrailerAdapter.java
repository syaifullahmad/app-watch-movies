package com.lazday.youtubeplayer.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lazday.youtubeplayer.R;
import com.lazday.youtubeplayer.model.TrailerModel;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {
    private final String TAG = "TrailerAdapter";

    private List<TrailerModel.Results> results;
    private Context context;
    private OnAdapterListener listener;

    public TrailerAdapter(Context context, List<TrailerModel.Results> results, OnAdapterListener listener) {
        this.results    = results ;
        this.context    = context ;
        this.listener   = listener ;
    }

    @NonNull
    @Override
    public TrailerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_trailer,
                        parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull final TrailerAdapter.ViewHolder viewHolder, int i) {
        final TrailerModel.Results result = results.get(i);
        viewHolder.textView.setText( result.getName());

        viewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnClick( result.getKey() );
            }
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }

    public void setData(List<TrailerModel.Results> newResults){
        results.clear();
        results.addAll(newResults);
        notifyDataSetChanged();
        listener.OnVideo( results.get(0).getKey() );
    }

    public interface OnAdapterListener {
        void OnClick(String key);
        void OnVideo(String key);
    }
}
