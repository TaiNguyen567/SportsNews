package clc65.thanhtai.sportsnews;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DetailActivity extends AppCompatActivity {

    private WebView webViewContent;
    // Biến lưu thông tin bài viết hiện tại để dùng cho nút Lưu/Chia sẻ
    private String currentUrl = "";
    private String currentTitle = "";
    private String currentImage = ""; // Ảnh đại diện (nếu lấy được)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // 1. Ánh xạ View
        webViewContent = findViewById(R.id.webview_content);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_detail);

        // Nút Back trên Header (nếu có)
        if (findViewById(R.id.btn_back_detail) != null) {
            findViewById(R.id.btn_back_detail).setOnClickListener(v -> finish());
        }

        // 2. XỬ LÝ SỰ KIỆN BOTTOM BAR (Menu dưới đáy)
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            // Sửa các ID này cho khớp với file menu của bạn
            if (id == R.id.nav_home) {
                finish(); // Quay lại
                return true;
            } else if (id == R.id.nav_save) {
                saveArticle(); // Lưu tin
                return true;
            } else if (id == R.id.nav_share) {
                shareArticle(); // Chia sẻ
                return true;
            }
            return false;
        });

        // 3. Cấu hình WebView
        webViewContent.getSettings().setJavaScriptEnabled(true);
        webViewContent.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webViewContent.setWebViewClient(new WebViewClient());

        // 4. Lấy Link từ màn hình trước
        currentUrl = getIntent().getStringExtra("LINK");

        if (currentUrl != null && !currentUrl.isEmpty()) {
            parseVnExpressContent(currentUrl);
        } else {
            Toast.makeText(this, "Lỗi đường dẫn", Toast.LENGTH_SHORT).show();
        }
    }

    // Hàm Chia sẻ bài viết
    private void shareArticle() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, currentTitle);
        shareIntent.putExtra(Intent.EXTRA_TEXT, currentTitle + "\n" + currentUrl);
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ bài viết qua"));
    }

    // Hàm Lưu bài viết vào Yêu thích
    private void saveArticle() {
        if (currentTitle.isEmpty()) {
            Toast.makeText(this, "Đang tải dữ liệu, vui lòng chờ...", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng News để lưu
        // Lưu ý: Ảnh đại diện lấy từ nội dung bài viết hoặc để mặc định
        if (currentImage.isEmpty()) {
            currentImage = "https://s1.vnecdn.net/vnexpress/restruct/i/v95/logo_default.jpg";
        }

        News newsToSave = new News(currentTitle, currentUrl, currentImage, "Đã lưu");

        // Gọi hàm từ FavoritesManager
        FavoritesManager.addFavorite(this, newsToSave);
        Toast.makeText(this, "Đã lưu vào mục Yêu thích!", Toast.LENGTH_SHORT).show();
    }

    private void parseVnExpressContent(String url) {
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(10000).get();

                // Lấy nội dung
                String title = doc.select(".title-detail").text();
                String date = doc.select(".date").text();
                String description = doc.select(".description").text();
                Element contentElement = doc.select(".fck_detail").first();

                // Cập nhật biến toàn cục để dùng cho nút Lưu/Chia sẻ
                currentTitle = title;

                String contentHtml = "";
                if (contentElement != null) {
                    contentElement.select(".box-tinlienquan").remove();
                    contentElement.select("iframe").remove();

                    Elements images = contentElement.select("img");
                    // Lấy ảnh đầu tiên trong bài làm ảnh đại diện (nếu có)
                    if (!images.isEmpty()) {
                        currentImage = images.first().attr("src");
                    }

                    for (Element img : images) {
                        img.attr("style", "max-width: 100%; height: auto; border-radius: 8px; margin: 10px 0;");
                    }
                    contentHtml = contentElement.html();
                }

                // Tạo HTML hiển thị
                String finalHtml = "<!DOCTYPE html><html><head>" +
                        "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                        "<style>" +
                        "body { font-family: 'Roboto', sans-serif; font-size: 16px; line-height: 1.6; color: #333; padding: 16px; padding-bottom: 80px; margin: 0; }" + // padding-bottom để tránh bị menu che
                        "h1 { font-size: 24px; font-weight: bold; margin-bottom: 8px; color: #000; line-height: 1.3; }" +
                        ".date { font-size: 13px; color: #757575; margin-bottom: 20px; display: block; }" +
                        ".desc { font-weight: bold; font-size: 18px; margin-bottom: 15px; }" +
                        "p { text-align: justify; margin-bottom: 15px; }" +
                        "</style></head><body>" +
                        "<h1>" + title + "</h1>" +
                        "<span class='date'>" + date + "</span>" +
                        "<div class='desc'>" + description + "</div>" +
                        contentHtml +
                        "</body></html>";

                new Handler(Looper.getMainLooper()).post(() -> {
                    webViewContent.loadDataWithBaseURL(null, finalHtml, "text/html", "utf-8", null);
                });

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(DetailActivity.this, "Lỗi tải trang", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}