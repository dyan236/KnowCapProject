package com.example.hacknortheastproject;

public class GalleryPicture {
    private String filePath;
    public GalleryPicture(){
        filePath="";
    }
    public GalleryPicture(String s){
        filePath = s;
    }

    public String getFilePath(){ return filePath; }
    public void setFilePath(String s){ filePath = s; }
}

