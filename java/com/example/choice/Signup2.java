package com.example.choice;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Signup2 extends AppCompatActivity {
    String email, password, gender, age, category;
    private int category_count, color; //category_count : 선택한 카테고리 개수 체크
    private boolean isSelected_gender, isSelected_age; //체크 여부 확인
    private Button men, women, teens, twenties, thirties, forties, fifties, fashion, interior, media, daily, food, animal, book, digital, economy, travel;
    String TAG = "SignUp2Test";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        isSelected_gender = false;
        isSelected_age = false;
        color = Color.BLACK; //버튼 텍스트 컬러
        category_count = 0;
        category = "";

        email = getIntent().getStringExtra("email");
        Log.i(TAG, "email : " + email);
        password = getIntent().getStringExtra("password");
        Log.i(TAG,"password : " + password);

        //성별
        men = findViewById(R.id.men);
        women = findViewById(R.id.women);

        //나이
        teens = findViewById(R.id.teens);
        twenties = findViewById(R.id.twenties);
        thirties = findViewById(R.id.thirties);
        forties = findViewById(R.id.forties);
        fifties = findViewById(R.id.fifties);

        //카테고리
        fashion = findViewById(R.id.fashion);
        interior = findViewById(R.id.interior);
        media = findViewById(R.id.media);
        daily = findViewById(R.id.daily);
        food = findViewById(R.id.food);
        animal = findViewById(R.id.animal);
        book = findViewById(R.id.book);
        digital = findViewById(R.id.digital);
        economy = findViewById(R.id.economy);
        travel = findViewById(R.id.travel);
    }

    public void onClick(View v){
        if(category_count < 3 || !isSelected_age || !isSelected_gender){
            Toast.makeText(Signup2.this, "모든 항목이 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
        } else{
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if (success) { // 회원등록에 성공한 경우
                            Toast.makeText(getApplicationContext(),"회원 등록에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Signup2.this, Welcome.class);
                            startActivity(intent);
                        } else { // 회원등록에 실패한 경우
                            Toast.makeText(getApplicationContext(),"회원 등록에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            String category1 = category.split(" ")[0];
            String category2 = category.split(" ")[1];
            String category3 = category.split(" ")[2];

            Log.i(TAG, "category : " + category1 + ", " + category2 + ", " + category3);

            // 서버 요청
            RegisterRequest registerRequest = new RegisterRequest(email, password, gender, age, category1, category2, category3, responseListener);
            RequestQueue queue = Volley.newRequestQueue(Signup2.this);
            queue.add(registerRequest);
        }
    }

    public void onClickGender(View v) {
        v.setBackgroundColor(ContextCompat.getColor(Signup2.this, R.color.purple));
        switch (v.getId()) {
            case R.id.women:
                gender = "F";
                women.setTextColor(color);
                break;
            case R.id.men:
                gender = "M";
                men.setTextColor(color);
        }
        isSelected_gender = true;
        women.setEnabled(false);
        men.setEnabled(false);
    }

    public void onClickAge(View v) {
        v.setBackgroundColor(ContextCompat.getColor(Signup2.this, R.color.purple));
        switch (v.getId()) {
            case R.id.teens:
                age = "10";
                teens.setTextColor(color);
                break;
            case R.id.twenties:
                age = "20";
                twenties.setTextColor(color);
                break;
            case R.id.thirties:
                age = "30";
                thirties.setTextColor(color);
                break;
            case R.id.forties:
                age = "40";
                forties.setTextColor(color);
                break;
            case R.id.fifties:
                age = "50";
                fifties.setTextColor(color);
        }
        isSelected_age = true;
        teens.setEnabled(false);
        twenties.setEnabled(false);
        thirties.setEnabled(false);
        forties.setEnabled(false);
        fifties.setEnabled(false);
    }

    public void onClickCategory(View v) {
        v.setBackgroundColor(ContextCompat.getColor(Signup2.this, R.color.purple));
        category_count++;
        if (category_count <= 3) {
            switch (v.getId()) {
                case R.id.fashion:
                    category += "fashion ";
                    fashion.setTextColor(color);
                    break;
                case R.id.interior:
                    category += "interior ";
                    interior.setTextColor(color);
                    break;
                case R.id.media:
                    category += "media ";
                    media.setTextColor(color);
                    break;
                case R.id.daily:
                    category += "daily ";
                    daily.setTextColor(color);
                    break;
                case R.id.food:
                    category += "food ";
                    food.setTextColor(color);
                    break;
                case R.id.animal:
                    category += "animal ";
                    animal.setTextColor(color);
                    break;
                case R.id.book:
                    category += "book ";
                    book.setTextColor(color);
                    break;
                case R.id.digital:
                    category += "digital ";
                    digital.setTextColor(color);
                    break;
                case R.id.economy:
                    category += "economy ";
                    economy.setTextColor(color);
                    break;
                case R.id.travel:
                    category += "travel ";
                    travel.setTextColor(color);
                    break;
            }
        }
        if (category_count == 3) {
            fashion.setEnabled(false);
            interior.setEnabled(false);
            media.setEnabled(false);
            daily.setEnabled(false);
            food.setEnabled(false);
            animal.setEnabled(false);
            book.setEnabled(false);
            digital.setEnabled(false);
            economy.setEnabled(false);
            travel.setEnabled(false);
        }
    }
}