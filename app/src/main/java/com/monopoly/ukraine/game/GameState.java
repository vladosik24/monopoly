package com.monopoly.ukraine.game;

import com.monopoly.ukraine.model.BoardCell;
import com.monopoly.ukraine.model.Card;
import com.monopoly.ukraine.model.Player;

import java.io.Serializable;
import java.util.*;

public class GameState implements Serializable {

    private List<BoardCell> board;
    private List<Card> chanceCards, chestCards;
    private List<Player> players;
    private int currentPlayerIdx, bankMoney, chanceIdx, chestIdx;
    private Map<Integer,Integer> owned, houses;
    private boolean diceRolled, gameOver;
    private int winnerIdx = -1;
    private int lastDie1, lastDie2;
    private List<String> log = new ArrayList<>();
    private final Random rng = new Random();

    public GameState(List<Player> players) {
        this.players = players;
        this.bankMoney = 150000;
        this.owned = new HashMap<>();
        this.houses = new HashMap<>();
        buildBoard();
        buildCards();
    }

    private void buildBoard() {
        board = new ArrayList<>();
        board.add(new BoardCell(0,  "СТАРТ",              BoardCell.Type.CORNER,"🏁",11,1));
        board.add(new BoardCell(1,  "Кривий Ріг",         "⛏️",600, new int[]{20,100,300,450,625,750},   500,0,11,2));
        board.add(new BoardCell(2,  "Скарбниця",          BoardCell.Type.CHEST, "📦",11,3));
        board.add(new BoardCell(3,  "Запоріжжя",          "⚡",600, new int[]{40,200,600,900,1100,1250},  500,0,11,4));
        board.add(new BoardCell(4,  "Мито",               "📋",200,true,11,5));
        board.add(new BoardCell(5,  "Укрзалізниця",       "🚂",2000,new int[]{250,500,1000,2000},11,6));
        board.add(new BoardCell(6,  "Дніпро",             "🌊",1000,new int[]{60,300,900,1600,2500,3500}, 1000,1,11,7));
        board.add(new BoardCell(7,  "Шанс",               BoardCell.Type.CHANCE,"❓",11,8));
        board.add(new BoardCell(8,  "Миколаїв",           "⚓",1000,new int[]{60,300,900,1600,2500,3500}, 1000,1,11,9));
        board.add(new BoardCell(9,  "Херсон",             "🌻",1200,new int[]{80,400,1000,1800,3000,4500},1000,1,11,10));
        board.add(new BoardCell(10, "ВЯЗНИЦЯ",            BoardCell.Type.CORNER,"⛓️",11,11));
        board.add(new BoardCell(11, "Харків",             "🏭",1400,new int[]{100,500,1500,2200,3600,5000},1500,2,10,11));
        board.add(new BoardCell(12, "Укртелеком",         "📡",1500,9,11));
        board.add(new BoardCell(13, "Полтава",            "🎭",1400,new int[]{100,500,1500,2200,3600,5000},1500,2,8,11));
        board.add(new BoardCell(14, "Суми",               "🌲",1600,new int[]{120,600,1800,2600,4000,5500},1500,2,7,11));
        board.add(new BoardCell(15, "Аеропорт Бориспіль", "✈️",2000,new int[]{250,500,1000,2000},6,11));
        board.add(new BoardCell(16, "Чернігів",           "⛪",1800,new int[]{140,700,2000,3000,4500,6250},2000,3,5,11));
        board.add(new BoardCell(17, "Скарбниця",          BoardCell.Type.CHEST, "📦",4,11));
        board.add(new BoardCell(18, "Житомир",            "🌿",1800,new int[]{140,700,2000,3000,4500,6250},2000,3,3,11));
        board.add(new BoardCell(19, "Вінниця",            "💦",2000,new int[]{160,800,2200,3300,5000,7000},2000,3,2,11));
        board.add(new BoardCell(20, "ПАРКІНГ",            BoardCell.Type.CORNER,"🅿️",1,11));
        board.add(new BoardCell(21, "Хмельницький",       "🏰",2200,new int[]{180,900,2500,4000,5500,7500},2500,4,1,10));
        board.add(new BoardCell(22, "Шанс",               BoardCell.Type.CHANCE,"❓",1,9));
        board.add(new BoardCell(23, "Рівне",              "💎",2200,new int[]{180,900,2500,4000,5500,7500},2500,4,1,8));
        board.add(new BoardCell(24, "Луцьк",              "🦁",2400,new int[]{200,1000,3000,4500,6000,8000},2500,4,1,7));
        board.add(new BoardCell(25, "Морський порт",      "🚢",2000,new int[]{250,500,1000,2000},1,6));
        board.add(new BoardCell(26, "Тернопіль",          "🏔️",2600,new int[]{220,1100,3300,5000,7000,9000},3000,5,1,5));
        board.add(new BoardCell(27, "Чернівці",           "🌺",2600,new int[]{220,1100,3300,5000,7000,9000},3000,5,1,4));
        board.add(new BoardCell(28, "Нафтогаз",           "⛽",1500,1,3));
        board.add(new BoardCell(29, "Ужгород",            "🏞️",2800,new int[]{240,1200,3600,5500,7500,9500},3000,5,1,2));
        board.add(new BoardCell(30, "ЙДИ ДО ВЯЗНИЦІ",    BoardCell.Type.CORNER,"👮",1,1));
        board.add(new BoardCell(31, "Одеса",              "🌊",3000,new int[]{260,1300,3900,5750,8000,11000},3500,6,2,1));
        board.add(new BoardCell(32, "Черкаси",            "🌸",3000,new int[]{260,1300,3900,5750,8000,11000},3500,6,3,1));
        board.add(new BoardCell(33, "Скарбниця",          BoardCell.Type.CHEST, "📦",4,1));
        board.add(new BoardCell(34, "Хортиця",            "⚔️",3200,new int[]{280,1500,4500,6250,8750,12500},3500,6,5,1));
        board.add(new BoardCell(35, "Аеропорт Схід",      "🛫",2000,new int[]{250,500,1000,2000},6,1));
        board.add(new BoardCell(36, "Шанс",               BoardCell.Type.CHANCE,"❓",7,1));
        board.add(new BoardCell(37, "Львів",              "🦁",3500,new int[]{350,1750,5000,7000,9000,12500},4000,7,8,1));
        board.add(new BoardCell(38, "Акциз",              "📋",100,true,9,1));
        board.add(new BoardCell(39, "Київ",               "🏛️",4000,new int[]{500,2000,6000,9000,12000,15000},4000,7,10,1));
    }

