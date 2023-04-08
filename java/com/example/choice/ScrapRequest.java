package com.example.choice;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ScrapRequest extends StringRequest { // PHP 파일 연동
    final static private String URL = "http://ec2-15-164-169-103.ap-northeast-2.compute.amazonaws.com/scrap.php"; //서버 URL 설정
    private Map<String, String> map;

    public ScrapRequest(String email, int id, int mode, Response.Listener<String> listener){ //mode == 0 -> save, mode == 1 -> delete
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();
        map.put("user_email", email);
        map.put("id", String.valueOf(id));
        map.put("mode",  String.valueOf(mode));
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}