package com.ai.model;


public class Point {

    public boolean equals(Point p) {
        // TODO Auto-generated method stub
        return this.getX() == p.getX() && this.getY() == p.getY();
    }

    @Override
    public boolean equals(Object p) {
        return this.equals((Point) p);
    }

    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void move(int xDelta, int yDelta) {
        x += xDelta;
        y += yDelta;
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public static void main(String[] args) {
        Point p = new Point(3, 5);
        Point p2 = new Point(3, 4);
        System.out.println(p.equals(p2));
    }
}