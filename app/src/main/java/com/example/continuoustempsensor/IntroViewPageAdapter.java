package com.example.continuoustempsensor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.github.barteksc.pdfviewer.PDFView;

import java.util.List;

public class IntroViewPageAdapter extends PagerAdapter {

    public static Context mContext;
    List<ScreenItem> mListScreen;
    public static CheckBox terms;
    public static Button button;


    public IntroViewPageAdapter(Context mContext, List<ScreenItem> mListScreen) {
        IntroViewPageAdapter.mContext = mContext;
        this.mListScreen = mListScreen;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen;
        int pos = mListScreen.get(position).getPos();
        if (pos == 0) {
            layoutScreen = inflater.inflate(R.layout.layout_screen1, null);
            ImageView imgSlide = layoutScreen.findViewById(R.id.uci);
            ImageView imgTips = layoutScreen.findViewById(R.id.tips);
            TextView title = layoutScreen.findViewById(R.id.intro_title);
            TextView description = layoutScreen.findViewById(R.id.description);
            title.setText(mListScreen.get(position).getTitle());
            description.setText(mListScreen.get(position).getDescription());
            imgSlide.setImageResource(mListScreen.get(position).getScreenImg());
            imgTips.setImageResource(mListScreen.get(position).getTipsImg());
        } else if (pos == 2) {
            layoutScreen = inflater.inflate(R.layout.layout_screen2, null);
            TextView title = layoutScreen.findViewById(R.id.sensorText);
            title.setText(mListScreen.get(position).getTitle());
            VideoView movie = layoutScreen.findViewById(R.id.videoTuts);
            Uri video = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.sample);
            movie.setVideoURI(video);
            movie.setMediaController(new MediaController(mContext));
            Button play = layoutScreen.findViewById(R.id.playButton);
            play.setOnClickListener(v -> {
                play.setVisibility(View.INVISIBLE);
                movie.start();
            });
        } else if (pos == 3) {
            layoutScreen = inflater.inflate(R.layout.layout_screen3, null);
            ImageView imgSlide = layoutScreen.findViewById(R.id.uci);
            ImageView imgTips = layoutScreen.findViewById(R.id.tips);
            TextView title = layoutScreen.findViewById(R.id.intro_title);
            TextView description = layoutScreen.findViewById(R.id.description);
            title.setText(mListScreen.get(position).getTitle());
            description.setText(mListScreen.get(position).getDescription());
            imgSlide.setImageResource(mListScreen.get(position).getScreenImg());
            imgTips.setImageResource(mListScreen.get(position).getTipsImg());
        } else if (pos == 1) {
            layoutScreen = inflater.inflate(R.layout.layout_screen4, null);
            TextView title = layoutScreen.findViewById(R.id.service);
            title.setText(mListScreen.get(position).getTitle());
            PDFView pdfView = layoutScreen.findViewById(R.id.pdf);
            pdfView.fromAsset("Abstract.pdf").load();
            terms = layoutScreen.findViewById(R.id.terms);
        } else {
            layoutScreen = inflater.inflate(R.layout.layout_screen5, null);
            ImageView deviceImg = layoutScreen.findViewById(R.id.ourDevice);
            TextView title = layoutScreen.findViewById(R.id.intro_connect);
            TextView description = layoutScreen.findViewById(R.id.connect_describe);
            button = layoutScreen.findViewById(R.id.buttonCnct);
            title.setText(mListScreen.get(position).getTitle());
            description.setText(mListScreen.get(position).getDescription());
            deviceImg.setImageResource(mListScreen.get(position).getScreenImg());
        }
        container.addView(layoutScreen);
        return layoutScreen;
    }

    @Override
    public int getCount() {
        return mListScreen.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
