package com.sasaki.algorithms;

import java.util.ArrayList;

public class CircleWrap {
    public CircleWrap wrappedCircle = null;//hacky d3 reference thing

    ArrayList<CircleWrap> children = new ArrayList<>();
    public Object id;
    public CircleWrap next;
    public CircleWrap previous;

    double x;
    double y;
    double r = 999;

    public CircleWrap(String id) {
        this.id = id;
    }

    public CircleWrap(CircleWrap circle) {
        this.wrappedCircle = circle;
        this.r = 1010101;//TEMP to check if this is used (shouldn't be!)
    }

    public CircleWrap() {
    }

    public CircleWrap(double radius) {
        this.r = radius;
    }

    public CircleWrap(double x, double y, double r) {
        if (r < 0) throw new java.lang.IllegalArgumentException("CircleWrap cannot have negative radius: " + r);
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public void addChild(CircleWrap child) {
        this.children.add(child);
    }

    public void setID(String id) {
        this.id = id;
    }

    public Object getID() {
        return this.id;
    }

    public void applyPositionToChildren() {
        if (hasChildren()) {
            for (CircleWrap child : this.children) {
                child.x += this.x;
                child.y += this.y;
                child.applyPositionToChildren();
            }
        }
    }

    public String toConStr() {
        return "new CircleWrap(" + this.x + "," + this.y + "," + this.r + ")";
    }

    @Override
    public String toString() {
        return "[CircleWrap:" + this.x + "," + this.y + "," + this.r + "]";
    }

    public boolean hasChildren() {
        return this.children != null && this.children.size() > 0;
    }
}
