package com.monopoly.ukraine.ui;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.monopoly.ukraine.R;
import com.monopoly.ukraine.game.GameState;
import com.monopoly.ukraine.model.BoardCell;
import com.monopoly.ukraine.model.Player;
import java.util.List;
import java.util.Map;

public class GameActivity extends AppCompatActivity {
    private GameState gs;
    private GridLayout boardGrid;
    private LinearLayout playersBar;
    private TextView tvLog, tvDie1, tvDie2, tvBank;
    private Button btnRoll;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final int[] GROUP_COLORS={
        0xFF8B4513,0xFF00CED1,0xFFFF69B4,0xFFFF8C00,
        0xFFFF0000,0xFFFFD700,0xFF228B22,0xFF00008B};
    private static final String[] DICE={"","⚀","⚁","⚂","⚃","⚄","⚅"};

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        boardGrid=findViewById(R.id.boardGrid);
        playersBar=findViewById(R.id.playersBar);
        tvLog=findViewById(R.id.tvLog);
        btnRoll=findViewById(R.id.btnRoll);
        tvDie1=findViewById(R.id.tvDie1);
        tvDie2=findViewById(R.id.tvDie2);
        tvBank=findViewById(R.id.tvBank);
        List<Player> players=(List<Player>)getIntent().getSerializableExtra("players");
        gs=new GameState(players);
        renderBoard(); refreshUI();
        btnRoll.setOnClickListener(v->onRoll());
    }

    private void onRoll(){
        if(gs.isDiceRolled()||gs.isGameOver()) return;
        btnRoll.setEnabled(false);
        Player p=gs.getCurrentPlayer();
        int[]dice=gs.rollDice();
        tvDie1.setText(DICE[dice[0]]); tvDie2.setText(DICE[dice[1]]);
        animateDie(tvDie1); animateDie(tvDie2);
        int total=dice[0]+dice[1]; boolean isDouble=dice[0]==dice[1];
        handler.postDelayed(()->{
            if(p.isInJail()){
                boolean freed=gs.processJailTurn(isDouble);
                if(!freed){appendLog(p.getAvatar()+" у вязниці. Спроба "+p.getJailTurns());refreshUI();endTurnDelay();return;}
                appendLog("✅ "+p.getName()+" вийшов з вязниці!");
            }
            GameState.LandResult r=gs.moveCurrentPlayer(total);
            updateTokenPositions(); refreshUI(); handleResult(r);
        },500);
    }

    private void handleResult(GameState.LandResult r){
        if(gs.isGameOver()){goToWin();return;}
        switch(r.event){
            case JAIL: showInfo("⛓️ Вязниця!",gs.getCurrentPlayer().getName()+" відправлений до вязниці!"); break;
            case TAX:  showInfo("📋 "+r.cell.getName(),"Ви сплатили ₴"+r.cell.getTaxAmount()); break;
            case CHANCE: showCardDialog("❓ ШАНС",r); break;
            case CHEST:  showCardDialog("📦 СКАРБНИЦЯ",r); break;
            case CAN_BUY: showBuyDialog(r.cell); return;
            case PAY_RENT: showInfo("💸 Оренда!",gs.getCurrentPlayer().getName()+" сплатив ₴"+r.rentAmount+" → "+r.rentOwner.getName()); break;
            default: endTurnDelay(); return;
        }
    }

    private void showBuyDialog(BoardCell cell){
        Player p=gs.getCurrentPlayer();
        boolean canAfford=p.getMoney()>=cell.getPrice();
        String msg="Ціна: ₴"+cell.getPrice();
        if(cell.getRent()!=null) msg+="\nОренда: ₴"+cell.getRent()[0]+(cell.getRent().length>4?"\nГотель: ₴"+cell.getRent()[cell.getRent().length-1]:"");
        msg+="\n\nВаш баланс: ₴"+p.getMoney();
        new AlertDialog.Builder(this,R.style.GameDialog)
            .setTitle(cell.getIcon()+"  "+cell.getName())
            .setMessage(msg)
            .setPositiveButton(canAfford?"💰 Купити за ₴"+cell.getPrice():"Недостатньо коштів",(d,w)->{
                if(canAfford){gs.buyProperty(cell.getId());refreshUI();renderOwnedIndicators();}
                endTurnDelay();})
            .setNegativeButton("Пропустити",(d,w)->endTurnDelay())
            .setCancelable(false).show();
    }

    private void showCardDialog(String type, GameState.LandResult r){
        String extra="";
        if(r.collectTotal>0) extra="\nОтримано від гравців: ₴"+r.collectTotal;
        if(r.repairTotal>0) extra="\nСума ремонту: ₴"+r.repairTotal;
        new AlertDialog.Builder(this,R.style.GameDialog)
            .setTitle(type).setMessage(r.card.getIcon()+"  "+r.card.getText()+extra)
            .setPositiveButton("OK",(d,w)->{refreshUI();endTurnDelay();})
            .setCancelable(false).show();
    }

    private void showInfo(String title, String msg){
        new AlertDialog.Builder(this,R.style.GameDialog).setTitle(title).setMessage(msg)
            .setPositiveButton("OK",(d,w)->{refreshUI();endTurnDelay();}).setCancelable(false).show();
    }

    private void endTurnDelay(){
        handler.postDelayed(()->{gs.nextTurn();refreshUI();if(gs.isGameOver())goToWin();},400);
    }

    private void goToWin(){
        Intent i=new Intent(this,WinActivity.class);
        i.putExtra("players",(java.io.Serializable)gs.getPlayers());
        i.putExtra("winnerIdx",gs.getWinnerIdx());
        startActivity(i); finish();
    }

    private void refreshUI(){
        tvBank.setText("Банк: ₴"+gs.getBankMoney());
        renderPlayers(); updateLog();
        btnRoll.setEnabled(!gs.isDiceRolled()&&!gs.isGameOver());
    }

    private void updateLog(){
        List<String> logs=gs.getLog();
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<Math.min(logs.size(),4);i++) sb.append(logs.get(i)).append("\n");
        tvLog.setText(sb.toString().trim());
    }

    private void appendLog(String msg){gs.getLog().add(0,msg);updateLog();}

    private void renderBoard(){
        boardGrid.removeAllViews();
        boardGrid.setColumnCount(11); boardGrid.setRowCount(11);
        List<BoardCell> board=gs.getBoard();
        BoardCell[][]grid=new BoardCell[11][11];
        for(BoardCell c:board){int r=c.getGridRow()-1,col=c.getGridCol()-1;if(r>=0&&r<11&&col>=0&&col<11)grid[r][col]=c;}

        for(int r=0;r<11;r++){
            for(int c=0;c<11;c++){
                if(r>=1&&r<=9&&c>=1&&c<=9){
                    if(r==1&&c==1){
                        View center=makeCenterView();
                        GridLayout.LayoutParams lp=new GridLayout.LayoutParams(GridLayout.spec(1,9),GridLayout.spec(1,9));
                        lp.width=0;lp.height=0;lp.setGravity(Gravity.FILL);
                        center.setLayoutParams(lp);boardGrid.addView(center);
                    }
                    continue;
                }
                BoardCell cell=grid[r][c];
                View v=makeCellView(cell);
                GridLayout.LayoutParams lp=new GridLayout.LayoutParams(GridLayout.spec(r,1,1f),GridLayout.spec(c,1,1f));
                lp.width=0;lp.height=0;lp.setGravity(Gravity.FILL);
                v.setLayoutParams(lp);boardGrid.addView(v);
            }
        }
        renderOwnedIndicators(); updateTokenPositions();
    }

    private View makeCellView(BoardCell cell){
        FrameLayout frame=new FrameLayout(this);
        frame.setPadding(1,1,1,1);frame.setBackgroundColor(0xFF111827);
        if(cell==null) return frame;
        frame.setTag(cell.getId());
        LinearLayout inner=new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);inner.setGravity(Gravity.CENTER);
        inner.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
        inner.setBackgroundColor(0xFF111827);
        if(cell.getType()==BoardCell.Type.PROPERTY){
            View bar=new View(this);
            LinearLayout.LayoutParams bp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,dp(4));
            bar.setLayoutParams(bp);bar.setBackgroundColor(GROUP_COLORS[cell.getGroup()]);inner.addView(bar);
        }
        TextView ti=new TextView(this);ti.setText(cell.getIcon());ti.setTextSize(10);ti.setGravity(Gravity.CENTER);inner.addView(ti);
        TextView tn=new TextView(this);tn.setText(cell.getName());tn.setTextSize(5);tn.setTextColor(0xFFE8EAF6);tn.setGravity(Gravity.CENTER);tn.setPadding(1,0,1,0);inner.addView(tn);
        if(cell.getPrice()>0){TextView tp=new TextView(this);tp.setText("₴"+cell.getPrice());tp.setTextSize(4.5f);tp.setTextColor(0xFFF4C542);tp.setGravity(Gravity.CENTER);inner.addView(tp);}
        frame.addView(inner);
        frame.setOnClickListener(v->showCellInfo(cell));
        return frame;
    }

    private void showCellInfo(BoardCell cell){
        if(cell.getType()!=BoardCell.Type.PROPERTY&&cell.getType()!=BoardCell.Type.STATION&&cell.getType()!=BoardCell.Type.UTILITY) return;
        Integer ownerIdx=gs.getOwned().get(cell.getId());
        String ownerText=ownerIdx==null?"Не куплено":"Власник: "+gs.getPlayers().get(ownerIdx).getName();
        int h=gs.getHouses().getOrDefault(cell.getId(),0);
        String hText=h==0?"":(h<5?"\nБудинків: "+h:"\n🏨 Готель");
        String msg=ownerText+hText+(cell.getRent()!=null?"\nОренда: ₴"+cell.getRent()[0]+(cell.getRent().length>4?"\nГотель: ₴"+cell.getRent()[cell.getRent().length-1]:""):"");
        AlertDialog.Builder b=new AlertDialog.Builder(this,R.style.GameDialog).setTitle(cell.getIcon()+"  "+cell.getName()).setMessage(msg).setNegativeButton("Закрити",null);
        if(ownerIdx!=null&&ownerIdx==gs.getCurrentPlayerIdx()&&cell.getType()==BoardCell.Type.PROPERTY&&h<5&&gs.isDiceRolled())
            b.setPositiveButton((h==4?"🏨 Готель":"🏠 Будинок")+" ₴"+cell.getHousePrice(),(d,w)->{
                if(gs.buildHouse(cell.getId())){renderBoard();refreshUI();}
                else Toast.makeText(this,"Недостатньо грошей!",Toast.LENGTH_SHORT).show();});
        b.show();
    }

    private View makeCenterView(){
        FrameLayout f=new FrameLayout(this);f.setBackgroundColor(0xFF0a0f1e);
        LinearLayout inner=new LinearLayout(this);inner.setOrientation(LinearLayout.VERTICAL);inner.setGravity(Gravity.CENTER);
        inner.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
        TextView flag=new TextView(this);flag.setText("🇺🇦");flag.setTextSize(36);flag.setGravity(Gravity.CENTER);inner.addView(flag);
        TextView title=new TextView(this);title.setText("МОНОПОЛІЯ\nУКРАЇНА");title.setTextSize(12);title.setTextColor(0xFFF4C542);title.setGravity(Gravity.CENTER);title.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);inner.addView(title);
        f.addView(inner);return f;
    }

    private void renderOwnedIndicators(){
        for(Map.Entry<Integer,Integer>e:gs.getOwned().entrySet()){
            View cell=boardGrid.findViewWithTag(e.getKey());
            if(cell instanceof FrameLayout){
                FrameLayout frame=(FrameLayout)cell;
                for(int i=frame.getChildCount()-1;i>=0;i--){if("owned".equals(frame.getChildAt(i).getTag()))frame.removeViewAt(i);}
                View dot=new View(this);dot.setTag("owned");
                FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(dp(6),dp(6));
                lp.gravity=Gravity.BOTTOM|Gravity.END;lp.setMargins(0,0,2,2);dot.setLayoutParams(lp);
                dot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(gs.getPlayers().get(e.getValue()).getColor()));
                dot.setBackground(getResources().getDrawable(R.drawable.circle,null));
                frame.addView(dot);
            }
        }
    }

    private void updateTokenPositions(){
        for(int i=0;i<boardGrid.getChildCount();i++){
            View v=boardGrid.getChildAt(i);
            if(v instanceof FrameLayout){FrameLayout f=(FrameLayout)v;
                for(int j=f.getChildCount()-1;j>=0;j--){Object t=f.getChildAt(j).getTag();if(t instanceof String&&((String)t).startsWith("token"))f.removeViewAt(j);}}
        }
        int[][]off={{-6,-6},{6,-6},{-6,6},{6,6}};
        for(int i=0;i<gs.getPlayers().size();i++){
            Player p=gs.getPlayers().get(i);if(p.isBankrupt())continue;
            View cell=boardGrid.findViewWithTag(p.getPosition());
            if(cell instanceof FrameLayout){
                FrameLayout frame=(FrameLayout)cell;
                TextView tok=new TextView(this);tok.setTag("token"+i);tok.setText(p.getAvatar());tok.setTextSize(8);
                FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(dp(14),dp(14));
                lp.gravity=Gravity.CENTER;lp.setMargins(Math.max(0,dp(off[i%4][0])),Math.max(0,dp(off[i%4][1])),0,0);
                tok.setLayoutParams(lp);tok.setGravity(Gravity.CENTER);
                tok.setBackgroundTintList(android.content.res.ColorStateList.valueOf(p.getColor()));
                tok.setBackground(getResources().getDrawable(R.drawable.circle,null));
                frame.addView(tok);
            }
        }
    }

    private void renderPlayers(){
        playersBar.removeAllViews();
        for(int i=0;i<gs.getPlayers().size();i++){
            Player p=gs.getPlayers().get(i);boolean cur=(i==gs.getCurrentPlayerIdx());
            CardView card=new CardView(this);card.setRadius(dp(10));card.setCardElevation(cur?dp(4):0);
            card.setCardBackgroundColor(cur?0xFF1a2440:0xFF111827);
            LinearLayout inner=new LinearLayout(this);inner.setOrientation(LinearLayout.VERTICAL);inner.setPadding(dp(10),dp(8),dp(10),dp(8));
            LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.HORIZONTAL);row.setGravity(Gravity.CENTER_VERTICAL);
            TextView av=new TextView(this);av.setText(p.getAvatar());av.setTextSize(16);row.addView(av);
            TextView nm=new TextView(this);nm.setText("  "+p.getName());nm.setTextSize(11);nm.setTextColor(cur?p.getColor():0xFF9BA8C0);nm.setTypeface(cur?android.graphics.Typeface.DEFAULT_BOLD:android.graphics.Typeface.DEFAULT);row.addView(nm);
            inner.addView(row);
            TextView mn=new TextView(this);mn.setText("₴"+p.getMoney());mn.setTextSize(14);mn.setTextColor(p.isBankrupt()?0xFFD62B2B:0xFFF4C542);mn.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);inner.addView(mn);
            TextView st=new TextView(this);st.setText(gs.getBoard().get(p.getPosition()).getName()+(p.isInJail()?" ⛓️":"")+(p.isBankrupt()?" 💀":""));st.setTextSize(9);st.setTextColor(0xFF4A5568);inner.addView(st);
            card.addView(inner);
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(dp(110),LinearLayout.LayoutParams.WRAP_CONTENT);lp.setMargins(0,0,dp(8),0);card.setLayoutParams(lp);
            playersBar.addView(card);
        }
    }

    private void animateDie(TextView die){ObjectAnimator.ofFloat(die,"rotationY",0f,360f).setDuration(400).start();}
    private int dp(int v){return(int)(v*getResources().getDisplayMetrics().density);}

    @Override public void onBackPressed(){
        new AlertDialog.Builder(this,R.style.GameDialog).setTitle("Вийти з гри?").setMessage("Прогрес буде втрачено.")
            .setPositiveButton("Вийти",(d,w)->super.onBackPressed()).setNegativeButton("Продовжити",null).show();
    }
          }
