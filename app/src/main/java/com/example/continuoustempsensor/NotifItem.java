package com.example.continuoustempsensor;

public class NotifItem {
    String Title, Time;
    int img;

    public NotifItem(int Img, String title, String description) {
        Title = title;
        Time = description;
        img = Img;
    }

    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Time;
    }

    public int getImg() {
        return img;
    }
}
