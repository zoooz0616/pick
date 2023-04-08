package com.example.choice;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class PostRequest extends StringRequest { // PHP 파일 연동
    final static private String URL = "http://ec2-15-164-169-103.ap-northeast-2.compute.amazonaws.com/write.php"; // 서버 URL 설정
    private Map<String, String> map;

    public PostRequest(String email, String title, String option1, String option2, String category, String url1, String url2, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();
        map.put("email", email);
        map.put("category", category);
        map.put("title", title);
        map.put("item1", option1);
        map.put("item2", option2);
        map.put("url1", url1);
        map.put("url2", url2);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}