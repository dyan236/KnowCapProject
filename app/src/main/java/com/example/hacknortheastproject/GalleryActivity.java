package com.example.hacknortheastproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.RecyclerView);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setLayoutManager(layoutManager);
        ArrayList<GalleryPicture> data = getPictures();
        picAdapter adapter = new picAdapter(data, getApplicationContext());
        recyclerView.setAdapter(adapter);
        initButton();
        initButton2();
    }

    private ArrayList<GalleryPicture> getPictures(){
        ArrayList<GalleryPicture> res = new ArrayList<>();
        String appPath = Environment.getExternalStorageDirectory().toString() + "/HackNortheastProject";
        try{
            final File f = new File(appPath);
            for (final File fileEntry : f.listFiles()) {

                GalleryPicture g = new GalleryPicture(appPath + "/" + fileEntry.getName());
                System.out.println(g.getFilePath());
                res.add(g);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }


        //listFilesForFolder(f);

    return res;
    }

    private void initButton(){
        ImageButton b = findViewById(R.id.backGalleryButton);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(GalleryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initButton2(){
        ImageButton b = findViewById(R.id.trashButton);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(GalleryActivity.this, "This feature is not yet implemented. :)", Toast.LENGTH_SHORT).show();
            }
        });
    }


}