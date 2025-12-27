package clc65.thanhtai.sportsnews;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private ArrayList<News> listNews;
    private ArrayList<News> originalList;
    private SearchView searchView;

    // Khai báo các View của Tab
    private TextView tvNoibat, tvMoinhat;
    private View lineNoibat, lineMoinhat;
    private LinearLayout tabNoibat, tabMoinhat;

    // Link RSS
    private final String URL_NOIBAT = "https://vnexpress.net/rss/the-thao/bong-da.rss"; // Coi bóng đá là nổi bật
    private final String URL_MOINHAT = "https://vnexpress.net/rss/the-thao.rss";       // Tin tổng hợp mới nhất

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 1. Ánh xạ View
        recyclerView = view.findViewById(R.id.recycler_view_home);
        searchView = view.findViewById(R.id.search_view);

        tabNoibat = view.findViewById(R.id.tab_noibat);
        tabMoinhat = view.findViewById(R.id.tab_moinhat);
        tvNoibat = view.findViewById(R.id.tv_noibat);
        tvMoinhat = view.findViewById(R.id.tv_moinhat);
        lineNoibat = view.findViewById(R.id.line_noibat);
        lineMoinhat = view.findViewById(R.id.line_moinhat);

        // 2. Setup RecyclerView
        listNews = new ArrayList<>();
        originalList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NewsAdapter(getContext(), listNews, false);
        recyclerView.setAdapter(adapter);

        // 3. Xử lý sự kiện click Tab
        tabNoibat.setOnClickListener(v -> {
            updateTabUI(true); // true = Nổi bật đang chọn
            loadRSS(URL_NOIBAT);
        });

        tabMoinhat.setOnClickListener(v -> {
            updateTabUI(false); // false = Mới nhất đang chọn
            loadRSS(URL_MOINHAT);
        });

        // 4. Xử lý tìm kiếm
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterNews(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterNews(newText);
                return true;
            }
        });

        // Mặc định chọn Tab Nổi Bật khi mở app
        updateTabUI(true);
        loadRSS(URL_NOIBAT);

        return view;
    }

    // Hàm đổi màu Tab
    private void updateTabUI(boolean isNoiBatSelected) {
        if (isNoiBatSelected) {
            // Nổi bật: Đỏ, Đậm | Mới nhất: Xám, Nhạt
            tvNoibat.setTextColor(Color.parseColor("#B71C1C"));
            lineNoibat.setVisibility(View.VISIBLE);

            tvMoinhat.setTextColor(Color.parseColor("#999999"));
            lineMoinhat.setVisibility(View.INVISIBLE);
        } else {
            // Ngược lại
            tvNoibat.setTextColor(Color.parseColor("#999999"));
            lineNoibat.setVisibility(View.INVISIBLE);

            tvMoinhat.setTextColor(Color.parseColor("#B71C1C"));
            lineMoinhat.setVisibility(View.VISIBLE);
        }
        // Xóa tìm kiếm khi chuyển tab
        searchView.setQuery("", false);
    }

    private void filterNews(String text) {
        listNews.clear();
        if (text.isEmpty()) {
            listNews.addAll(originalList);
        } else {
            text = text.toLowerCase();
            for (News item : originalList) {
                if (item.getTitle().toLowerCase().contains(text)) {
                    listNews.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void loadRSS(String url) {
        // (Giữ nguyên code loadRSS như bài trước, không thay đổi gì ở logic parse tin)
        // Code ở bài trước đã rất tốt rồi, chỉ cần copy lại vào đây
        // ... (Bạn copy đoạn loadRSS từ câu trả lời trước vào đây nhé)

        // --- Code loadRSS ngắn gọn để bạn dễ hình dung ---
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                        .timeout(15000).get();
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
                        if (imgElement != null) imgSrc = imgElement.attr("src");
                    } catch (Exception e) {}
                    if (!title.isEmpty()) tempList.add(new News(title, link, imgSrc, pubDate));
                }
                new Handler(Looper.getMainLooper()).post(() -> {
                    originalList.clear();
                    originalList.addAll(tempList);
                    listNews.clear();
                    listNews.addAll(tempList);
                    adapter.notifyDataSetChanged();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}