package com.example.continuoustempsensor;

import android.os.Bundle;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;

public class PrivacyFormsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_forms);
        Button back = findViewById(R.id.termsBack);
        back.setOnClickListener(v -> onBackPressed());


        PDFView pdfView = findViewById(R.id.privacyPDF);
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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}