package com.example.movie_chatbot;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.movie_chatbot.AI_items.AiResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Ai_chat extends AppCompatActivity implements ServerInterworking.ReportPlayStatus {
    private static final String STORY_ID = "API_KEY"; //챗봇 플랫폼 키값
    private static final String AI_SERVER_URL = "http://mindmap.ai:8000/v1/" + STORY_ID; //챗봇주소
    private static final String TAG = "debug";//디버그 태그http://mindmap.ai:8000/v1/
    TextView nictext;
    EditText dataView;
    private ServerInterworking mSi;
    private int status = 0;
    private static AiResult aiResult;
    private static final int RESULT_SPEECH = 1;
    TextToSpeech tts;
    Handler mHandler = new Handler ();
    String API_KEY = "API_KEY";
    static String question = "무비봇의 추천 영화입니다.";
    String default_url = "https://api.themoviedb.org/3/movie/upcoming?api_key="+API_KEY+"&language=ko-KR&page=1";
    static String myname;
    //리사이클러뷰 넣어주는거
    static ArrayList<Movie> movieList = new ArrayList<>();
    ArrayList<botList> data = new ArrayList<>();
    RecyclerView recyclerView; //리사이클러뷰
    LinearLayoutManager layoutManager; //리사이클러뷰에서 필요한 레이아웃 매니저
    botAdapter botAdapter; //어뎁터
    Context context;
    @RequiresApi(api = Build.VERSION_CODES.DONUT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_aichat);
        dataView = (EditText) findViewById (R.id.data);
        //리싸이클러뷰
        myname = "user";
        //리사이클러뷰 선언
        recyclerView = (RecyclerView) findViewById(R.id.botrecycler);
        layoutManager = new LinearLayoutManager (getApplicationContext ());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL); //아이템이 어떻게 나열될지 선택 vertical or horizental
        recyclerView.setLayoutManager(layoutManager); //레이아웃 매니저 연결
        //어댑터 선언.
        botAdapter = new botAdapter (data);
        recyclerView.setAdapter (botAdapter);
        movieList = new ArrayList<Movie>();

        MyAsyncTask mAsyncTask = new MyAsyncTask();
        mAsyncTask.execute(default_url);

        //키보드 위로
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //텍스트 리스닝.
        tts = new TextToSpeech (getApplicationContext (), new TextToSpeech.OnInitListener () {
            @Override
            public void onInit(int state) {
                if (state != TextToSpeech.ERROR) {
                    tts.setLanguage (Locale.KOREAN);
                    Log.e("지금", "텍스트 읽어주고이:ㅆ어 ");
                }
            }
        });

        dataView = (EditText) findViewById (R.id.data);
        //작성한 텍스트를 올려준다.
        Button button = (Button) findViewById (R.id.button);
        button.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {

                if (dataView.getText ().length () > 0) {
                    String r = dataView.getText ().toString ();
                    status++;
                    data.add (new botList (myname,r,null));
                    chatBotInterworking (AI_SERVER_URL, r);
                    Log.e ("키보드","입력했을때"+r);
                }
            }
        });

        //음성인식 버튼
        Button sttBtn = (Button) findViewById (R.id.sttBtn);
        sttBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (RecognizerIntent.ACTION_RECOGNIZE_SPEECH);  // Intent 생성
                intent.putExtra (RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName ());  // 호출할 패키지
                intent.putExtra (RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");  // 인식할 언어 설정
                intent.putExtra (RecognizerIntent.EXTRA_PROMPT, "말해주세요");
                Toast.makeText (Ai_chat.this, "음성인식 실행", Toast.LENGTH_SHORT).show ();

                try {
                    startActivityForResult (intent, RESULT_SPEECH);
                    Log.e ("키보드","지금은 음성인식기능이 동작중");
                } catch (ActivityNotFoundException e) {
                    Toast.makeText (Ai_chat.this, "Speech To Text를 지원하지 않습니다.", Toast.LENGTH_SHORT).show ();
                    e.getStackTrace ();
                }
            }
        });

        // Handler를 사용하여 1초 후에 실행 (이렇게 하지 않으면 결과가 음성으로 출력되지 않음)
        mHandler.postDelayed (new Runnable () {
            @Override
            public void run() {
                Log.e ("핸들러","들어왔나");
                mSi = new ServerInterworking (Ai_chat.this);
                mSi.registerCallbackPlay (Ai_chat.this);
                status = 1;
                String initialQuery = makeRestApiRequestData ("");
                Log.i (TAG, "query = " + initialQuery);
                mSi.reportPlayStateToServer (AI_SERVER_URL, initialQuery);
            }
        }, 1000);


        //챗봇서버요청.
        mSi = new ServerInterworking (this);
        mSi.registerCallbackPlay (this);
        status = 1;
        String initialQuery = makeRestApiRequestData ("");
        Log.i (TAG, "query = " + initialQuery);

    }
    //리사이클러뷰 아이템.
    public class botList {
        public  String bot;
        public String use;
        public String api;

        public String getbot() {
            return bot;
        }

        public String getUse() {
            return use;
        }

        public botList(String bot, String use, String api){
            this.bot = bot;
            this.use = use;
            this.api = api;
        }

    }


    //뷰홀더.
    public static class botViewHolder extends RecyclerView.ViewHolder{
        TextView  use;
        ImageView bot;
        static ImageView movieimg;
        LinearLayout linearLayout;
        LinearLayout linearLayoutmain;

        public botViewHolder(View itemView) {
            super(itemView);

            bot = (ImageView) itemView.findViewById(R.id.botchat);
            movieimg = (ImageView) itemView.findViewById (R.id.movieimg);
            use = (TextView) itemView.findViewById(R.id.usechat);
            linearLayout = (LinearLayout) itemView.findViewById (R.id.botmaglayout);
            linearLayoutmain = (LinearLayout)itemView.findViewById (R.id.botlinear);
        }
    }//뷰홀더 끝

    //어댑터 시작
    //바인드 뷰홀더= 뷰홀더를 데이터와 바인딩 시킬때 어떻게 할 것이냐고 묻는다.
    //여기서 데이터를 추가하고 추가한 데이터들을 컨트롤 하기위해서 어레이리스트만든다.
    //아이템을 추가하는 메소드를 만드는 add
    //데이터가 거쳐가는곳이라고 생각하면 좋다. 이는 어댑터에 쌓여있어야 이것들을 리스트에 뿌려 줄 수있기때문
    public class botAdapter extends RecyclerView.Adapter{

        ArrayList<botList> data;
        botAdapter(ArrayList<botList> data){
            this.data = data;
        }



        //뷰홀더를 어떻게 생성할 것인가 불어보는 부분 객체만들어주고 리턴해주면됨
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.chatbot_item, parent, false);
            return new botViewHolder (item);
        }//뷰홀더 끝


        //바인드 뷰홀더= 뷰홀더를 데이터와 바인딩 시킬때 어떻게 할 것이냐고 묻는다.
        //여기서 데이터를 추가하고 추가한 데이터들을 컨트롤 하기위해서 어레이리스트만든다.
        //아이템을 추가하는 메소드를 만드는 부분 여기선 add로 하겠다.
        //데이터가 거쳐가는곳이라고 생각하면 좋다. 이는 어댑터에 쌓여있어야 이것들을 리스트에 뿌려 줄 수있기때문
        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            botList botList = data.get(position);

            botViewHolder item = (botViewHolder) holder;
            //아까 만들어준 메소드를 넣어줌.
            //나인패치 적용하는 부분 내가 받은 메세지는 왼쪽에 내 메세지는 오른쪽에.
            Log.e ("onBindViewHolder data",""+data);

            //내가보는메세지
            if (myname.equals (data.get (position).bot)){
                item.use.setText(botList.getUse ());

                item.use.setBackgroundResource (R.drawable.ms2);
                item.linearLayout.setVisibility (View.INVISIBLE);
                //item.use.setTextSize (25);
                item.linearLayoutmain.setGravity (Gravity.RIGHT);
                Log.e ("내가보는메세지 data",""+data);
                //챗봇이 보는 메세지
            }
            else if (question.equals (data.get (position).use)) {

                //Glide사용해서 이미지 넣기.
                Glide.with(Ai_chat.this)
                        .load("https://image.tmdb.org/t/p/w500"+movieList.get(0).getPoster_path())
                        .override(200,320)
                        .into(item.movieimg);
                //영화 정보 불러오기.
                item.use.setText(botList.getUse() + "\n" + movieList.get(0).getTitle()
                        + "\n" + movieList.get(0).getOriginal_title()
                        + "\n" + movieList.get(0).getRelease_date()
                        + "\n" + movieList.get(0).getOverview());
//                데이터 로그 확인.
//                Log.e("movieList", " : " +movieList.get(0).getTitle());
//                Log.e("movieList", " : " +movieList.get(0).getOriginal_title());
//                Log.e("movieList", " : " +movieList.get(0).getRelease_date());

                Log.e("movieList", " : " +movieList.get(0).getPoster_path());
                item.use.setBackgroundResource(R.drawable.ms1);
                item.linearLayout.setVisibility(View.VISIBLE);
                // item.use.setTextSize (25);
                item.linearLayoutmain.setGravity(Gravity.LEFT);
            }
            else {
                item.use.setText (botList.getUse ());
                item.use.setBackgroundResource (R.drawable.ms1);
                item.linearLayout.setVisibility (View.VISIBLE);
                // item.use.setTextSize (25);
                item.linearLayoutmain.setGravity (Gravity.LEFT);

            }

            //챗봇이 추천한 이미지 클릭시 detailActivity로 데이터 전송 인텐트.
            botViewHolder.movieimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Ai_chat.this, DetailActivity.class);
                    intent.putExtra("id", movieList.get(0).getId());
                    intent.putExtra("title", movieList.get(0).getTitle());
                    intent.putExtra("original_title", movieList.get(0).getOriginal_title());
                    intent.putExtra("poster_path", movieList.get(0).getPoster_path());
                    intent.putExtra("overview", movieList.get(0).getOverview());
                    intent.putExtra("release_date", movieList.get(0).getRelease_date());
                    intent.putExtra("backdrop_path", movieList.get(0).getBackdrop_path());
                    startActivity(intent);
                    Log.e("Adapter", "Clcked: ");
                }
            });
        }



        @Override
        public int getItemCount() {
            return data.size();
        }
    }
    //어댑터끝


    private void chatBotInterworking(String aiServerUrl, String data) {
        String initialQuery = makeRestApiRequestData (data);
        Log.i (TAG, "query = " + initialQuery);
        mSi.reportPlayStateToServer (AI_SERVER_URL, initialQuery);
    }

    //챗봇 서버에서 받는 내용
    @Override
    public void callbackReturnReportPlayStatus(String result) {
        Log.e (TAG+"왜 일까", result);
        storeResult (result);
        String viewResult = aiResult.getOutput ().getText ().get (0);
        dataView.setText("");
        data.add (new botList ("",viewResult,null));
        botAdapter.notifyDataSetChanged ();
        Log.e ("콜백 리턴", result);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21 (viewResult);
            Log.e ("콜백리턴 안", result);


        } else {
            ttsUnder20 (viewResult);
        }
    }


    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<> ();
        map.put (TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak (text, TextToSpeech.QUEUE_FLUSH, map);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId = this.hashCode () + "";
        tts.speak (text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    private void storeResult(String result) {
        Log.e ("storeResult 값 확인.", String.valueOf (aiResult));
        Gson gson = new Gson ();
        aiResult = gson.fromJson (result, AiResult.class);
    }

    //키값에 연결된 uri도 가져와야 한다.
    private String makeRestApiRequestData(String s) {
        String request = null;
        switch (status) {
            case 1:
                //키 번호 확인.
                request = "{\"story_id\":\"STORY_ID\",\"context\":{},\"input\":{}}";
                break;
            default:
                JSONObject rootObj = new JSONObject ();
                try {
                    rootObj.put ("story_id", aiResult.getStory_id ());
                    JSONObject context = new JSONObject ();
                    context.put ("random", aiResult.getContext ().isRandom ());
                    context.put ("input_field", JSONObject.NULL);
                    JSONObject information = new JSONObject ();
                    information.put ("conversation_counter", aiResult.getContext ().getInformation ().getConversation_counter ());
                    information.put ("user_request_counter", aiResult.getContext ().getInformation ().getUser_request_counter ());
                    JSONObject conversation_stackObj = new JSONObject ();
                    conversation_stackObj.put ("conversation_node", aiResult.getContext ().getInformation ().getConversation_stack ().get (0).getConversation_node ());
                    conversation_stackObj.put ("conversation_node_name", aiResult.getContext ().getInformation ().getConversation_stack ().get (0).getConversation_node_name ());
                    JSONArray conversation_stackArray = new JSONArray ();
                    conversation_stackArray.put (conversation_stackObj);
                    information.put ("conversation_stack", conversation_stackArray);
                    context.put ("information", information);
                    context.put ("visit_counter", aiResult.getContext ().getVisit_counter ());
                    context.put ("retrieve_field", aiResult.getContext ().isRetrieve_field ());
                    context.put ("conversation_id", aiResult.getContext ().getConversation_id ());
                    context.put ("variables", JSONObject.NULL);
                    context.put ("reprompt", aiResult.getContext ().isReprompt ());
                    context.put ("keyboard", JSONObject.NULL);
                    context.put ("message", JSONObject.NULL);
                    rootObj.put ("context", context);
                    JSONObject inputObj = new JSONObject ();
                    inputObj.put ("text", s);
                    rootObj.put ("input", inputObj);
                    request = rootObj.toString ();
                } catch (JSONException e) {
                    e.printStackTrace ();
                }
                break;
        }
        return request;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data3) {
        super.onActivityResult (requestCode, resultCode, data3);
        if (resultCode == RESULT_OK && requestCode == RESULT_SPEECH) {
            // 음성인식 결과
            ArrayList<String> sstResult = data3.getStringArrayListExtra (RecognizerIntent.EXTRA_RESULTS);
            // 결과들 중 음성과 가장 유사한 단어부터 시작되는 0번째 문자열을 저장
            String result_sst = sstResult.get (0);
            status++;
            data.add (new botList (myname,result_sst,null)); //음성인식내용을 받는다
            botAdapter.notifyDataSetChanged ();

            chatBotInterworking (AI_SERVER_URL, result_sst);

        }
    }
    public class MyAsyncTask extends AsyncTask<String, Void, Movie[]> {
        //영화 정보 가져오는 MyAsyncTask

        @Override
        public void onPreExecute() {
            movieList.clear();
            super.onPreExecute();
        }

        @Override
        public Movie[] doInBackground(String... strings) {
            Log.d("AsyncTask", "url : " + strings[0]);
            OkHttpClient client = new OkHttpClient();
            //request를 가져와서 순위권 차트를 넣는다.
            //넣을때 배열에 넣어서 execute할때 배열에 있는 0번째 값을 가져온다.
            Request request = new Request.Builder()
                    .url(strings[0])
                    .build();


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
                Toast.makeText(Ai_chat.this, "데이터 가져오기 실패", Toast.LENGTH_SHORT).show();
            }
            return null;
        }


        @Override
        public void onPostExecute(Movie[] result) {
            super.onPostExecute(result);

            //ArrayList에 차례대로 집어 넣는다.
            if(result.length > 0){
                for(Movie p : result){
                    movieList.add(p);
                }
            }
            //랜덤으로 섞어준다.
            Collections.shuffle(movieList);
            // 1개 추출하고
            //값 확인.
//            Log.e("movieList", " : " +movieList.get(0).getId());
//            Log.e("movieList", " : " +movieList.get(0).getTitle());
//            Log.e("movieList", " : " +movieList.get(0).getOriginal_title());

        }

    }






}