    private void buildCards() {
        chanceCards = new ArrayList<>(Arrays.asList(
            new Card("🎁","Премія від держави! Отримайте ₴2000.",2000),
            new Card("🚔","Штраф. Заплатіть ₴500.",-500),
            new Card("🏁","Йдіть до СТАРТУ! Отримайте ₴2000.",Card.Action.GOTO,2000,0),
            new Card("🤝","Отримайте ₴1000 від кожного гравця.",Card.Action.COLLECT,1000),
            new Card("🔧","Ремонт: ₴400 за будинок.",Card.Action.REPAIR,400),
            new Card("🎰","Виграш у лотерею ₴1500!",1500),
            new Card("🏦","Банк повертає ₴500.",500),
            new Card("⛓️","Прямо до вязниці!",Card.Action.JAIL,0),
            new Card("🎪","Відвідали фестиваль. Отримайте ₴300.",300),
            new Card("📉","Фондовий ринок впав. Заплатіть ₴800.",-800)
        ));
        Collections.shuffle(chanceCards);

        chestCards = new ArrayList<>(Arrays.asList(
            new Card("🎂","День народження! ₴200 від кожного.",Card.Action.COLLECT,200),
            new Card("🎨","Продали картину. Отримайте ₴1000.",1000),
            new Card("🏥","Лікарня. Заплатіть ₴500.",-500),
            new Card("📊","Повернення податку ₴300!",300),
            new Card("🃏","Виграш в казино — ₴2000!",2000),
            new Card("🅿️","Штраф за паркування ₴200.",-200),
            new Card("🏠","Спадщина ₴5000!",5000),
            new Card("💡","Комунальні послуги: ₴800.",-800),
            new Card("🚀","Стартап вистрілив! Отримайте ₴2500.",2500),
            new Card("🌾","Врожай знищено. Заплатіть ₴600.",-600)
        ));
        Collections.shuffle(chestCards);
    }

