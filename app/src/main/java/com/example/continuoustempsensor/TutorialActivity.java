package com.example.continuoustempsensor;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.List;

public class TutorialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        VideoView movie = findViewById(R.id.settingVideo);
        Uri video = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.sample);
        movie.setVideoURI(video);
        movie.setMediaController(new MediaController(this));
        Button play = findViewById(R.id.playVideo);
        play.setOnClickListener(v -> {
            play.setVisibility(View.INVISIBLE);
            movie.start();
        });
        Button back = findViewById(R.id.tutorialBack);
        back.setOnClickListener(v -> onBackPressed());

        CustomViewPager appPages = findViewById(R.id.appContainer);
        final List<PageItem> mList = new ArrayList<>();
        mList.add(R.drawable.homepage, 1);
        mList.add(R.drawable.datalog, 0);
        mList.add(R.drawable.settings, 2);
        AppViewPageAdapter appViewPageAdapter = new AppViewPageAdapter(this, mList);
        appPages.setAdapter(appViewPageAdapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}