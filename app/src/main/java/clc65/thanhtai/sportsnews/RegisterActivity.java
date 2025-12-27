package clc65.thanhtai.sportsnews;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText; // Quan trọng: Dùng EditText thường
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    // Đổi TextInputEditText thành EditText
    private EditText edtEmail, edtPass, edtConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ đúng với EditText
        edtEmail = findViewById(R.id.edt_email);
        edtPass = findViewById(R.id.edt_pass);
        edtConfirm = findViewById(R.id.edt_confirm_pass);

        findViewById(R.id.btn_register).setOnClickListener(v -> register());

        // Bấm nút Đăng nhập thì quay lại màn hình cũ
        findViewById(R.id.tv_login).setOnClickListener(v -> finish());
    }

    private void register() {
        String email = edtEmail.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();
        String confirm = edtConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Vui lòng nhập Email");
            return;
        }

        if (pass.length() < 6) {
            edtPass.setError("Mật khẩu phải trên 6 ký tự");
            return;
        }

        if (!pass.equals(confirm)) {
            edtConfirm.setError("Mật khẩu nhập lại không khớp");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                // Đăng ký xong thì vào thẳng màn hình chính, xóa lịch sử back
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}