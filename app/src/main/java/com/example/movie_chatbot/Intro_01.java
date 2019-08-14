package com.example.movie_chatbot;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.movie_chatbot.databinding.ActivityIntro01Binding;

public class Intro_01 extends AppCompatActivity {

    //데이터 바인딩
    //이 클래스가 로그인클래스라 로그인 바인딩이며 다른 클래스는 바인딩이름이 달라짐.
    ActivityIntro01Binding Binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_01);

        //이렇게 소환해주면 사용할 때 모든 버스턴을 그냥 setOnClickLisner처럼 바로 불러와 사용 할 수 있다.
        Binding = DataBindingUtil.setContentView(this,R.layout.activity_intro_01);
        Binding.setIntroActivity(this);

//        //액션바 감추기
//        ActionBar actionBar =getSupportActionBar ();
//        actionBar.hide ();

        //상태바 없애기.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //핸들러로 2초뒤 액티비티 꺼지기
        Handler intro_handler = new Handler ();
        intro_handler.postDelayed (new Runnable () {
            @Override
            public void run() {
                Intent intro_intent = new Intent (Intro_01.this, Movielist.class);
                startActivity (intro_intent);
                //액티비티 전환 부드럽게 하기
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        },3000);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.dropstar);
        Binding.imageView1.startAnimation(animation);
        Binding.imageView2.startAnimation(animation);
        Binding.imageView3.startAnimation(animation);
        Binding.imageView4.startAnimation(animation);
        Binding.imageView5.startAnimation(animation);
        Binding.imageView7.startAnimation(animation);
        Binding.imageView8.startAnimation(animation);
        Binding.imageView9.startAnimation(animation);
        Binding.imageView10.startAnimation(animation);
        Binding.imageView11.startAnimation(animation);
        Binding.imageView12.startAnimation(animation);
    }//OnCreateimageView6
}
