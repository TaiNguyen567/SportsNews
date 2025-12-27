package clc65.thanhtai.sportsnews;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText edtNewPass, edtConfirmPass;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        edtNewPass = findViewById(R.id.edt_new_pass);
        edtConfirmPass = findViewById(R.id.edt_confirm_new_pass);
        btnSave = findViewById(R.id.btn_save_pass);

        // Nút quay lại (nếu bạn muốn dùng nút back cứng của điện thoại thì không cần dòng này)
        findViewById(R.id.btn_back_change_pass).setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String newPass = edtNewPass.getText().toString().trim();
        String confirmPass = edtConfirmPass.getText().toString().trim();

        if (TextUtils.isEmpty(newPass)) {
            edtNewPass.setError("Vui lòng nhập mật khẩu mới");
            return;
        }

        if (newPass.length() < 6) {
            edtNewPass.setError("Mật khẩu phải từ 6 ký tự trở lên");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            edtConfirmPass.setError("Mật khẩu xác nhận không khớp");
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.updatePassword(newPass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                            finish(); // Đóng màn hình này
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Phiên đăng nhập hết hạn, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
        }
    }
}