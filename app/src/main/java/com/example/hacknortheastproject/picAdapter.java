package com.example.hacknortheastproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class picAdapter extends RecyclerView.Adapter {
    private ArrayList<GalleryPicture> list;
    private Context pContext;
    public class ListViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public ListViewHolder(@NonNull View itemView){
            super(itemView);
            imageView=itemView.findViewById(R.id.galleryImageView);
        }
    }
    public picAdapter(ArrayList<GalleryPicture> aList, Context c){
        list = aList;
        pContext=c;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ListViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ListViewHolder cvh = (ListViewHolder) holder;

        File img = new File(list.get(position).getFilePath());
        if(img.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(img.getAbsolutePath());

            Bitmap resized = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
            /*int side = bitmap.getWidth();
            if(bitmap.getHeight() < side)
                side = bitmap.getHeight();
            Bitmap rbmp = Bitmap.createBitmap(bitmap, 0, 0, side, side);

            */
            cvh.imageView.setImageBitmap(resized);



        }
    }



    @Override
    public int getItemCount() {
        return list.size();
    }
}
