package clc65.thanhtai.sportsnews;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private ArrayList<News> listNews;

    private TextView tvNoibat, tvMoinhat;
    private View lineNoibat, lineMoinhat;
    private LinearLayout tabNoibat, tabMoinhat;

    private final String URL_NOIBAT = "https://vnexpress.net/rss/the-thao.rss";
    private final String URL_MOINHAT = "https://vnexpress.net/rss/the-thao.rss";

    private String currentUrl = URL_MOINHAT;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_home);
        tabNoibat = view.findViewById(R.id.tab_noibat);
        tabMoinhat = view.findViewById(R.id.tab_moinhat);
        tvNoibat = view.findViewById(R.id.tv_noibat);
        tvMoinhat = view.findViewById(R.id.tv_moinhat);
        lineNoibat = view.findViewById(R.id.line_noibat);
        lineMoinhat = view.findViewById(R.id.line_moinhat);

        listNews = new ArrayList<>();
        adapter = new NewsAdapter(getContext(), listNews, false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        selectTab(false);

        tabNoibat.setOnClickListener(v -> selectTab(true));
        tabMoinhat.setOnClickListener(v -> selectTab(false));

        return view;
    }

    private void selectTab(boolean isNoibat) {
        if (isNoibat) {
            tvNoibat.setTextColor(0xFFB71C1C);
            lineNoibat.setVisibility(View.VISIBLE);
            tvMoinhat.setTextColor(0xFF999999);
            lineMoinhat.setVisibility(View.INVISIBLE);
            currentUrl = URL_NOIBAT;
        } else {
            tvMoinhat.setTextColor(0xFFB71C1C);
            lineMoinhat.setVisibility(View.VISIBLE);
            tvNoibat.setTextColor(0xFF999999);
            lineNoibat.setVisibility(View.INVISIBLE);
            currentUrl = URL_MOINHAT;
        }

        listNews.clear();
        adapter.notifyDataSetChanged();
        loadRSS(currentUrl);
    }

    private void loadRSS(String url) {
        new Thread(() -> {
            ArrayList<News> tempList = new ArrayList<>();
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0")
                        .timeout(20000)
                        .ignoreContentType(true)
                        .ignoreHttpErrors(true)
                        .get();

                Elements items = doc.select("item");

                for (Element item : items) {
                    try {
                        String title = item.select("title").text();
                        String link = item.select("link").text();
                        String pubDate = item.select("pubDate").text();
                        String imgSrc = "https://s1.vnecdn.net/vnexpress/restruct/i/v95/logo_default.jpg";

                        try {
                            String descriptionRaw = item.select("description").text();
                            if (descriptionRaw.isEmpty()) descriptionRaw = item.text();

                            if (!descriptionRaw.isEmpty()) {
                                Document descDoc = Jsoup.parse(descriptionRaw);
                                Element imgElement = descDoc.select("img").first();
                                if (imgElement != null) {
                                    imgSrc = imgElement.attr("src");
                                    if (imgSrc.contains("_180x108")) {
                                        imgSrc = imgSrc.replace("_180x108", "_680x0");
                                    }
                                }
                            }
                        } catch (Exception e) {
                        }

                        if (!title.isEmpty()) {
                            tempList.add(new News(title, link, imgSrc, pubDate));
                        }
                    } catch (Exception e) {
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                if (!url.equals(currentUrl)) return;

                listNews.clear();
                listNews.addAll(tempList);
                adapter.notifyDataSetChanged();

                if (listNews.isEmpty()) {
                    Toast.makeText(getContext(), "Không tải được dữ liệu", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}