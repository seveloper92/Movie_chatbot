package com.example.movie_chatbot;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.RecyclerViewHolders> {

    private ArrayList<Movie> mMovieList;
    private LayoutInflater mInflate;
    private Context mContext;

    //constructor
    //레이아웃 인플레이터를 이용해서 영화 목록만큼 뷰에 뿌려준다.
    public MovieRecyclerViewAdapter(Context context, ArrayList<Movie> itemList) {
        this.mContext = context;
        this.mInflate = LayoutInflater.from(context);
        this.mMovieList = itemList;
    }


    @Override
    //레이아웃 화면을 어댑터에 사용 가능 하도록 해주는 뷰를 생성한다. 기능적인것.
    //영화 목록에서 가져온 정보를 배치한다.
    public RecyclerViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflate.inflate(R.layout.list_item, parent, false);
        RecyclerViewHolders viewHolder = new RecyclerViewHolders(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolders holder, final int position) {
        //해당 주소에서 포스터만 가져온다.
        String url = "https://image.tmdb.org/t/p/w500" + mMovieList.get(position).getPoster_path();
        Log.e("uri값", ": " + url);
        Glide.with(mContext)
                .load(url)
                .into(holder.imageView);



        //각 아이템 클릭 이벤트
        //인플레이터로 각정보 뿌려 줫으니 뷰에있는 정보를 가지고 들어간다.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("id", mMovieList.get(position).getId());
                intent.putExtra("title", mMovieList.get(position).getTitle());
                intent.putExtra("original_title", mMovieList.get(position).getOriginal_title());
                intent.putExtra("poster_path", mMovieList.get(position).getPoster_path());
                intent.putExtra("overview", mMovieList.get(position).getOverview());
                intent.putExtra("release_date", mMovieList.get(position).getRelease_date());
                intent.putExtra("backdrop_path", mMovieList.get(position).getBackdrop_path());
                mContext.startActivity(intent);
                Log.e("Adapter", "Clcked: " + position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return this.mMovieList.size();
    }


    //뷰홀더 - 따로 클래스 파일로 만들어도 된다.
    public static class RecyclerViewHolders extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public RecyclerViewHolders(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.listimageView);
        }
    }

}
