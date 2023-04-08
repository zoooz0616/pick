package com.example.choice;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class Intro extends AppCompatActivity {

    Handler handler = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() { //자동로그인

            Intent intent;
            //저장된 사용자 정보가 없다면 로그인 화면으로 이동
            if(SaveSharedPreferences.getUserEmail(Intro.this).length() == 0 || SaveSharedPreferences.getUserPwd(Intro.this).length() == 0) {
                intent = new Intent(Intro.this, Welcome.class);
            } else { //저장된 사용자 정보가 있다면 메인 화면으로 이동
                intent = new Intent(Intro.this, MainActivity.class);
            }
            startActivity(intent);
            finish(); // Activity 화면 제거
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);
    }

    @Override
    protected void onResume() {
        super.onResume();
    // 다시 화면에 들어왔을 때 예약 걸어주기
        handler.postDelayed(r, 2000); // 2초 뒤에 Runnable 객체 수행
    }

    @Override
    protected void onPause() {
        super.onPause();
    // 화면 벗어나면, handler에 예약해놓은 작업 취소
        handler.removeCallbacks(r); // 예약 취소
    }
}