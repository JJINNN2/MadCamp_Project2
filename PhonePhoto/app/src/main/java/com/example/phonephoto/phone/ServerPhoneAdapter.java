package com.example.phonephoto.phone;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phonephoto.R;

import java.util.ArrayList;

public class ServerPhoneAdapter extends RecyclerView.Adapter<ServerPhoneAdapter.ServerPhoneViewHolder>{

    String TAG = "PJ2 ServerPhoneAdapter";
    Context context;
    ArrayList<String> nameArray, numberArray;

    // ViewHolder : 각 view를 보관하는 holder 객체
    public class ServerPhoneViewHolder extends RecyclerView.ViewHolder {


        protected ConstraintLayout constraintLayout;
        protected TextView phoneName;
        protected TextView phoneNumber;
        // protected ImageButton zoominButton;
        protected ImageButton downloadButton;
        protected ImageButton deleteButton;

        public ServerPhoneViewHolder(@NonNull final View itemView) {

            super(itemView);
            this.constraintLayout =itemView.findViewById(R.id.constraintLayout);
            this.phoneName = itemView.findViewById(R.id.phoneName);
            this.phoneNumber = itemView.findViewById(R.id.phoneNumber);
            this.downloadButton = itemView.findViewById(R.id.downloadButton);
            this.deleteButton = itemView.findViewById(R.id.deleteButton);

            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    downloadSelectedPhone(context, position);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    deleteSelectedPhone(context, position);
                }
            });
        }
    }

    public ServerPhoneAdapter(Context context, ArrayList<String> nameArray, ArrayList<String> numberArray) {
        this.context = context;
        this.nameArray = nameArray;
        this.numberArray = numberArray;
        Log.d(TAG, "ServerPhoneAdapter");
    }

    @NonNull
    @Override
    public ServerPhoneAdapter.ServerPhoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.phone_server_item, parent, false);
        ServerPhoneAdapter.ServerPhoneViewHolder serverPhoneViewHolder = new ServerPhoneAdapter.ServerPhoneViewHolder(view);
        Log.d(TAG, "onCreateViewHolder");
        return serverPhoneViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ServerPhoneAdapter.ServerPhoneViewHolder holder, int position) {
        holder.phoneName.setText(nameArray.get(position));
        holder.phoneNumber.setText(numberArray.get(position));
    }

    @Override
    public int getItemCount() {
        if(nameArray == null) return 0;
        return nameArray.size();
    }

    static void downloadSelectedPhone(Context context, int position) {
        ((ServerPhoneActivity)context).downloadPhone(position);
    }

    static void deleteSelectedPhone(Context context, int position) {
        ((ServerPhoneActivity)context).deletePhone(position);

    }
}
