package com.example.movie_chatbot;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailActivity extends YouTubeBaseActivity {

    ArrayList<Youtube> youtubeList;
    String API_KEY = "API_KEY";
    private YouTubePlayerView youTubeView;
    private String trailer01;
    private String m_id;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        youtubeList = new ArrayList<Youtube>();

        Intent intent = getIntent();
        m_id = intent.getStringExtra("id");
        final String title = intent.getStringExtra("title");
        final String original_title = intent.getStringExtra("original_title");
        String poster_path = intent.getStringExtra("poster_path");
        Log.e("포스터 데이터",""+poster_path);
        String overview = intent.getStringExtra("overview");
        String release_date = intent.getStringExtra("release_date");
        TextView textView_title = (TextView)findViewById(R.id.tv_title);
        textView_title.setText(title);
        TextView textView_original_title = (TextView)findViewById(R.id.tv_original_title);
        textView_original_title.setText(original_title);
        ImageView imageView_poster = (ImageView) findViewById(R.id.iv_poster);
        Glide.with(mContext)
                .load(poster_path)
                .apply(new RequestOptions()
                .fitCenter())
                .into(imageView_poster);

        TextView textView_overview = (TextView)findViewById(R.id.tv_overview);
        textView_overview.setText(overview);
        TextView textView_release_date = (TextView)findViewById(R.id.tv_release_date);
        textView_release_date.setText(release_date);

        //Asynctask
        YoutubeAsyncTask mProcessTask = new YoutubeAsyncTask();
        mProcessTask.execute(m_id);

//movie search button
        Button bt_search = (Button) findViewById(R.id.bt_gg);
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //웹브라우저를 열어서 검색한다.
                String term = original_title +" "+title + "후기";
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, term);
                startActivity(intent);
            }
        });

        Button bt_jamak = (Button) findViewById(R.id.bt_cgv);
        bt_jamak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 버튼 이벤트 내 Intent를 생성하여 Intent.ACTION_VIEW 액션을 취해준 뒤, url을 넣어줌
                Intent intent = new Intent (Intent.ACTION_VIEW,Uri.parse("http://www.cgv.co.kr/ticket/"));
                startActivity(intent);
            }
        });



    }

    public void playVideo(final String videoId, YouTubePlayerView youTubePlayerView) {
        //initialize youtube player view
        Log.d("Youtube", "trailer: " + videoId);
        youTubePlayerView.initialize(API_KEY,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.cueVideo(videoId);

                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });
    }

    @SuppressLint("StaticFieldLeak")
    public class YoutubeAsyncTask extends AsyncTask<String, Void, Youtube[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Youtube[] youtubes) {
            super.onPostExecute(youtubes);

            //ArrayList에 차례대로 집어 넣는다.
            if(youtubes.length > 0){
                youtubeList.addAll(Arrays.asList(youtubes));

                //유튜브뷰어를 이용 화면에 출력하자.
                trailer01 = youtubeList.get(0).getKey();
                Log.d("Youtube", "trailer : " + trailer01);
                youTubeView = findViewById(R.id.youtube_view);
                playVideo(trailer01, youTubeView);

            }
        }

        @Override
        protected Youtube[] doInBackground(String... strings) {
            String m_id = strings[0];

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    // 영화api키 넣는곳.
                    //지금 m_id가 비어있어서 여기에 값을 넣어줘야한다.
                    .url("https://api.themoviedb.org/3/movie/"+m_id+"/videos?api_key="+API_KEY)
                    .build();
            Log.e("주소","멀까"+request);
            try {
                Response response = client.newCall(request).execute();
                Gson gson = new GsonBuilder().create();
                JsonParser parser = new JsonParser();
                JsonElement rootObject = parser.parse(response.body().charStream())
                        .getAsJsonObject().get("results");
                Youtube[] posts = gson.fromJson(rootObject, Youtube[].class);
                return posts;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
