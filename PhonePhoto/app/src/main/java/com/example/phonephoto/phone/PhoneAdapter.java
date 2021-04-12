package com.example.phonephoto.phone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phonephoto.R;

import java.util.ArrayList;

public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.PhoneViewHolder>{

    String TAG = "PJ2 PhoneAdapter";
    ArrayList<PhoneItem> items = new ArrayList<PhoneItem>();

    // ViewHolder : 각 view를 보관하는 holder 객체

    public static class PhoneViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;      // 연락처 이름
        protected TextView number;      // 연락처 번호

        public PhoneViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.name);
            this.number = itemView.findViewById(R.id.number);

        }
    }

    public PhoneAdapter(ArrayList<PhoneItem> items) {
        this.items = items;
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
    public PhoneAdapter.PhoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.phone_item, parent, false);
        PhoneAdapter.PhoneViewHolder phoneViewHolder = new PhoneAdapter.PhoneViewHolder(view);
        return phoneViewHolder;
    }

    // ViewHolder의 내용을 변경
    @Override
    public void onBindViewHolder(@NonNull PhoneAdapter.PhoneViewHolder holder, int position) {

//        if(cursor != null && cursor.moveToNext()) {
//            Glide.with(holder.itemView).load(cursor.getString(1)).override(width).centerCrop().into(holder.image);
//        }
        holder.name.setText(items.get(position).getName());
        holder.number.setText(items.get(position).getNumber());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}