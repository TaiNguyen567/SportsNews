package clc65.thanhtai.sportsnews;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class CategoryNewsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private ArrayList<News> listNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_news);

        TextView tvTitle = findViewById(R.id.tv_cate_title);
        ImageView btnBack = findViewById(R.id.btn_back_cate);
        recyclerView = findViewById(R.id.recycler_view_cate);

        // --- SỬA Ở ĐÂY: Đổi "RSS_LINK" thành "LINK" cho thống nhất ---
        String title = getIntent().getStringExtra("TITLE");
        String rssLink = getIntent().getStringExtra("LINK");

        if (title != null) tvTitle.setText(title);
        btnBack.setOnClickListener(v -> finish());

        // Setup RecyclerView
        listNews = new ArrayList<>();
        // Lưu ý: false nghĩa là không phải màn hình yêu thích (để hiện nút sao rỗng)
        adapter = new NewsAdapter(this, listNews, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Load tin tức
        if (rssLink != null && !rssLink.isEmpty()) {
            loadRSS(rssLink);
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy đường dẫn RSS", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadRSS(String url) {
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(10000).get();
                Elements items = doc.select("item");
                ArrayList<News> tempList = new ArrayList<>();

                for (Element item : items) {
                    String title = item.select("title").text();
                    String link = item.select("link").text();
                    String pubDate = item.select("pubDate").text();
                    String description = item.select("description").text();
                    String imgSrc = "https://s1.vnecdn.net/vnexpress/restruct/i/v95/logo_default.jpg";

                    try {
                        Document descDoc = Jsoup.parse(description);
                        Element imgElement = descDoc.select("img").first();
                        if (imgElement != null) {
                            imgSrc = imgElement.attr("src");
                            // Hack nhỏ: đổi ảnh nhỏ sang ảnh lớn của VnExpress cho nét
                            if (imgSrc.contains("_180x108")) {
                                imgSrc = imgSrc.replace("_180x108", "_680x0");
                            }
                        }
                    } catch (Exception e) {}

                    if (!title.isEmpty()) tempList.add(new News(title, link, imgSrc, pubDate));
                }

                new Handler(Looper.getMainLooper()).post(() -> {
                    listNews.addAll(tempList);
                    adapter.notifyDataSetChanged();

                    if (listNews.isEmpty()) {
                        Toast.makeText(CategoryNewsActivity.this, "Không tải được bài viết nào", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(CategoryNewsActivity.this, "Lỗi mạng hoặc link hỏng", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}