package clc65.thanhtai.sportsnews;

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
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText edtEmail;
    private Button btnReset;
    private TextView tvBack;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edt_email_reset);
        btnReset = findViewById(R.id.btn_reset_pass);
        tvBack = findViewById(R.id.tv_back_login);

        // Nút Gửi yêu cầu
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        // Nút Quay lại
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng màn hình này để về lại Login
            }
        });
    }

    private void resetPassword() {
        String email = edtEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Vui lòng nhập Email");
            return;
        }

        // Gửi yêu cầu lên Firebase
        // Firebase sẽ tự động gửi 1 email chứa link reset password cho user
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Đã gửi email! Vui lòng kiểm tra hộp thư của bạn.",
                                    Toast.LENGTH_LONG).show();
                            finish(); // Gửi xong thì về màn hình đăng nhập luôn
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Lỗi";
                            Toast.makeText(ForgotPasswordActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}