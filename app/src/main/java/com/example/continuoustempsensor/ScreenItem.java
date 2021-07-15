package com.example.continuoustempsensor;

public class ScreenItem {
    String Title, Description;
    int ScreenImg, TipsImg, pos;

    public ScreenItem(String title, String description, int screenImg, int tipsImg, int position) {
        Title = title;
        Description = description;
        ScreenImg = screenImg;
        TipsImg = tipsImg;
        pos = position;
    }

    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Description;
    }

    public int getScreenImg() {
        return ScreenImg;
    }

    public int getTipsImg() {return TipsImg;}

    public int getPos() {return pos;}

}
