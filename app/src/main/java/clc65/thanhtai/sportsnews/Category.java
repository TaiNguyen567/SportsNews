package clc65.thanhtai.sportsnews;

public class Category {
    private String name;
    private String rssLink;
    private int imageResId;

    public Category(String name, String rssLink, int imageResId) {
        this.name = name;
        this.rssLink = rssLink;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getRssLink() { return rssLink; }
    public int getImageResId() { return imageResId; }
}