    public int[] rollDice() {
        lastDie1 = rng.nextInt(6)+1; lastDie2 = rng.nextInt(6)+1;
        diceRolled = true;
        return new int[]{lastDie1, lastDie2};
    }

    public LandResult moveCurrentPlayer(int steps) {
        Player p = getCurrentPlayer();
        int oldPos = p.getPosition();
        int newPos = (oldPos + steps) % 40;
        boolean passedGo = newPos < oldPos || (oldPos + steps >= 40);
        if (passedGo) { p.addMoney(2000); bankMoney -= 2000; }
        p.setPosition(newPos);
        addLog(p.getAvatar()+" "+p.getName()+" → "+board.get(newPos).getName());
        return processLanding(p.getIndex(), newPos, passedGo);
    }

    public LandResult processLanding(int pIdx, int pos, boolean passedGo) {
        Player p = players.get(pIdx);
        BoardCell cell = board.get(pos);
        LandResult r = new LandResult();
        r.passedGo = passedGo; r.cell = cell;

        switch (cell.getType()) {
            case CORNER:
                if (cell.getId()==30) { p.setPosition(10); p.setInJail(true); p.setJailTurns(0); r.event=LandResult.Event.JAIL; }
                else r.event = LandResult.Event.NONE;
                break;
            case TAX:
                p.subtractMoney(cell.getTaxAmount()); bankMoney+=cell.getTaxAmount();
                r.event = LandResult.Event.TAX; break;
            case CHANCE:
                r.card = chanceCards.get(chanceIdx++ % chanceCards.size());
                r.event = LandResult.Event.CHANCE; applyCard(r.card, pIdx, r); break;
            case CHEST:
                r.card = chestCards.get(chestIdx++ % chestCards.size());
                r.event = LandResult.Event.CHEST; applyCard(r.card, pIdx, r); break;
            case PROPERTY: case STATION: case UTILITY:
                Integer ownerIdx = owned.get(cell.getId());
                if (ownerIdx==null) { r.event = LandResult.Event.CAN_BUY; }
                else if (ownerIdx==pIdx) { r.event = LandResult.Event.OWN_PROPERTY; }
                else {
                    Player owner = players.get(ownerIdx);
                    if (owner.isBankrupt()) { r.event=LandResult.Event.NONE; break; }
                    int rent = calculateRent(cell, ownerIdx);
                    p.subtractMoney(rent); owner.addMoney(rent);
                    checkBankruptcy(pIdx);
                    r.event=LandResult.Event.PAY_RENT; r.rentAmount=rent; r.rentOwner=owner;
                }
                break;
        }
        checkWin(); return r;
    }

    private void applyCard(Card card, int pIdx, LandResult r) {
        Player p = players.get(pIdx);
        switch(card.getAction()) {
            case MONEY:
                if(card.getAmount()>=0){p.addMoney(card.getAmount());bankMoney-=card.getAmount();}
                else{p.subtractMoney(-card.getAmount());bankMoney+=(-card.getAmount());}
                checkBankruptcy(pIdx); break;
            case GOTO:
                p.setPosition(card.getTarget());
                if(card.getAmount()>0){p.addMoney(card.getAmount());bankMoney-=card.getAmount();} break;
            case COLLECT:
                int total=0;
                for(int i=0;i<players.size();i++){
                    if(i!=pIdx&&!players.get(i).isBankrupt()){
                        players.get(i).subtractMoney(card.getAmount());
                        p.addMoney(card.getAmount()); total+=card.getAmount();}}
                r.collectTotal=total; break;
            case REPAIR:
                int rep=0;
                for(Map.Entry<Integer,Integer> e:owned.entrySet())
                    if(e.getValue()==pIdx) rep+=houses.getOrDefault(e.getKey(),0)*card.getAmount();
                p.subtractMoney(rep); bankMoney+=rep; checkBankruptcy(pIdx); r.repairTotal=rep; break;
            case JAIL:
                p.setPosition(10); p.setInJail(true); p.setJailTurns(0);
                r.event=LandResult.Event.JAIL; break;
        }
    }

