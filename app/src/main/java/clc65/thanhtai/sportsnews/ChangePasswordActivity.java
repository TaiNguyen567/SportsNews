package clc65.thanhtai.sportsnews;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText edtOldPass, edtNewPass, edtConfirmPass;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        edtOldPass = findViewById(R.id.edt_old_pass);
        edtNewPass = findViewById(R.id.edt_new_pass);
        edtConfirmPass = findViewById(R.id.edt_confirm_new_pass);
        btnSave = findViewById(R.id.btn_save_pass);

        findViewById(R.id.btn_back_change_pass).setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String oldPass = edtOldPass.getText().toString().trim(); // Lấy pass cũ
        String newPass = edtNewPass.getText().toString().trim();
        String confirmPass = edtConfirmPass.getText().toString().trim();

        if (TextUtils.isEmpty(oldPass)) {
            edtOldPass.setError("Vui lòng nhập mật khẩu hiện tại");
            return;
        }

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

        if (user != null && user.getEmail() != null) {
            Toast.makeText(this, "Đang xử lý...", Toast.LENGTH_SHORT).show();

            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);

            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(ChangePasswordActivity.this, "Lỗi cập nhật: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        edtOldPass.setError("Mật khẩu hiện tại không đúng");
                        Toast.makeText(ChangePasswordActivity.this, "Xác thực thất bại. Vui lòng kiểm tra mật khẩu cũ.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Lỗi người dùng, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
        }
    }
}