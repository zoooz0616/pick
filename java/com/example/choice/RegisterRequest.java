package com.example.choice;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest { // PHP 파일 연동
    final static private String URL =  "http://ec2-15-164-169-103.ap-northeast-2.compute.amazonaws.com/register.php"; // 서버 URL 설정
    private Map<String, String> map;

    public RegisterRequest(String email, String pw, String gender, String age, String category1, String category2, String category3, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        map = new HashMap<>();
        map.put("email",email);
        map.put("pw", pw);
        map.put("gender", gender);
        map.put("age", age);
        map.put("category1", category1);
        map.put("category2", category2);
        map.put("category3", category3);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }

}