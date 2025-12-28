package clc65.thanhtai.sportsnews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private ArrayList<News> listFavorite;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_favorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listFavorite = new ArrayList<>();
        adapter = new NewsAdapter(getContext(), listFavorite, false);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        listFavorite.clear();
        ArrayList<News> savedList = FavoritesManager.getFavorites(getContext());

        if (savedList != null) {
            listFavorite.addAll(savedList);
        }

        adapter.notifyDataSetChanged();

        if (listFavorite.isEmpty()) {
            Toast.makeText(getContext(), "Chưa có tin nào được lưu", Toast.LENGTH_SHORT).show();
        }
    }
}