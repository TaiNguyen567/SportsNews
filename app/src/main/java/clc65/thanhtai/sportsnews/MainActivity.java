package clc65.thanhtai.sportsnews;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private ArrayList<Article> articleList;
    private ImageView btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        btnProfile = findViewById(R.id.btn_profile_main);

        // Cấu hình RecyclerView
        articleList = new ArrayList<>();
        adapter = new NewsAdapter(this, articleList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Sự kiện click nút Profile (Nếu bạn chưa làm ProfileActivity thì comment dòng này lại)
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Bắt đầu tải tin tức từ VnExpress
        new FetchNewsTask().execute("https://vnexpress.net/rss/the-thao.rss");
    }

    // Class con chạy ngầm để lấy dữ liệu
    private class FetchNewsTask extends AsyncTask<String, Void, ArrayList<Article>> {
        @Override
        protected ArrayList<Article> doInBackground(String... strings) {
            String url = strings[0];
            ArrayList<Article> list = new ArrayList<>();
            try {
                Document doc = Jsoup.connect(url).get();
                Elements items = doc.select("item");

                for (Element item : items) {
                    String title = item.select("title").text();
                    String link = item.select("link").text();
                    String pubDate = item.select("pubDate").text();

                    // Lấy ảnh từ trong thẻ description
                    String description = item.select("description").text();
                    Document descDoc = Jsoup.parse(description);
                    Element imgTag = descDoc.select("img").first();
                    String imgUrl = (imgTag != null) ? imgTag.attr("src") : "";

                    list.add(new Article(title, link, imgUrl, pubDate));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<Article> articles) {
            if (articles.size() > 0) {
                articleList.clear();
                articleList.addAll(articles);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(MainActivity.this, "Lỗi tải tin tức!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}