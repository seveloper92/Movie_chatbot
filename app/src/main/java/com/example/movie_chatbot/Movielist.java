package com.example.movie_chatbot;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Movielist extends AppCompatActivity {

    public RecyclerView recyclerView;
    public MovieRecyclerViewAdapter adapter;
    protected static ArrayList<Movie> movieList;
    int cas;
    String search_url;
    String default_url = "https://api.themoviedb.org/3/movie/upcoming?api_key=eb9b23d5f97028e13476b7132693c728&language=ko-KR&page=1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movielist);

        //영화검색하는 툴바.
        Toolbar myToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        //리사이클러뷰 선언
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(Movielist.this, 1));
        movieList = new ArrayList<Movie>();
        adapter = new MovieRecyclerViewAdapter(Movielist.this, movieList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();



        //Asynctask - OKHttp
        MyAsyncTask mAsyncTask = new MyAsyncTask();
        mAsyncTask.execute(default_url);
        Log.e("Asynctask","온크리에이트값"+default_url);


    }


    //영화검색 툴바 코드.
    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("제목을 입력하세요.");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            //검색어를 다 입력하고 서치 버튼을 눌렀을때
            @Override
            public boolean onQueryTextSubmit(String Search) {

                //티비프로그램 선택
                if (cas==1){
                    search_url = "https://api.themoviedb.org/3/tv/popular?api_key=eb9b23d5f97028e13476b7132693c728&query="+ Search +"&language=ko-KR&page=1";
                    Log.e("cas값"+search_url,""+cas);
                }
                //영화 선택
                else {
                    search_url = "https://api.themoviedb.org/3/search/movie?api_key=eb9b23d5f97028e13476b7132693c728&query=" + Search + "&language=ko-KR&page=1";
                    Log.e("cas값"+search_url,""+cas);
                }
                //검색값 담아줌.
                Search = searchView.toString();
                MyAsyncTask mAsyncTask = new MyAsyncTask();
                mAsyncTask.execute(search_url);
                Toast.makeText(Movielist.this, "결과를 검색합니다.", Toast.LENGTH_SHORT).show();
                Log.e("Search", "keyword: " + Search);
                //여기서 AsyncTask를 이용 검색 리퀘스트로 데이터를 받아 오게 처리 하자. - AsyncTask 공유할것.

                return false;
            }

            //검색 입력창에 새로운 텍스트가 들어갈때 마다 호출 - 여기선 필요 없음
            @Override
            public boolean onQueryTextChange(String s) {
                Log.e("Search", "keyword: " + s);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //케이스에 따라 목록이 바뀜.
        switch(id){
            case R.id.action_tv:
                cas= 1;
                adapter = new MovieRecyclerViewAdapter(Movielist.this, movieList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "티비 목록을 선택하셨습니다.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_movie:
                cas= 2;
                adapter = new MovieRecyclerViewAdapter(Movielist.this, movieList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "영화 목록을 선택하셨습니다.", Toast.LENGTH_LONG).show();
                return true;
                //다이얼로그 띄어서 화면 설정 변경하는 부분.
            case R.id.action_settings:
                //다이얼로그 작성
                AlertDialog.Builder builder = new AlertDialog.Builder (Movielist.this);
                //다이얼로그 xml을 불러 그 xml에 넣어줄 에디터들을 넣어준다.
                View mView = getLayoutInflater ().inflate (R.layout.movie_dialog, null);
                TextView one = (TextView) mView.findViewById (R.id.one);
                TextView two = (TextView) mView.findViewById (R.id.two);
                TextView three = (TextView) mView.findViewById (R.id.three);
                builder.setView (mView);
                final AlertDialog alertDialog = builder.create ();
                alertDialog.show ();
                //다이얼로그 확인 버튼 누르면
                one.setOnClickListener (new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {
                        //원래 구분값 주어서 바꾸려 했으나. 서버에 요청을 다시 해야 하므로 온크리에이트에서 열 값만 바꿔줌.
//                        movie_count = 1;
//                        adapter = new MovieRecyclerViewAdapter(Movielist.this, movieList);
//                        recyclerView.setAdapter(adapter);
//                        adapter.notifyDataSetChanged();
//                        alertDialog.dismiss();
                        recyclerView.setLayoutManager(new GridLayoutManager(Movielist.this, 1));
                        movieList = new ArrayList<Movie>();
                        adapter = new MovieRecyclerViewAdapter(Movielist.this, movieList);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        alertDialog.dismiss();

                        //Asynctask - OKHttp
                        MyAsyncTask mAsyncTask = new MyAsyncTask();
                        mAsyncTask.execute(default_url);
                        Log.e("온크리에이트값","온크리에이트값"+default_url);
                    }
                });
                two.setOnClickListener (new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {
                        recyclerView.setLayoutManager(new GridLayoutManager(Movielist.this, 2));
                        movieList = new ArrayList<Movie>();
                        adapter = new MovieRecyclerViewAdapter(Movielist.this, movieList);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        alertDialog.dismiss();

                        //Asynctask - OKHttp
                        MyAsyncTask mAsyncTask = new MyAsyncTask();
                        mAsyncTask.execute(default_url);
                        Log.e("온크리에이트값","온크리에이트값"+default_url);
                    }
                });
                three.setOnClickListener (new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {
                        recyclerView.setLayoutManager(new GridLayoutManager(Movielist.this, 3));
                        movieList = new ArrayList<Movie>();
                        adapter = new MovieRecyclerViewAdapter(Movielist.this, movieList);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        alertDialog.dismiss();

                        //Asynctask - OKHttp
                        MyAsyncTask mAsyncTask = new MyAsyncTask();
                        mAsyncTask.execute(default_url);
                        Log.e("온크리에이트값","온크리에이트값"+default_url);
                    }
                });

                return true;
            default:
                Toast.makeText(this, "모양인가", Toast.LENGTH_LONG).show();
                Intent intro_intent = new Intent(Movielist.this, Ai_chat.class);
                startActivity (intro_intent);
                //액티비티 전환 부드럽게 하기
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                return super.onOptionsItemSelected(item);
        }
    }

    public class MyAsyncTask extends AsyncTask<String, Void, Movie[]> {
        //로딩중 표시
        ProgressDialog progressDialog = new ProgressDialog(Movielist.this);

        @Override
        public void onPreExecute() {
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
            progressDialog.setMessage("로딩중...");
            //show dialog
            progressDialog.show();

            movieList.clear();
            super.onPreExecute();
        }

        @Override
        public Movie[] doInBackground(String... strings) {
            Log.d("AsyncTask", "url : " + strings[0]);
            OkHttpClient client = new OkHttpClient();
            //전역변수 request를 가져와서 순위권 차트를 넣는다.
            //넣을때 배열에 넣어서 execute할때 배열에 있는 0번째 값을 가져온다.
            Request request = new Request.Builder()
                    .url(strings[0])
                    .build();

//            Request request = new Request.Builder()
//                    .url("https://api.themoviedb.org/3/movie/upcoming?api_key=eb9b23d5f97028e13476b7132693c728&language=ko-KR&page=1")
//                    .build();
            try {
                Log.e("데이터 가져옴","ㅎ");
                Response response = client.newCall(request).execute();
                Gson gson = new GsonBuilder().create();
                JsonParser parser = new JsonParser();
                JsonElement rootObject = parser.parse(response.body().charStream())
                        .getAsJsonObject().get("results");
                //가져온 데이터를 무비클래스 오브젝트에 넣기.
                Movie[] posts = gson.fromJson(rootObject, Movie[].class);
                return posts;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(Movielist.this, "안댐", Toast.LENGTH_SHORT).show();
            }
            return null;
        }


        @Override
        public void onPostExecute(Movie[] result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            //ArrayList에 차례대로 집어 넣는다.
            if(result.length > 0){
                for(Movie p : result){
                    movieList.add(p);
                }
            }
            //어답터 설정
            adapter = new MovieRecyclerViewAdapter(Movielist.this, movieList);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }

    }
            //목록 배열 초기화

}