    public int calculateRent(BoardCell cell, int ownerIdx) {
        if(cell.getType()==BoardCell.Type.STATION){
            long cnt=board.stream().filter(b->b.getType()==BoardCell.Type.STATION&&Integer.valueOf(ownerIdx).equals(owned.get(b.getId()))).count();
            int[]r=cell.getRent(); return r[(int)Math.min(cnt-1,r.length-1)];
        }
        if(cell.getType()==BoardCell.Type.UTILITY){
            long cnt=board.stream().filter(b->b.getType()==BoardCell.Type.UTILITY&&Integer.valueOf(ownerIdx).equals(owned.get(b.getId()))).count();
            return cnt>=2?1200:600;
        }
        int h=houses.getOrDefault(cell.getId(),0);
        int[]r=cell.getRent(); return r[Math.min(h,r.length-1)];
    }

    public boolean buyProperty(int cellId) {
        BoardCell cell=board.get(cellId); Player p=getCurrentPlayer();
        if(p.getMoney()<cell.getPrice()) return false;
        p.subtractMoney(cell.getPrice()); bankMoney+=cell.getPrice();
        owned.put(cellId,p.getIndex()); return true;
    }

    public boolean buildHouse(int cellId) {
        BoardCell cell=board.get(cellId); Player p=getCurrentPlayer();
        int cur=houses.getOrDefault(cellId,0);
        if(cur>=5||p.getMoney()<cell.getHousePrice()) return false;
        p.subtractMoney(cell.getHousePrice()); bankMoney+=cell.getHousePrice();
        houses.put(cellId,cur+1); return true;
    }

    public boolean processJailTurn(boolean isDouble) {
        Player p=getCurrentPlayer(); p.setJailTurns(p.getJailTurns()+1);
        if(isDouble){p.setInJail(false);p.setJailTurns(0);return true;}
        if(p.getJailTurns()>=3){p.subtractMoney(500);bankMoney+=500;p.setInJail(false);p.setJailTurns(0);return true;}
        return false;
    }

    private void checkBankruptcy(int pIdx) {
        Player p=players.get(pIdx);
        if(p.getMoney()<=0){
            p.setBankrupt(true); p.setMoney(0);
            owned.entrySet().removeIf(e->e.getValue()==pIdx);
            addLog("💀 "+p.getName()+" збанкрутував!");
        }
    }

    private void checkWin() {
        long active=players.stream().filter(p->!p.isBankrupt()).count();
        if(active<=1){
            gameOver=true;
            for(int i=0;i<players.size();i++) if(!players.get(i).isBankrupt()){winnerIdx=i;return;}
            winnerIdx=0;
            for(int i=1;i<players.size();i++) if(players.get(i).getMoney()>players.get(winnerIdx).getMoney()) winnerIdx=i;
        }
    }

    public void nextTurn() {
        diceRolled=false;
        do { currentPlayerIdx=(currentPlayerIdx+1)%players.size(); }
        while(players.get(currentPlayerIdx).isBankrupt()&&!gameOver);
    }

    private void addLog(String msg){log.add(0,msg);if(log.size()>30)log.remove(log.size()-1);}

    public List<BoardCell> getBoard(){return board;}
    public List<Player> getPlayers(){return players;}
    public Player getCurrentPlayer(){return players.get(currentPlayerIdx);}
    public int getCurrentPlayerIdx(){return currentPlayerIdx;}
    public int getBankMoney(){return bankMoney;}
    public Map<Integer,Integer> getOwned(){return owned;}
    public Map<Integer,Integer> getHouses(){return houses;}
    public boolean isDiceRolled(){return diceRolled;}
    public List<String> getLog(){return log;}
    public boolean isGameOver(){return gameOver;}
    public int getWinnerIdx(){return winnerIdx;}
    public int getLastDie1(){return lastDie1;}
    public int getLastDie2(){return lastDie2;}

    public static class LandResult implements Serializable {
        public enum Event{NONE,JAIL,TAX,CHANCE,CHEST,CAN_BUY,PAY_RENT,OWN_PROPERTY}
        public Event event=Event.NONE;
        public BoardCell cell; public Card card;
        public boolean passedGo;
        public int rentAmount, collectTotal, repairTotal;
        public Player rentOwner;
    }
                  }
