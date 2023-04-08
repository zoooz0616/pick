package com.example.choice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment implements View.OnClickListener { //View.OnClickListener 추가 yj
    private View view;
    List<MyItem> list;
    RecyclerView recyclerView;
    Button[] buttons;
    LinearLayoutManager linearLayoutManager;
    HomeAdapter adapter;
    String jsonString, email, category, mode; //mode == 0 -> 추천 카테고리 쿼리 실행, mode == 1 -> 카테고리별 동작 쿼리 실행
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = View.inflate(this.getActivity(), R.layout.home, null);
        email = SaveSharedPreferences.getUserEmail(view.getContext());
        mode = "0"; //카테고리를 선택하지 않은 경우, 초기 사용자가 선택한 카테고리 3가지가 메인에 노출

        final JsonParse jsonParse = new JsonParse();
        jsonParse.execute("http://ec2-15-164-169-103.ap-northeast-2.compute.amazonaws.com/read.php"); //원래는 read_test.php

        recyclerView = view.findViewById(R.id.list);
        linearLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        buttons = new Button[]{view.findViewById(R.id.recommendation), view.findViewById(R.id.popularity), view.findViewById(R.id.fashion), view.findViewById(R.id.interior),
                view.findViewById(R.id.media), view.findViewById(R.id.daily),view.findViewById(R.id.food)
                , view.findViewById(R.id.animal), view.findViewById(R.id.book), view.findViewById(R.id.digital)
                ,view.findViewById(R.id.economy), view.findViewById(R.id.travel)};
        this.SetListener();
        return view;
    }

    public class JsonParse extends AsyncTask<String, Void, String> {
        String TAG = "JsonParseTest";

        @Override
        protected String doInBackground(String... strings) {
            // execute의 매개변수를 받아와서 사용
            String url = strings[0];
            try {
                String selectData = "email=" + email + "&category=" + category + "&mode="+ mode;

                URL serverURL = new URL(url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) serverURL.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(selectData.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                // 연결 상태 확인
                int responseStatusCode = httpURLConnection.getResponseCode();
                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();
                Log.d(TAG, sb.toString().trim());

                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG, "InsertData : Error ", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String fromdoInBackgroundString) { // doInBackgroundString return값
            super.onPostExecute(fromdoInBackgroundString);

            if (fromdoInBackgroundString == null)
                Toast.makeText(view.getContext(), "데이터를 가져오는데 실패하였습니다.", Toast.LENGTH_SHORT).show();
            else {
                jsonString = fromdoInBackgroundString;
                list = doParse();
                if (list.size() == 0)
                    Toast.makeText(view.getContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                adapter = new HomeAdapter(list, view.getContext());
                recyclerView.setAdapter(adapter);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        private ArrayList<MyItem> doParse() {
            ArrayList<MyItem> tmplList = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("Post");
                for (int i = 0; i < jsonArray.length(); i++) {
                    MyItem article = new MyItem();
                    JSONObject item = jsonArray.getJSONObject(i);
                    article.setId(item.getInt("id"));
                    Log.i(TAG, "" + article.getId());
                    article.setDate(item.getString("update_date"));
                    article.setTitle(item.getString("title"));
                    article.setBtn1(item.getString("item1"));
                    article.setBtn2(item.getString("item2"));
                    article.setImg1(item.getString("image1"));
                    article.setImg2(item.getString("image2"));

                    //try {
                        //String imagePath = "https://sswu-pick.s3.ap-northeast-2.amazonaws.com/";
//                        URL url;
//                        URLConnection conn;
//                        BufferedInputStream bis;
//                        Bitmap bm;


//                        url = new URL(item.getString("image1"));
//                        conn = url.openConnection();
//                        conn.connect();
//                        bis = new BufferedInputStream(conn.getInputStream());
//                        //bm = BitmapFactory.decodeStream(bis);
//                        bm = getResizedBitmap(decodeUri(bis), getResources().getDimensionPixelSize(R.dimen.idcard_pic_height), getResources().getDimensionPixelSize(R.dimen.idcard_pic_width));
//                        bis.close();
//                        article.setImg1(bm);
//
//                        url = new URL(item.getString("image2"));
//                        conn = url.openConnection();
//                        conn.connect();
//                        bis = new BufferedInputStream(conn.getInputStream());
//                        //bm = BitmapFactory.decodeStream(bis);
//                        bm = getResizedBitmap(decodeUri(bis), getResources().getDimensionPixelSize(R.dimen.idcard_pic_height), getResources().getDimensionPixelSize(R.dimen.idcard_pic_width));
//                        bis.close();
//                        article.setImg2(bm);
                   // } catch (Exception e) {
                   // }

                    article.setCnt1(item.getInt("count1"));
                    article.setCnt2(item.getInt("count2"));
                    article.setVoted(false);
                    article.setIsClipped(item.getInt("is_clipped"));

                    tmplList.add(article);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return tmplList;
        }
    }

    public void SetListener() {
        for(int i = 0; i < 12; i++){
            buttons[i].setOnClickListener(this);
        }
    }

    // 상단 카테고리 클릭 이벤트
    @Override
    public void onClick(View view) {
        list.clear();
        int position = 0;
        mode = "1"; //추천 카테고리 이외의 카테고리
        Log.i("Home2", "onClick()");
        for(int i = 0; i < 12; i++){
            buttons[i].setTextColor(0xff000000);
        }
        final JsonParse jsonParse = new JsonParse();
        switch (view.getId()) {
            case R.id.recommendation: //추천 카테고리는 초기 사용자가 선택한 관심카테고리를 보여줌 (다른 카테고리 선택 한 후 다시 메인화면으로 돌아오고 싶을때 추천 카테고리 클릭)
                mode = "0";
                category = "";
                break;
            //추천 카테고리가 아닌 다른 카테고리를 선택한 경우 해당 카테고리를 보여줌
            case R.id.popularity: //인기글 차후 구현해야할 부분
                position = 1;
                category = ""; //이부분 구현하기
                break;
            case R.id.fashion:
                position = 2;
                category = "fashion"; //서버로 전달할 카테고리명 설정
                break;
            case R.id.interior:
                position = 3;
                category = "interior";
                break;
            case R.id.media:
                position = 4;
                category = "media";
                break;
            case R.id.daily:
                position = 5;
                category = "daily";
                break;
            case R.id.food:
                position = 6;
                category = "food";
                break;
            case R.id.animal:
                position = 7;
                category = "animal";
                break;
            case R.id.book:
                position = 8;
                category = "book";
                break;
            case R.id.digital:
                position = 9;
                category = "digital";
                break;
            case R.id.economy:
                position = 10;
                category = "economy";
                break;
            case R.id.travel:
                position = 11;
                category = "travel";
                break;
        }
        buttons[position].setTextColor(ContextCompat.getColor(Home.this.getContext(), R.color.dark_purple_temp));
        jsonParse.execute("http://ec2-15-164-169-103.ap-northeast-2.compute.amazonaws.com/read.php");
    }

    public Bitmap getResizedBitmap(Bitmap image, int newHeight, int newWidth) {
        int width = image.getWidth();
        int height = image.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        if (Build.VERSION.SDK_INT <= 19) {
            //matrix.postRotate(90);
        }
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }

    private Bitmap decodeUri(InputStream bis) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(bis, null, o);

        final int REQUIRED_SIZE = 100;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(bis, null, o2);
    }


}