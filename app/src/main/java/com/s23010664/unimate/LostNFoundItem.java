package com.s23010664.unimate;

public class LostNFoundItem {
    private int id;
    private String type; // "lost" or "found"
    private String title;
    private String description;
    private String location;
    private String date;
    private String contact;
    private String imageUri;

    public LostNFoundItem(int id, String type, String title, String description, String location, String date, String contact, String imageUri) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.location = location;
        this.date = date;
        this.contact = contact;
        this.imageUri = imageUri;
    }

    public int getId() { return id; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public String getContact() { return contact; }
    public String getImageUri() { return imageUri; }
}
