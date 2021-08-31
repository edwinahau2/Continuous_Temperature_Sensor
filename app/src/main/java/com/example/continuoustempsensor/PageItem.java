package com.example.continuoustempsensor;

public class PageItem {

    int appPage, pos;

    public PageItem(int appImg, int position) {
        appPage = appImg;
        pos = position;
    }

    public int getAppImg() {
        return appPage;
    }

    public int getImgPos() {
        return pos;
    }
}
