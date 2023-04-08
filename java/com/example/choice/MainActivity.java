package com.example.choice;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    //하단 네비게이션 뷰(메뉴 선택에 따라 맞는 프래그먼트 보여줌)
    Menu menu;
    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Home home;
    private Post post;
    private Notice notice;
    private My my;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //네비게이션 이벤트
        bottomNavigationView = findViewById(R.id.bottomNavi); // activity_main안의 네비게이션 뷰의 id가 bottomNavi.
        menu=bottomNavigationView.getMenu(); //select/unselect 아이콘 다르게 하기 위해서 선언.
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.action_home:
                        menuItem.setIcon(R.drawable.home_2);
                        menu.findItem(R.id.action_post).setIcon(R.drawable.post_1);
                        menu.findItem(R.id.action_notice).setIcon(R.drawable.notice_1);
                        menu.findItem(R.id.action_my).setIcon(R.drawable.my_1);
                        setFrag(0);
                        break;
                    case R.id.action_post:
                        menuItem.setIcon(R.drawable.post_2);
                        menu.findItem(R.id.action_home).setIcon(R.drawable.home_1);
                        menu.findItem(R.id.action_notice).setIcon(R.drawable.notice_1);
                        menu.findItem(R.id.action_my).setIcon(R.drawable.my_1);
                        setFrag(1);
                        break;
                    case R.id.action_notice:
                        menuItem.setIcon(R.drawable.notice_2);
                        menu.findItem(R.id.action_home).setIcon(R.drawable.home_1);
                        menu.findItem(R.id.action_post).setIcon(R.drawable.post_1);
                        menu.findItem(R.id.action_my).setIcon(R.drawable.my_1);
                        setFrag(2);
                        break;
                    case R.id.action_my:
                        menuItem.setIcon(R.drawable.my_2);
                        menu.findItem(R.id.action_home).setIcon(R.drawable.home_1);
                        menu.findItem(R.id.action_post).setIcon(R.drawable.post_1);
                        menu.findItem(R.id.action_notice).setIcon(R.drawable.notice_1);
                        setFrag(3);
                        break;
                }
                return true;
            }
        });

        home=new Home();
        post=new Post();
        notice=new Notice();
        my=new My();
        setFrag(0); // 첫 프래그먼트 화면 지정(default=home)
    }

    // 프레그먼트 교체
    private void setFrag(int n)
    {
        fm = getSupportFragmentManager();
        ft= fm.beginTransaction();
        switch (n)
        {
            case 0: //home 버튼이 눌렸을 때
                ft.replace(R.id.Main_Frame,home);
                ft.commit();
                break;

            case 1: //post 버튼이 눌렸을 때
                ft.replace(R.id.Main_Frame,post);
                ft.commit();
                break;

            case 2: //notice 버튼이 눌렸을 때
                ft.replace(R.id.Main_Frame,notice);
                ft.commit();
                break;

            case 3: //my 버튼이 눌렸을 때
                ft.replace(R.id.Main_Frame,my);
                ft.commit();
                break;
        }
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}