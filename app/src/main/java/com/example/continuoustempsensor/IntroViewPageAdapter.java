package com.example.continuoustempsensor;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.blure.complexview.ComplexView;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class IntroViewPageAdapter extends PagerAdapter {

    public static Context mContext;
    List<ScreenItem> mListScreen;
    public static CheckBox terms;
    public static Button button;
    public static Button learnApp;
    public static Button start;


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
            learnApp = layoutScreen.findViewById(R.id.learn);
            TextView tv = layoutScreen.findViewById(R.id.temp_intro);
            ImageView imageView = layoutScreen.findViewById(R.id.sparkView_intro);
            ImageView btImage = layoutScreen.findViewById(R.id.btSym_intro);
            TextView btButton = layoutScreen.findViewById(R.id.btStat_intro);
            ImageView notif = layoutScreen.findViewById(R.id.notif_intro);
            ComplexView complexView = layoutScreen.findViewById(R.id.complex_intro);
            TextView textView = layoutScreen.findViewById(R.id.dataLog_intro);
            TabLayout tabLayout = layoutScreen.findViewById(R.id.daytime_intro);
            ImageView graph = layoutScreen.findViewById(R.id.calendarView_intro);
            BottomNavigationView menu = layoutScreen.findViewById(R.id.bottomMenu);
            start = layoutScreen.findViewById(R.id.start);
            start.setVisibility(View.INVISIBLE);
            menu.setVisibility(View.INVISIBLE);
            tv.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            btImage.setVisibility(View.INVISIBLE);
            btButton.setVisibility(View.INVISIBLE);
            notif.setVisibility(View.INVISIBLE);
            complexView.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);
            tabLayout.setVisibility(View.INVISIBLE);
            graph.setVisibility(View.INVISIBLE);
            learnApp.setOnClickListener(v -> {
                learnApp.setVisibility(View.INVISIBLE);
                IntroActivity.dotIndicators.setVisibility(View.INVISIBLE);
                IntroActivity.btnNext.setVisibility(View.INVISIBLE);
                IntroActivity.btnBack.setVisibility(View.INVISIBLE);
                ShowGuides("Home", "Content Description", R.id.home, 1, layoutScreen);
            });
        } else if (pos == 1) {
            layoutScreen = inflater.inflate(R.layout.layout_screen4, null);
            TextView title = layoutScreen.findViewById(R.id.service);
            title.setText(mListScreen.get(position).getTitle());
            PDFView pdfView = layoutScreen.findViewById(R.id.pdf);
            pdfView.fromAsset("Abstract.pdf")
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAnnotationRendering(false)
                .enableAntialiasing(true)
                .autoSpacing(true)
                .pageFitPolicy(FitPolicy.WIDTH)
                .pageSnap(false) // snap pages to screen boundaries
                .pageFling(false) // make a fling change only a single page like ViewPager
                .nightMode(false) // toggle night mode
                .load();
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

    private void ShowGuides(String title, String content, int viewId, final int type, View layoutScreen) {
        BottomNavigationView menu = layoutScreen.findViewById(R.id.bottomMenu);
        TextView tv = layoutScreen.findViewById(R.id.temp_intro);
        ImageView imageView = layoutScreen.findViewById(R.id.sparkView_intro);
        ImageView btImage = layoutScreen.findViewById(R.id.btSym_intro);
        TextView btButton = layoutScreen.findViewById(R.id.btStat_intro);
        ImageView notif = layoutScreen.findViewById(R.id.notif_intro);
        ComplexView complexView = layoutScreen.findViewById(R.id.complex_intro);
        TextView textView = layoutScreen.findViewById(R.id.dataLog_intro);
        TabLayout tabLayout = layoutScreen.findViewById(R.id.daytime_intro);
        ImageView graph = layoutScreen.findViewById(R.id.calendarView_intro);
        if (type >= 1 && type < 6) {
            menu.setVisibility(View.VISIBLE);
            menu.setSelectedItemId(R.id.home);
            tv.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            btImage.setVisibility(View.VISIBLE);
            btButton.setVisibility(View.VISIBLE);
            notif.setVisibility(View.VISIBLE);
            complexView.setVisibility(View.VISIBLE);
        } else if (type >= 6 && type < 9){
            menu.setSelectedItemId(R.id.profile);
            textView.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            graph.setVisibility(View.VISIBLE);
            tv.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            btImage.setVisibility(View.INVISIBLE);
            btButton.setVisibility(View.INVISIBLE);
            notif.setVisibility(View.INVISIBLE);
            complexView.setVisibility(View.INVISIBLE);
            if (type == 7) {
//                graph.setBackground(R.drawable.a);
            } else if (type == 8) {
//                graph.setBackground(R.drawable.a);
            }
            graph.setVisibility(View.VISIBLE);
        } else if (type == 9) {
            menu.setSelectedItemId(R.id.Bt);
        }

        if (type < 11) {
            new GuideView.Builder(mContext)
                    .setTitle(title)
                    .setContentText(content)
                    .setTargetView((layoutScreen.findViewById(viewId)))
                    .setDismissType(DismissType.outside)
                    .setGravity(Gravity.auto)
                    .setGuideListener(view -> {
                        if (type == 1) {
                            ShowGuides("Your Temperature", "Content", R.id.complex_intro, 2, layoutScreen);
                        } else if (type == 2) {
                            ShowGuides("Your Graph", "Content", R.id.sparkView_intro, 3, layoutScreen);
                        } else if (type == 3) {
                            ShowGuides("Bluetooth", "Content", R.id.btSym_intro, 4, layoutScreen);
                        } else if (type == 4) {
                            ShowGuides("Notifications", "Content", R.id.notif_intro, 5, layoutScreen);
                        } else if (type == 5) {
                            ShowGuides("Data Log", "Content", R.id.profile, 6, layoutScreen);
                        } else if (type == 6) {
                            ShowGuides("Tabs", "Content", R.id.daytime_intro, 7, layoutScreen);
                        } else if (type == 7) {
                            ShowGuides("Daily Calendar", "Content", R.id.calendarView_intro, 8, layoutScreen);
                        } else if (type == 8) {
                            ShowGuides("Weekly Calendar", "Content", R.id.calendarView_intro, 9, layoutScreen);
                        } else if (type == 9) {
                            ShowGuides("Settings", "Content", R.id.Bt, 10, layoutScreen);
                        } else if (type == 10) {
                            IntroActivity.btnBack.setVisibility(View.VISIBLE);
                            IntroActivity.dotIndicators.setVisibility(View.VISIBLE);
                            tv.setVisibility(View.INVISIBLE);
                            imageView.setVisibility(View.INVISIBLE);
                            btImage.setVisibility(View.INVISIBLE);
                            btButton.setVisibility(View.INVISIBLE);
                            notif.setVisibility(View.INVISIBLE);
                            complexView.setVisibility(View.INVISIBLE);
                            textView.setVisibility(View.INVISIBLE);
                            tabLayout.setVisibility(View.INVISIBLE);
                            graph.setVisibility(View.INVISIBLE);
                            menu.setVisibility(View.INVISIBLE);
                            start.setVisibility(View.VISIBLE);
                            ShowGuides("", "", 0, 11, layoutScreen);
                        }
                    })
                    .build()
                    .show();
        } else {
            start.setOnClickListener(v -> IntroActivity.btnNext.performClick());
        }
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
