package clc65.thanhtai.sportsnews;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText edtEmail, edtPass, edtConfirmPass;
    private Button btnRegister;
    private TextView tvLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ View
        edtEmail = findViewById(R.id.edt_email);
        edtPass = findViewById(R.id.edt_pass);
        edtConfirmPass = findViewById(R.id.edt_confirm_pass);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);

        // 1. Xử lý nút Đăng ký
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // 2. Nếu bấm "Đăng nhập" thì quay lại màn cũ
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng màn đăng ký để lộ ra màn đăng nhập bên dưới
            }
        });
    }

    private void registerUser() {
        String email = edtEmail.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();
        String confirmPass = edtConfirmPass.getText().toString().trim();

        // --- KIỂM TRA LỖI (VALIDATION) ---
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Vui lòng nhập Email");
            edtEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            edtPass.setError("Vui lòng nhập Mật khẩu");
            edtPass.requestFocus();
            return;
        }
        if (pass.length() < 6) {
            edtPass.setError("Mật khẩu phải có ít nhất 6 ký tự");
            edtPass.requestFocus();
            return;
        }
        if (!pass.equals(confirmPass)) {
            edtConfirmPass.setError("Mật khẩu nhập lại không khớp");
            edtConfirmPass.requestFocus();
            return;
        }

        // --- GỬI LÊN FIREBASE ---
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Thành công
                            Toast.makeText(RegisterActivity.this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();

                            // Chuyển thẳng vào màn hình chính
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            // Xóa lịch sử back stack để user không bấm Back về trang đăng ký được nữa
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            // Thất bại (Ví dụ: Email đã tồn tại)
                            String error = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                            Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + error, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}