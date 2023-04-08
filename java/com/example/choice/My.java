package com.example.choice;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class My extends Fragment implements View.OnClickListener{ //11.26 View.OnClickListener 추가 yj
    private View view;
    private String email, jsonString, mode;
    RecyclerView recyclerView;
    List<MyItem> list;
    TextView post_cnt, save_cnt, post_text, save_text;

    //사용자 리스트뷰 화면
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = View.inflate(this.getActivity(), R.layout.my, null);

        //로그인한 사용자 아이디로 이메일 설정정
        email = SaveSharedPreferences.getUserEmail(view.getContext());
        TextView txt_email = view.findViewById(R.id.email);
        txt_email.setText(email);

        post_cnt = view.findViewById(R.id.post_cnt);
        save_cnt = view.findViewById(R.id.save_cnt);
        post_text = view.findViewById(R.id.post_text);
        save_text = view.findViewById(R.id.save_text);
        post_cnt.setOnClickListener(this);
        save_cnt.setOnClickListener(this);

        post_cnt.setTextColor(ContextCompat.getColor(My.this.getContext(), R.color.dark_purple_temp));
        post_text.setTextColor(ContextCompat.getColor(My.this.getContext(), R.color.dark_purple_temp));

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        int count1 = jsonObject.getInt("count1");
                        int count2 = jsonObject.getInt("count2");
                        post_cnt.setText("" + count1);
                        save_cnt.setText("" + count2);
                    } else { // 로그인에 실패한 경우
                        Toast.makeText(view.getContext(), "실패..", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        MyCountRequest myCountRequest = new MyCountRequest(email, responseListener);
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        queue.add(myCountRequest);

        final JsonParse jsonParse = new JsonParse();
        mode = "0";
        jsonParse.execute("http://ec2-15-164-169-103.ap-northeast-2.compute.amazonaws.com/my_post.php");

        recyclerView = view.findViewById(R.id.list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }

    public class JsonParse extends AsyncTask<String, Void, String> {
        String TAG = "JsonParseTest";

        @Override
        protected String doInBackground(String... strings) {
            // execute의 매개변수를 받아와서 사용
            String url = strings[0];
            try {
                String selectData;
                //서버로 데이터 전송
                Log.d(TAG, "----------------" + email);
                //selectData = "email=" + email;
                selectData = "email=" + email + "&mode=" + mode;

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
                //post_cnt.setText(""+list.size());
                MyAdapter adapter = new MyAdapter(list, view.getContext());
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

    @Override
    public void onClick(View view) {
        list.clear();
        final JsonParse jsonParse = new JsonParse();
        switch (view.getId()) {
            case R.id.post_cnt:
                mode = "0";
                post_cnt.setTextColor(ContextCompat.getColor(My.this.getContext(), R.color.dark_purple_temp));
                save_cnt.setTextColor(0xff000000);
                post_text.setTextColor(ContextCompat.getColor(My.this.getContext(), R.color.dark_purple_temp));
                save_text.setTextColor(0xff000000);
                break;
            case R.id.save_cnt:
                mode = "1";
                save_cnt.setTextColor(ContextCompat.getColor(My.this.getContext(), R.color.dark_purple_temp));
                post_cnt.setTextColor(0xff000000);
                save_text.setTextColor(ContextCompat.getColor(My.this.getContext(), R.color.dark_purple_temp));
                post_text.setTextColor(0xff000000);
                break;
        }
        jsonParse.execute("http://ec2-15-164-169-103.ap-northeast-2.compute.amazonaws.com/my_post.php");
    }
}
