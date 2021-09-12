package com.example.continuoustempsensor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class FeedbackActivity extends AppCompatActivity {

    public static final String URL="https://forms.gle/v19jDUjGWBeMSiXr5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Button back = findViewById(R.id.feedbackBack);
        back.setOnClickListener(v -> onBackPressed());
        Button submitButton = findViewById(R.id.submit);
        submitButton.setOnClickListener(v -> {
            Uri uriURL = Uri.parse(URL);
            Intent launch = new Intent(Intent.ACTION_VIEW, uriURL);
            startActivity(launch);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}