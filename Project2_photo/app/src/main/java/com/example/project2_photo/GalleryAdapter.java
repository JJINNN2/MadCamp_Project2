package com.example.project2_photo;

import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    Cursor cursor;

    // ViewHolder : 각 view를 보관하는 holder 객체
    public static class GalleryViewHolder extends RecyclerView.ViewHolder {

        protected ImageView image;      // 이미지
        protected TextView title;       // 사진 제목

        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);

            this.image = itemView.findViewById(R.id.imageItem);
            this.title = itemView.findViewById(R.id.imageTitle);
        }
    }

    public GalleryAdapter(Cursor cursor) {
        this.cursor = cursor;
    }

    /*** onCreateViewHolder()
     *
     * 새로운 View를 생성할 때 실행되어 ViewHolder를 반환
     * convertView가 null 일 때 inflate 하고 ViewHolder를 생성
     * -> 각 요소를 findViewById를 통해 저장
     * -> 그리고 앞으로는 ViewHolder를 getTag로 불러와서 재사용한다. 즉 find~를 다시 안 해도 된다.
     *
     * @param parent
     * @param viewType
     * @return
     */

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photoitem, parent, false);
        GalleryViewHolder galleryViewHolder = new GalleryViewHolder(view);
        return galleryViewHolder;
    }

    // // ViewHolder의 내용을 변경
    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {

        /** cursor 이용해 얻은 테이블 정보:
         * | _id | _data | _size | _display_name | mime_type | title | date_added | date_modified | ... */
        if(cursor != null && cursor.moveToNext()) {
            Glide.with(holder.itemView).load(cursor.getString(1)).thumbnail(0.33f).centerCrop().into(holder.image);
        }

    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

}
