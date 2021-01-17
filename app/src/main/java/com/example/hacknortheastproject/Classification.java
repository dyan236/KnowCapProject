package com.example.hacknortheastproject;
//package classification;
import java.util.HashMap; 

public class Classification {

	// Instance Variables
    private static HashMap<String, String> dict = new HashMap<String, String>();
    private static boolean initialized = false;

    static public void init(){
        if(!initialized) {
            dict.put("Cat", "a small domesticated carnivorous mammal with soft fur, a short snout, and retractable claws");
            dict.put("Dog", "a domesticated carnivorous mammal that typically has a long snout, an acute sense of smell, nonretractable claws, and a barking, howling, or whining voice.");
            dict.put("Teacup", "a cup from which tea is drunk.");
            dict.put("Bottle", "a container, typically made of glass or plastic and with a narrow neck, used for storing drinks or other liquids.");
            dict.put("Pencil", "an instrument for writing or drawing, consisting of a thin stick of graphite or a similar substance enclosed in a long thin piece of wood or fixed in a metal or plastic case.");
            dict.put("Fish", "a limbless cold-blooded vertebrate animal with gills and fins and living wholly in water.");
            dict.put("Key", "a small piece of shaped metal with incisions cut to fit the wards of a particular lock, which is inserted into a lock and turned to open or close it.");
            dict.put("Glasses", "a pair of lenses set in a frame resting on the nose and ears, used to correct or assist defective eyesight or protect the eyes.");
            dict.put("Stapler", "a device for fastening together sheets of paper with a staple or staples.");
            dict.put("Tape", "a narrow strip of material, typically used to hold or fasten something.");
            dict.put("Phone", "device used for communication");
            dict.put("Flower", "Smell good");
            dict.put("Calculator", "Helps make math easier");
            dict.put("Bed","Keeps monsters away");
            dict.put("2D barcode", "Test description for 2d barcode object. wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww");
            initialized=true;
        }
    }
    //get info based upon object name
    static public String getInfo(String name) {
    	return dict.get(name);
    }
    
    //add object name and description
    public void addItem(String name, String info) {
    	dict.put(name , info);
    }
    
    //remove object name and description
    public void removeItem(String name) {
    	dict.remove(name);
    }
    
    //remove all object name and description
    public void clearItems() {
    	dict.clear();
    }
    
    //get size of dictionary 
    public int getSize() {
    	return dict.size();
    }
    
    //print all keys
    public void printKeys() {
    	for (String i : dict.keySet()) 
    		  System.out.println(i);
    }
    
    //print all values
    public void printValues() {
    	for (String i : dict.values()) 
    		  System.out.println(i);
    }
    
    //print all Items
    public void printItems() {
    	for (String i : dict.keySet()) 
    		  System.out.println("key: " + i + " value: " +dict.get(i));
    }
	
}
