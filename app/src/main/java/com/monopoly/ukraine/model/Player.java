package com.monopoly.ukraine.model;

import java.io.Serializable;

public class Player implements Serializable {
    private String name, avatar;
    private int color, money, position, jailTurns, index;
    private boolean bankrupt, inJail;

    public Player(String name, String avatar, int color, int index) {
        this.name = name; this.avatar = avatar; this.color = color; this.index = index;
        this.money = 15000; this.position = 0;
    }

    public String getName() { return name; }
    public String getAvatar() { return avatar; }
    public int getColor() { return color; }
    public int getMoney() { return money; }
    public int getPosition() { return position; }
    public boolean isBankrupt() { return bankrupt; }
    public boolean isInJail() { return inJail; }
    public int getJailTurns() { return jailTurns; }
    public int getIndex() { return index; }

    public void setName(String n) { name = n; }
    public void setAvatar(String a) { avatar = a; }
    public void setMoney(int m) { money = m; }
    public void setPosition(int p) { position = p; }
    public void setBankrupt(boolean b) { bankrupt = b; }
    public void setInJail(boolean j) { inJail = j; }
    public void setJailTurns(int t) { jailTurns = t; }

    public void addMoney(int a) { money += a; }
    public void subtractMoney(int a) { money -= a; if (money < 0) money = 0; }
    public void move(int steps) { position = (position + steps) % 40; }
}
