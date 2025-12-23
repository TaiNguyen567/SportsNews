package clc65.thanhtai.sportsnews;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvEmail, btnChangePass, btnLogout;
    private ImageView btnBack;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ
        tvEmail = findViewById(R.id.tv_user_email);
        btnChangePass = findViewById(R.id.btn_change_pass_nav);
        btnLogout = findViewById(R.id.btn_logout_profile);
        btnBack = findViewById(R.id.btn_back_profile);

        // Hiển thị Email người dùng hiện tại
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            tvEmail.setText(user.getEmail());
        }

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý chuyển sang màn đổi mật khẩu
        btnChangePass.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        // Xử lý Đăng xuất
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            // Xóa hết Activity cũ và chuyển về Login
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}