package clc65.thanhtai.sportsnews;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

    RecyclerView recyclerView;
    NewsAdapter adapter;
    ArrayList<Article> articleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        articleList = new ArrayList<>();
        adapter = new NewsAdapter(this, articleList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Gọi hàm lấy tin tức RSS (VnExpress Thể Thao)
        new FetchNewsTask().execute("https://vnexpress.net/rss/the-thao.rss");
    }

    // Class con để chạy ngầm việc tải tin tức
    private class FetchNewsTask extends AsyncTask<String, Void, ArrayList<Article>> {
        @Override
        protected ArrayList<Article> doInBackground(String... strings) {
            String url = strings[0];
            ArrayList<Article> list = new ArrayList<>();
            try {
                // Jsoup kết nối và lấy toàn bộ file RSS
                Document doc = Jsoup.connect(url).get();
                // Chọn các thẻ <item>
                Elements items = doc.select("item");

                for (Element item : items) {
                    String title = item.select("title").text();
                    String link = item.select("link").text();
                    String pubDate = item.select("pubDate").text();

                    // Lấy ảnh từ thẻ description
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
            super.onPostExecute(articles);
            // Cập nhật dữ liệu lên giao diện
            articleList.clear();
            articleList.addAll(articles);
            adapter.notifyDataSetChanged();
        }
    }
}