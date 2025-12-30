package clc65.thanhtai.sportsnews;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvDate, tvLink;
    private WebView wvContent;
    private ImageView imgThumb, imgBackHeader;
    private LinearLayout btnBackBottom, btnSave, btnShare;

    private String currentUrl = "";
    private String currentTitle = "";
    // Khởi tạo mặc định để tránh null
    private String currentImage = "https://s1.vnecdn.net/vnexpress/restruct/i/v95/logo_default.jpg";
    private String currentDesc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvTitle = findViewById(R.id.tv_detail_title);
        tvDate = findViewById(R.id.tv_detail_date);
        wvContent = findViewById(R.id.wv_detail_content);
        tvLink = findViewById(R.id.tv_detail_link);
        imgThumb = findViewById(R.id.img_detail_thumb);

        imgBackHeader = findViewById(R.id.img_back);
        btnBackBottom = findViewById(R.id.btn_back_bottom);
        btnSave = findViewById(R.id.btn_save_news);
        btnShare = findViewById(R.id.btn_share_news);

        // Cấu hình WebView
        WebSettings webSettings = wvContent.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        wvContent.setWebViewClient(new WebViewClient());
        wvContent.setBackgroundColor(0x00000000);

        View.OnClickListener backAction = v -> finish();
        imgBackHeader.setOnClickListener(backAction);
        btnBackBottom.setOnClickListener(backAction);

        btnSave.setOnClickListener(v -> saveArticle());
        btnShare.setOnClickListener(v -> shareArticle());

        tvLink.setOnClickListener(v -> {
            if (!currentUrl.isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl));
                startActivity(browserIntent);
            }
        });

        // --- QUAN TRỌNG: Nhận dữ liệu đầu vào ---
        currentUrl = getIntent().getStringExtra("LINK");

        // 1. Nhận ảnh từ NewsAdapter gửi sang (Đây là ảnh chuẩn nhất)
        String passedImage = getIntent().getStringExtra("IMAGE");
        if (passedImage != null && !passedImage.isEmpty()) {
            currentImage = passedImage; // Gán ngay vào biến toàn cục
            loadImage(currentImage);
        }

        if (currentUrl != null && !currentUrl.isEmpty()) {
            parseVnExpressContent(currentUrl);
        } else {
            Toast.makeText(this, "Lỗi đường dẫn", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadImage(String url) {
        if (url != null && !url.isEmpty()) {
            imgThumb.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imgThumb);
        } else {
            imgThumb.setVisibility(View.GONE);
        }
    }

    private void parseVnExpressContent(String url) {
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0")
                        .timeout(10000)
                        .get();

                String title = doc.select(".title-detail").text();
                String date = doc.select(".date").text();
                String description = doc.select(".description").text();

                // Nếu chưa có ảnh (từ Intent), mới cố gắng tìm trong thẻ meta
                if (currentImage.equals("https://s1.vnecdn.net/vnexpress/restruct/i/v95/logo_default.jpg")) {
                    Elements metaImg = doc.select("meta[property=og:image]");
                    if (!metaImg.isEmpty()) {
                        currentImage = metaImg.attr("content");
                    }
                }

                Element contentElement = doc.select(".fck_detail").first();
                String contentText = "";

                if (contentElement != null) {
                    contentElement.select(".box-tinlienquan").remove();
                    contentElement.select(".video").remove();
                    contentText = contentElement.html();
                }

                currentTitle = title;

                // Biến cục bộ dùng để hiển thị UI
                String finalTitle = title;
                String finalDate = date;
                String finalImage = currentImage; // Dùng biến toàn cục đã cập nhật

                String htmlData = "<html><head><style>" +
                        "body { font-family: sans-serif; font-size: 16px; line-height: 1.5; color: #333; padding: 0; margin: 0; }" +
                        "img { max-width: 100%; height: auto; display: block; margin: 10px auto; }" +
                        "iframe { max-width: 100%; }" +
                        ".desc { font-weight: bold; margin-bottom: 15px; font-size: 18px; }" +
                        "</style></head><body>" +
                        "<div class='desc'>" + description + "</div>" +
                        contentText +
                        "</body></html>";

                new Handler(Looper.getMainLooper()).post(() -> {
                    tvTitle.setText(finalTitle);
                    tvDate.setText(finalDate);
                    loadImage(finalImage);
                    wvContent.loadDataWithBaseURL(null, htmlData, "text/html", "UTF-8", null);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void saveArticle() {
        // 1. Kiểm tra đăng nhập
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để lưu tin!", Toast.LENGTH_SHORT).show();
            return;
        }
        String userEmail = user.getEmail(); // Lấy email người dùng hiện tại

        // 2. Kiểm tra dữ liệu bài báo
        if (currentTitle.isEmpty()) return;
        if (currentImage.isEmpty()) currentImage = "https://s1.vnecdn.net/vnexpress/restruct/i/v95/logo_default.jpg";

        // 3. Khởi tạo Manager
        FavoritesManager db = new FavoritesManager(this);

        // 4. Kiểm tra xem đã lưu chưa (theo email này)
        if (!db.isFavorite(currentUrl, userEmail)) {
            News newsToSave = new News(currentTitle, currentUrl, currentImage, "Đã lưu");

            // GỌI HÀM CÓ TRUYỀN EMAIL
            db.addFavorite(newsToSave, userEmail);

            Toast.makeText(this, "Đã lưu vào mục Yêu thích!", Toast.LENGTH_SHORT).show();
            // (Optional) Đổi icon nút sang trạng thái đã lưu
            // btnSaveIcon.setImageResource(R.drawable.ic_star_filled);
        } else {
            // Nếu muốn bấm lần nữa thì bỏ lưu
            db.removeFavorite(currentUrl, userEmail);
            Toast.makeText(this, "Đã bỏ lưu bài viết", Toast.LENGTH_SHORT).show();
            // (Optional) Đổi icon về trạng thái chưa lưu
        }
    }

    private void shareArticle() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, currentTitle);
        shareIntent.putExtra(Intent.EXTRA_TEXT, currentTitle + "\n" + currentUrl);
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ bài viết qua"));
    }
}