package clc65.thanhtai.sportsnews;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class FavoritesManager {
    private static final String PREF_NAME = "MyFavorites";
    private static final String KEY_FAV = "fav_list";

    // Hàm thêm tin yêu thích
    public static void addFavorite(Context context, News news) {
        ArrayList<News> list = getFavorites(context);
        // Kiểm tra trùng lặp trước khi thêm
        for (News item : list) {
            if (item.getTitle().equals(news.getTitle())) return;
        }
        list.add(news);
        saveFavorites(context, list);
    }

    // --- ĐÂY LÀ HÀM BẠN ĐANG THIẾU ---
    public static void removeFavorite(Context context, News news) {
        ArrayList<News> list = getFavorites(context);

        // Tìm bài báo trong danh sách dựa vào Tiêu đề để xóa
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getTitle().equals(news.getTitle())) {
                list.remove(i);
                break; // Xóa xong thì dừng vòng lặp
            }
        }

        saveFavorites(context, list);
    }
    // ---------------------------------

    public static ArrayList<News> getFavorites(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_FAV, null);
        if (json == null) return new ArrayList<>();

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<News>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private static void saveFavorites(Context context, ArrayList<News> list) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(KEY_FAV, json);
        editor.apply();
    }
}