package clc65.thanhtai.sportsnews;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class FavoritesManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SportsNews.db";
    private static final int DATABASE_VERSION = 2; // Tăng version lên

    // Thêm cột user_email vào câu lệnh tạo bảng
    private static final String TABLE_CREATE =
            "CREATE TABLE favorites (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title TEXT, " +
                    "link TEXT, " +
                    "image TEXT, " +
                    "date TEXT, " +
                    "user_email TEXT)";

    public FavoritesManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS favorites");
        onCreate(db);
    }

    // 1. Hàm Thêm bài viết (Cần truyền thêm email)
    public void addFavorite(News news, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", news.getTitle());
        values.put("link", news.getLink());
        values.put("image", news.getImage());
        values.put("date", news.getDate());
        values.put("user_email", email); // Lưu email người dùng

        db.insert("favorites", null, values);
        db.close();
    }

    // 2. Hàm Xóa bài viết (Chỉ xóa bài của email này)
    public void removeFavorite(String link, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Xóa bài có Link trùng khớp VÀ Email trùng khớp
        db.delete("favorites", "link=? AND user_email=?", new String[]{link, email});
        db.close();
    }

    // 3. Hàm Kiểm tra đã thích chưa (Check theo email)
    public boolean isFavorite(String link, String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM favorites WHERE link=? AND user_email=?", new String[]{link, email});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    // 4. Hàm Lấy danh sách (Chỉ lấy bài của email này)
    public ArrayList<News> getFavoritesByEmail(String email) {
        ArrayList<News> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Lọc theo email
        Cursor cursor = db.rawQuery("SELECT * FROM favorites WHERE user_email=?", new String[]{email});

        if (cursor.moveToFirst()) {
            do {
                // SỬA: Thêm "OrThrow" vào sau getColumnIndex
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String link = cursor.getString(cursor.getColumnIndexOrThrow("link"));
                String image = cursor.getString(cursor.getColumnIndexOrThrow("image"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                list.add(new News(title, link, image, date));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
}