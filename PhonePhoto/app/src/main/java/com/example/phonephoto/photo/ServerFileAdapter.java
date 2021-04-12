package com.example.phonephoto.photo;

import android.content.Context;
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

public class ServerFileAdapter extends RecyclerView.Adapter<ServerFileAdapter.ServerFileViewHolder>{

    Context context;
    ArrayList<String> fileNames, pathArray;

    // ViewHolder : 각 view를 보관하는 holder 객체
    public class ServerFileViewHolder extends RecyclerView.ViewHolder {

        protected ConstraintLayout constraintLayout;
        protected TextView fileName;
       // protected ImageButton zoominButton;
        protected ImageButton downloadButton;
        protected ImageButton deleteButton;

        public ServerFileViewHolder(@NonNull final View itemView) {
            super(itemView);
            this.constraintLayout =itemView.findViewById(R.id.constraintLayout);
            this.fileName = itemView.findViewById(R.id.fileName);
            //this.zoominButton = itemView.findViewById(R.id.zoominButton);
            this.downloadButton = itemView.findViewById(R.id.downloadButton);
            this.deleteButton = itemView.findViewById(R.id.deleteButton);

            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    zoomInSelectedImage(context, position);
                }
            });

            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    downloadSelectedImage(context, position);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    deleteSelectedImage(context, position);
                }
            });
        }
    }

    public ServerFileAdapter(Context context, ArrayList<String> fileNames, ArrayList<String> pathArray) {
        this.context = context;
        this.fileNames = fileNames;
        this.pathArray = pathArray;
    }

    @NonNull
    @Override
    public ServerFileAdapter.ServerFileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_server_item, parent, false);
        ServerFileAdapter.ServerFileViewHolder serverFileViewHolder = new ServerFileAdapter.ServerFileViewHolder(view);
        return serverFileViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ServerFileAdapter.ServerFileViewHolder holder, int position) {
        holder.fileName.setText(fileNames.get(position));
    }

    @Override
    public int getItemCount() {
        if(fileNames == null) return 0;
        return fileNames.size();
    }

    static void zoomInSelectedImage(Context context, int position) {
        ((ServerGalleryActivity)context).zoomInImage(position);
    }

    static void downloadSelectedImage(Context context, int position) {
        ((ServerGalleryActivity)context).downloadImage(position);
    }

    static void deleteSelectedImage(Context context, int position) {
        ((ServerGalleryActivity)context).deleteImage(position);

    }
}
