package com.monopoly.ukraine.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.monopoly.ukraine.R;
import com.monopoly.ukraine.model.Player;
import java.util.*;

public class WinActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);
        List<Player> players=(List<Player>)getIntent().getSerializableExtra("players");
        int wi=getIntent().getIntExtra("winnerIdx",0);
        Player winner=players.get(wi);
        ((TextView)findViewById(R.id.tvWinEmoji)).setText(winner.getAvatar());
        ((TextView)findViewById(R.id.tvWinName)).setText(winner.getName()+" перемагає!");
        ((TextView)findViewById(R.id.tvWinSubtitle)).setText("Переможець Монополії України! 🇺🇦");
        LinearLayout stats=findViewById(R.id.statsContainer);
        List<Player> sorted=new ArrayList<>(players);
        sorted.sort((a,b)->b.getMoney()-a.getMoney());
        String[]medals={"🥇","🥈","🥉","4️⃣"};
        for(int i=0;i<sorted.size();i++){
            Player p=sorted.get(i);
            LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.HORIZONTAL);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(0,24,0,24);
            TextView nm=new TextView(this);nm.setText(medals[i]+" "+p.getAvatar()+" "+p.getName());nm.setTextSize(15);nm.setTextColor(i==0?0xFFF4C542:0xFFE8EAF6);
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1);nm.setLayoutParams(lp);row.addView(nm);
            TextView mn=new TextView(this);mn.setText("₴"+p.getMoney());mn.setTextSize(15);mn.setTextColor(i==0?0xFFF4C542:0xFF6B7A99);mn.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);row.addView(mn);
            stats.addView(row);
            if(i<sorted.size()-1){View div=new View(this);LinearLayout.LayoutParams dl=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);div.setLayoutParams(dl);div.setBackgroundColor(0x221e3a5f);stats.addView(div);}
        }
        findViewById(R.id.btnNewGame).setOnClickListener(v->{Intent i=new Intent(this,MainActivity.class);i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);startActivity(i);finish();});
    }
}
