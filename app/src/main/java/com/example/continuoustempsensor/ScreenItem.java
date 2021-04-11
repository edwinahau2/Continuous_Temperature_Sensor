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

//    public void setTitle(String title) {
//        Title = title;
//    }
//
//    public void setDescription(String description) {
//        Description = description;
//    }
//
//    public void setScreenImg(int screenImg) {
//        ScreenImg = screenImg;
//    }
//
//    public void setScreenImg(int tipsImg) { TipsImg = }

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
