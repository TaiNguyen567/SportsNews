package clc65.thanhtai.sportsnews;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        EditText edtEmail = findViewById(R.id.edt_email_reset);
        findViewById(R.id.btn_reset_pass).setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            if(!email.isEmpty()) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()) {
                                Toast.makeText(this, "Đã gửi mail!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
            }
        });
        findViewById(R.id.tv_back_login).setOnClickListener(v -> finish());
    }
}