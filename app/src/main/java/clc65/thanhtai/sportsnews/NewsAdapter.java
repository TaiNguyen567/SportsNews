package clc65.thanhtai.sportsnews;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context context;
    private ArrayList<News> listNews;
    private boolean isFavoriteScreen;

    public NewsAdapter(Context context, ArrayList<News> listNews, boolean isFavoriteScreen) {
        this.context = context;
        this.listNews = listNews;
        this.isFavoriteScreen = isFavoriteScreen;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = listNews.get(position);
        holder.tvTitle.setText(news.getTitle());
        holder.tvDate.setText(news.getDate());

        Glide.with(context).load(news.getImage()).into(holder.imgThumb);

        if (isFavoriteScreen) {
            holder.imgFav.setImageResource(android.R.drawable.ic_menu_delete);
        } else {
            holder.imgFav.setImageResource(android.R.drawable.btn_star_big_off);
        }

        holder.imgFav.setOnClickListener(v -> {
            if (isFavoriteScreen) {
                FavoritesManager.removeFavorite(context, news);
                listNews.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, listNews.size());
                Toast.makeText(context, "Đã xóa tin!", Toast.LENGTH_SHORT).show();
            } else {
                FavoritesManager.addFavorite(context, news);
                Toast.makeText(context, "Đã lưu tin!", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("LINK", news.getLink());
            intent.putExtra("IMAGE", news.getImage());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listNews.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate;
        ImageView imgThumb, imgFav;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDate = itemView.findViewById(R.id.tv_date);
            imgThumb = itemView.findViewById(R.id.img_thumb);
            imgFav = itemView.findViewById(R.id.img_fav);
        }
    }
}