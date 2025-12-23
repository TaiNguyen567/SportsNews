package clc65.thanhtai.sportsnews;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull; // Import cái này để tránh lỗi @NonNull
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText; // QUAN TRỌNG: Dùng cái này thay cho EditText
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    // Khai báo đúng kiểu TextInputEditText để khớp với XML
    private TextInputEditText edtEmail, edtPass;
    private Button btnLogin;
    private TextView tvRegister;
    private FirebaseAuth mAuth;
    private TextView tvForgotPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ View
        edtEmail = findViewById(R.id.edt_email);
        edtPass = findViewById(R.id.edt_pass);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        tvForgotPass = findViewById(R.id.tv_forgot_pass);

        // 1. Xử lý nút Đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // 2. Xử lý nút chuyển sang Đăng ký
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đảm bảo bạn đã tạo file RegisterActivity rồi nhé
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        tvForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String email = edtEmail.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();

        // Kiểm tra rỗng
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Email không được để trống");
            edtEmail.requestFocus(); // Đưa con trỏ về ô này
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            edtPass.setError("Mật khẩu không được để trống");
            edtPass.requestFocus();
            return;
        }

        // Gọi Firebase để đăng nhập
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Thành công -> Chuyển vào trang chủ
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Đóng màn hình Login lại
                        } else {
                            // Thất bại -> Lấy thông báo lỗi cụ thể
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}