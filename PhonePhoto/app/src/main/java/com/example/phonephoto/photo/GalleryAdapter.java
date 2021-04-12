package com.example.phonephoto.photo;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.phonephoto.R;

import java.util.ArrayList;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    String TAG = "PJ2 GalleryAdapter";

    //Cursor cursor;
    ArrayList<PhotoItem> items;
    int width;

    // ViewHolder : 각 view를 보관하는 holder 객체
    public static class GalleryViewHolder extends RecyclerView.ViewHolder {

        protected ImageView image;

        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);

            //Log.d("PJ2 GalleryAdapter", "GalleryViewHolder: " + itemView);
            this.image = itemView.findViewById(R.id.img);
        }
    }

    public GalleryAdapter(ArrayList<PhotoItem> items, int width) {
        this.items = items;
        this.width = width;
    }

    /*** onCreateViewHolder()
     *
     * 새로운 View를 생성할 때 실행되어 ViewHolder를 반환
     * convertView가 null 일 때 inflate 하고 ViewHolder를 생성
     * -> 각 요소를 findViewById를 통해 저장
     * -> 그리고 앞으로는 ViewHolder를 getTag로 불러와서 재사용한다. 즉 findViewById를 다시 안 해도 된다.
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Log.d(TAG, "onCreateViewHolder");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);
        GalleryViewHolder galleryViewHolder = new GalleryViewHolder(view);
        return galleryViewHolder;
    }

    // ViewHolder의 내용을 변경
    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        //Log.d(TAG, "onBindViewHolder: " + position);
        Glide.with(holder.itemView).load(items.get(position).getPath()).override(width).centerCrop().into(holder.image);
    }

    @Override
    public int getItemCount() {
        if(items == null) return 0;
        return items.size();
    }
}


