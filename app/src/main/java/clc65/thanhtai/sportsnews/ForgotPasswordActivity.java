package clc65.thanhtai.sportsnews;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        EditText edtEmail = findViewById(R.id.edt_email_reset);
        Button btnReset = findViewById(R.id.btn_reset_pass);
        TextView tvBack = findViewById(R.id.tv_back_login);

        btnReset.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Đã gửi link! Vui lòng kiểm tra cả mục Spam/Rác", Toast.LENGTH_LONG).show();
                        } else {
                            String error = "Lỗi gửi mail";
                            if (task.getException() != null) {
                                error = task.getException().getMessage();
                            }
                            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        tvBack.setOnClickListener(v -> finish());
    }
}