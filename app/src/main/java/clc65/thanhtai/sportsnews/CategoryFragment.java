package clc65.thanhtai.sportsnews;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class CategoryFragment extends Fragment {

    // Link RSS tương ứng
    private final String RSS_BONGDA = "https://vnexpress.net/rss/the-thao/bong-da.rss";
    private final String RSS_TENNIS = "https://vnexpress.net/rss/the-thao/tennis.rss";
    private final String RSS_DUAXE = "https://vnexpress.net/rss/the-thao/dua-xe.rss";
    private final String RSS_HAUTRUONG = "https://vnexpress.net/rss/the-thao/hau-truong.rss";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        // Xử lý sự kiện click
        view.findViewById(R.id.card_bongda).setOnClickListener(v -> openCategoryNews("Bóng đá", RSS_BONGDA));
        view.findViewById(R.id.card_tennis).setOnClickListener(v -> openCategoryNews("Tennis", RSS_TENNIS));
        view.findViewById(R.id.card_duaxe).setOnClickListener(v -> openCategoryNews("Đua xe", RSS_DUAXE));
        view.findViewById(R.id.card_hautruong).setOnClickListener(v -> openCategoryNews("Hậu trường", RSS_HAUTRUONG));

        return view;
    }

    private void openCategoryNews(String title, String rssLink) {
        Intent intent = new Intent(getContext(), CategoryNewsActivity.class);
        intent.putExtra("TITLE", title);
        intent.putExtra("RSS_LINK", rssLink);
        startActivity(intent);
    }
}