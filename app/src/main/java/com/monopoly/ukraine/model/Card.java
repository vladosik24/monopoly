package com.monopoly.ukraine.model;

import java.io.Serializable;

public class Card implements Serializable {
    public enum Action { MONEY, GOTO, COLLECT, REPAIR, JAIL }

    private String text, icon;
    private Action action;
    private int amount, target;

    public Card(String icon, String text, int amount) {
        this.icon=icon; this.text=text; this.action=Action.MONEY; this.amount=amount;
    }
    public Card(String icon, String text, Action action, int amount) {
        this.icon=icon; this.text=text; this.action=action; this.amount=amount;
    }
    public Card(String icon, String text, Action action, int amount, int target) {
        this.icon=icon; this.text=text; this.action=action; this.amount=amount; this.target=target;
    }

    public String getText() { return text; }
    public String getIcon() { return icon; }
    public Action getAction() { return action; }
    public int getAmount() { return amount; }
    public int getTarget() { return target; }
}
