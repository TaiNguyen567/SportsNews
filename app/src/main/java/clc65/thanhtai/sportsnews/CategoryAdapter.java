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
import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private ArrayList<Category> listCategory;

    public CategoryAdapter(Context context, ArrayList<Category> listCategory) {
        this.context = context;
        this.listCategory = listCategory;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = listCategory.get(position);
        holder.tvName.setText(category.getName());
        holder.imgIcon.setImageResource(category.getImageResId());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CategoryNewsActivity.class);

            intent.putExtra("TITLE", category.getName());
            intent.putExtra("LINK", category.getRssLink());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listCategory.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView imgIcon;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_category_name);
            imgIcon = itemView.findViewById(R.id.img_category_icon);
        }
    }
}