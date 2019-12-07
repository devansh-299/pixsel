package com.devansh.pixsel.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devansh.pixsel.R;
import com.devansh.pixsel.model.imageModel;
import com.devansh.pixsel.util.Util;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImageViewHolder>  {

    private ArrayList<imageModel> imageList;

    public ImageListAdapter(ArrayList<imageModel> imageList) {
        this.imageList = imageList;
    }

    public void updateImageList(List<imageModel> newList) {
        imageList.clear();
        imageList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(
                        R.layout.item_image,
                        parent,
                        false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageView image = holder.itemView.findViewById(R.id.imageView);
        TextView name = holder.itemView.findViewById(R.id.name_list);
        TextView date = holder.itemView.findViewById(R.id.date_list);
        LinearLayout layout = holder.itemView.findViewById(R.id.imageLayout);
        LinearLayout bottomLayout = holder.itemView.findViewById(R.id.item_bottom_bar);

        name.setText(imageList.get(position).imageName);
        date.setText(imageList.get(position).imageDate);

        Util.loadImage(image,imageList.get(position).imageUrl, Util.getProgressDrawable(image.getContext()));
        layout.setOnClickListener(view -> {
            ListFragmentDirections.ActionDetail action = ListFragmentDirections.actionDetail();
            action.setImageid(imageList.get(position).uuid);
            Navigation.findNavController(layout).navigate(action);
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        public View itemView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}
