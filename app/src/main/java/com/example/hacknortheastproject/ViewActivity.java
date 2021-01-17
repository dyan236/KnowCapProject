package com.example.hacknortheastproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.InputStream;

public class ViewActivity extends AppCompatActivity {
    String object;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        String fileLoc;

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                object = null;
                fileLoc = null;
            } else {
                object = extras.getString("objectName");
                fileLoc = extras.getString("fileLoc");
            }
        } else {
            object = (String) savedInstanceState.getSerializable("objectName");
            fileLoc = (String) savedInstanceState.getSerializable("fileLoc");
        }
        System.out.println("***"+fileLoc);
        ImageView iView = (ImageView) findViewById(R.id.imageViewer);
        File img = new File(fileLoc);
        if(img.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(img.getAbsolutePath());
            iView.setImageBitmap(bitmap);
        }
        initButton();
        initViews();

    }
    private void initButton(){
        Button b = findViewById(R.id.backButton);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ViewActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initViews(){
        TextView t1 = findViewById(R.id.titleText);
        Classification.init();
        TextView t2 = findViewById(R.id.desText);
        t1.setText(object);
        t2.setText(Classification.getInfo(object));
        System.out.println("\"" + object + "\"");
        //System.out.println(Classification.getInfo(object));
        System.out.println("\"" + Classification.getInfo(object) + "\"");
        ImageView extra = (ImageView) findViewById(R.id.extraImage);
        extra.setVisibility(View.GONE);
        try{
            InputStream is = getAssets().open(object+".jpg");
            Drawable d = Drawable.createFromStream(is, null);
            extra.setImageDrawable(d);
            extra.setVisibility(View.VISIBLE);
            is.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}