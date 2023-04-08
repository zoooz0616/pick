package com.example.choice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Signup1 extends AppCompatActivity {
    private EditText editEmail;
    private EditText editPwd;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup1);

        editEmail = findViewById(R.id.editEmail);
        editPwd = findViewById(R.id.editPwd);
        Button button = (Button)findViewById(R.id.continue_btn);

        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent Signupintent = new Intent(getApplicationContext(),Signup2.class);
                Signupintent.putExtra("email", editEmail.getText().toString());
                Signupintent.putExtra("password", editPwd.getText().toString());
                Signup1.this.startActivity(Signupintent);
            }
        });
    }
}