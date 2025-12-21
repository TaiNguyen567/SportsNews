package clc65.thanhtai.sportsnews;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private ArrayList<Article> list;
    private Context context;

    public NewsAdapter(Context context, ArrayList<Article> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = list.get(position);
        holder.tvTitle.setText(article.title);
        holder.tvDate.setText(article.pubDate); // Sau này thay bằng hàm tính thời gian "x phút trước"

        // Dùng Glide load ảnh
        Glide.with(context).load(article.image).into(holder.imgThumb);

        // Click vào bài báo
        holder.itemView.setOnClickListener(v -> {
            // Ở đây bạn sẽ code chuyển sang màn hình DetailActivity
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate;
        ImageView imgThumb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDate = itemView.findViewById(R.id.tv_date);
            imgThumb = itemView.findViewById(R.id.img_thumb);
        }
    }
}
