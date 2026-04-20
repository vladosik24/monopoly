package com.monopoly.ukraine.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.monopoly.ukraine.R;
import com.monopoly.ukraine.model.Player;
import java.util.ArrayList;
import java.util.List;

public class SetupActivity extends AppCompatActivity {
    private static final String[] AVATARS={"🚀","🦁","🐉","🌟","🎭","🦊","🐺","🦅"};
    private static final String[] COLORS={"#e53935","#1e88e5","#43a047","#fb8c00"};
    private static final String[] DEFAULTS={"Гравець 1","Гравець 2","Гравець 3","Гравець 4"};
    private int[] ai={0,1,2,3};
    private EditText[] inputs=new EditText[4];
    private TextView[] avatarViews=new TextView[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        LinearLayout container=findViewById(R.id.playerContainer);

        for(int i=0;i<4;i++){
            final int idx=i;
            View row=LayoutInflater.from(this).inflate(R.layout.item_player_setup,container,false);
            TextView av=row.findViewById(R.id.tvAvatar);
            av.setText(AVATARS[idx]);
            av.setOnClickListener(v->{ai[idx]=(ai[idx]+1)%AVATARS.length;av.setText(AVATARS[ai[idx]]);});
            avatarViews[i]=av;
            EditText et=row.findViewById(R.id.etPlayerName);
            et.setText(DEFAULTS[i]); inputs[i]=et;
            View dot=row.findViewById(R.id.vColor);
            dot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor(COLORS[i])));
            container.addView(row);
        }

        findViewById(R.id.btnPlay).setOnClickListener(v->{
            List<Player> players=new ArrayList<>();
            for(int i=0;i<4;i++){
                String name=inputs[i].getText().toString().trim();
                if(name.isEmpty()) name=DEFAULTS[i];
                players.add(new Player(name,AVATARS[ai[i]],Color.parseColor(COLORS[i]),i));
            }
            Intent intent=new Intent(this,GameActivity.class);
            intent.putExtra("players",(java.io.Serializable)new ArrayList<>(players));
            startActivity(intent);
        });
        findViewById(R.id.btnBack).setOnClickListener(v->finish());
    }
}
