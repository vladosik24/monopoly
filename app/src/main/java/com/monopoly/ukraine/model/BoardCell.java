package com.monopoly.ukraine.model;

import java.io.Serializable;

public class BoardCell implements Serializable {
    public enum Type { CORNER, PROPERTY, STATION, UTILITY, TAX, CHANCE, CHEST }

    private int id, price, housePrice, group, taxAmount, gridRow, gridCol;
    private String name, icon;
    private Type type;
    private int[] rent;

    // Corner / Chance / Chest
    public BoardCell(int id, String name, Type type, String icon, int row, int col) {
        this.id=id; this.name=name; this.type=type; this.icon=icon; this.gridRow=row; this.gridCol=col;
    }
    // Property
    public BoardCell(int id, String name, String icon, int price, int[] rent, int housePrice, int group, int row, int col) {
        this.id=id; this.name=name; this.type=Type.PROPERTY; this.icon=icon;
        this.price=price; this.rent=rent; this.housePrice=housePrice; this.group=group; this.gridRow=row; this.gridCol=col;
    }
    // Station
    public BoardCell(int id, String name, String icon, int price, int[] rent, int row, int col) {
        this.id=id; this.name=name; this.type=Type.STATION; this.icon=icon;
        this.price=price; this.rent=rent; this.gridRow=row; this.gridCol=col;
    }
    // Utility
    public BoardCell(int id, String name, String icon, int price, int row, int col) {
        this.id=id; this.name=name; this.type=Type.UTILITY; this.icon=icon;
        this.price=price; this.gridRow=row; this.gridCol=col;
    }
    // Tax
    public BoardCell(int id, String name, String icon, int taxAmount, boolean isTax, int row, int col) {
        this.id=id; this.name=name; this.type=Type.TAX; this.icon=icon;
        this.taxAmount=taxAmount; this.gridRow=row; this.gridCol=col;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Type getType() { return type; }
    public String getIcon() { return icon; }
    public int getPrice() { return price; }
    public int[] getRent() { return rent; }
    public int getHousePrice() { return housePrice; }
    public int getGroup() { return group; }
    public int getTaxAmount() { return taxAmount; }
    public int getGridRow() { return gridRow; }
    public int getGridCol() { return gridCol; }
}
