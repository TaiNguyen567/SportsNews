package clc65.thanhtai.sportsnews;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen; // Import cái này

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1. GỌI DÒNG NÀY ĐẦU TIÊN (Trước super.onCreate)
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        // Vì dùng API mới, bạn có thể giữ layout hoặc để null cũng được
        // setKeepOnScreenCondition giúp giữ logo lâu hơn nếu cần tải dữ liệu
        setContentView(R.layout.activity_splash);

        // Logic chuyển trang cũ của bạn
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}