package com.example.choice;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class MyCountRequest extends StringRequest { // PHP 파일 연동

    final static private String URL = "http://ec2-15-164-169-103.ap-northeast-2.compute.amazonaws.com/my_count.php"; // 서버 URL 설정
    private Map<String, String>map;

    public MyCountRequest(String email, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();
        map.put("email",email);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}