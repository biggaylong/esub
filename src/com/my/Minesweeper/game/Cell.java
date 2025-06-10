package com.my.Minesweeper.game;

// 单元格类，封装单元格的属性和方法
public class Cell {
    private boolean hasMine;
    private int adjacentMines;
    private boolean isClicked;
    private boolean isMarked;

    public Cell(boolean hasMine) {
        this.hasMine = hasMine;
        this.adjacentMines = 0;
        this.isClicked = false;
        this.isMarked = false;
    }

    public boolean hasMine() {
        return hasMine;
    }

    public int getAdjacentMines() {
        return adjacentMines;
    }

    public void setAdjacentMines(int adjacentMines) {
        this.adjacentMines = adjacentMines;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public void setMarked(boolean marked) {
        isMarked = marked;
    }
}