package com.monopoly.ukraine.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.monopoly.ukraine.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView logo = findViewById(R.id.tvLogo);
        ScaleAnimation scale = new ScaleAnimation(0.5f,1f,0.5f,1f,
            Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scale.setDuration(700); scale.setFillAfter(true);
        logo.startAnimation(scale);

        TextView sub = findViewById(R.id.tvSubtitle);
        AlphaAnimation fade = new AlphaAnimation(0f,1f);
        fade.setDuration(1200); fade.setStartOffset(400); fade.setFillAfter(true);
        sub.startAnimation(fade);

        findViewById(R.id.btnStart).setOnClickListener(v ->
            startActivity(new Intent(this, SetupActivity.class)));
    }
}
