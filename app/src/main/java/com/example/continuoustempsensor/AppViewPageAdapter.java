package com.example.continuoustempsensor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class AppViewPageAdapter extends PagerAdapter {

    List<PageItem> mPageItem;
    public static Context mContext;

    public AppViewPageAdapter(Context mContext, List<PageItem> mPageItem) {
        AppViewPageAdapter.mContext = mContext;
        this.mPageItem = mPageItem;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen;
        int pos = mPageItem.get(position).getImgPos();
        if (pos == 0) { // data log
            layoutScreen = inflater.inflate(R.layout.layout_datalog, null);
            ImageView imgPage = layoutScreen.findViewById(R.id.imgData);
            imgPage.setImageResource(mPageItem.get(position).getAppImg());
        } else if (pos == 1) { // home page
            layoutScreen = inflater.inflate(R.layout.layout_home, null);
            ImageView imgPage = layoutScreen.findViewById(R.id.imgHome);
            imgPage.setImageResource(mPageItem.get(position).getAppImg());
        } else { // settings
            layoutScreen = inflater.inflate(R.layout.layout_settings, null);
            ImageView imgPage = layoutScreen.findViewById(R.id.imgSettings);
            imgPage.setImageResource(mPageItem.get(position).getAppImg());
        }
        container.addView(layoutScreen);
        return layoutScreen;
    }

    @Override
    public int getCount() {
        return mPageItem.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
