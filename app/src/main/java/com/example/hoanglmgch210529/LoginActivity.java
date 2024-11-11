package com.example.hoanglmgch210529;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hoanglmgch210529.Course.CourseListActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            // Xử lý đăng nhập
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            Intent intent = new Intent(LoginActivity.this, CourseListActivity.class);
            startActivity(intent);
            // Kết thúc LoginActivity để ngăn quay lại trang đăng nhập khi nhấn nút Back
            finish();
//            if (email.isEmpty() || password.isEmpty()) {
//                Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
//            } else {
//                if (email.equals("hoang") && password.equals("123123")) {
//                    Toast.makeText(LoginActivity.this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
//                    // Chuyển đến HomeActivity sau khi đăng nhập thành công
//                    Intent intent = new Intent(LoginActivity.this, CourseListActivity.class);
//                    startActivity(intent);
//                    // Kết thúc LoginActivity để ngăn quay lại trang đăng nhập khi nhấn nút Back
//                    finish();
//                } else {
//                    Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
//                }
//            }
        });
    }
}
