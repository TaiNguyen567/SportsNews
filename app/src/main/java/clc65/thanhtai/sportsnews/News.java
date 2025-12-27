package clc65.thanhtai.sportsnews;

import java.io.Serializable;
import java.util.Objects;

public class News implements Serializable {
    private String title;
    private String link;
    private String image;
    private String date;

    public News(String title, String link, String image, String date) {
        this.title = title;
        this.link = link;
        this.image = image;
        this.date = date;
    }

    public String getTitle() { return title; }
    public String getLink() { return link; }
    public String getImage() { return image; }
    public String getDate() { return date; }

    // Hàm này giúp so sánh để không lưu trùng tin yêu thích
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        News news = (News) o;
        return Objects.equals(title, news.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}