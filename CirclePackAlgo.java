package com.sasaki.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class CirclePackAlgo {
    private boolean verbose;

    public CircleWrap enclose(ArrayList<CircleWrap> circles) {

        for (CircleWrap p : circles) {
            println("pre shuffle: " + p.x + "," + p.y + "," + p.r);
        }


        int i = 0;
        circles = new ArrayList<>(circles);
        Collections.shuffle(circles);
        int n = circles.size();
        ArrayList<CircleWrap> B = new ArrayList<>(n);
        CircleWrap p;
        CircleWrap e = null;

        while (i < n) {
            p = circles.get(i);
            println(i + ": Circle: " + p.x + "," + p.y + "," + p.r);
            if (e != null && enclosesWeak(e, p)) {
                ++i;
            } else {
                B = extendBasis(B, p);
                e = encloseBasis(B);
                i = 0;
            }
        }
        return e;
    }

    public void Test() {
        verbose = true;
        ArrayList<CircleWrap> B = new ArrayList<>();
        B.add(new CircleWrap(232.1594563990297, -372.01783603186004, 20.0));
        B.add(new CircleWrap(409.3978166799043, -89.98110903685964, 20.0));
        extendBasis(B, new CircleWrap(399.60713913056816, -61.62370024964979, 10.0));
    }

    private ArrayList<CircleWrap> extendBasis(ArrayList<CircleWrap> B, CircleWrap p) {
        int i;
        int j;

        StringBuilder extendBasisStr = new StringBuilder("ArrayList<CircleWrap> B = new ArrayList<>();\n");
        for (i = 0; i < B.size(); ++i) {
            extendBasisStr.append("B.add(" + B.get(i).toConStr() + ");\n");
        }
        extendBasisStr.append("extendBasis(B, " + p.toConStr() + ");\n");

        if (enclosesWeakAll(p, B)) {
            return new ArrayList<>(Arrays.asList(p));
        }

        for (i = 0; i < B.size(); ++i) {
            CircleWrap nodeCircle = B.get(i);
            println("enclosesNot from step 2");
            if (enclosesNot(p, nodeCircle) && enclosesWeakAll(encloseBasis2(nodeCircle, p), B)) {
                return new ArrayList<>(Arrays.asList(nodeCircle, p));
            }
        }

        for (i = 0; i < B.size() - 1; ++i) {
            for (j = i + 1; j < B.size(); ++j) {
                CircleWrap nodeCirclei = B.get(i);
                CircleWrap nodeCirclej = B.get(j);
                println("enclosesNot from step 3");

                if (enclosesNot(encloseBasis2(nodeCirclei, nodeCirclej), p)
                        && enclosesNot(encloseBasis2(nodeCirclei, p), nodeCirclej)
                        && enclosesNot(encloseBasis2(nodeCirclej, p), nodeCirclei)
                        && enclosesWeakAll(encloseBasis3(nodeCirclei, nodeCirclej, p), B)) {
                    return new ArrayList<>(Arrays.asList(nodeCirclei, nodeCirclej, p));
                }
            }
        }
        System.out.println("---------- Failed to extend basis -----------");
        System.out.println(extendBasisStr.toString());
        throw new java.lang.Error("could not extend basis");
    }

    private void println(String msg) {
        if (verbose) {
            System.out.println(msg);
        }
    }

    private boolean enclosesWeak(CircleWrap a, CircleWrap b) {
        double dr = (a.r - b.r + 1e-6);
        double dx = (b.x - a.x);
        double dy = (b.y - a.y);
        boolean ans = dr > 0 && (dr * dr) > (dx * dx + dy * dy);

        println("enclosesWeak: " + ans + " : " + a.toString() + " vs " + b.toString());
        println("dr: " + dr + " dx: " + dx + " dy " + dy + ": ? :" + (dr * dr) + ">" + (dx * dx + dy * dy));
        return ans;
    }

    private boolean enclosesWeakAll(CircleWrap a, ArrayList<CircleWrap> B) {
        println("enclosesWeakAll..." + B.size());
        for (int i = 0; i < B.size(); ++i) {
            println(i + " enclosesWeak?");
            if (!enclosesWeak(a, B.get(i))) {
                println("enclosesWeakAll: false");

                return false;
            }
        }
        println("enclosesWeakAll: true");

        return true;
    }

    private boolean enclosesNot(CircleWrap a, CircleWrap b) {
        double dr = (a.r - b.r);
        double dx = (b.x - a.x);
        double dy = (b.y - a.y);
        boolean ans = dr < 0 || dr * dr < dx * dx + dy * dy;
        println("enclosesNot: " + ans);
        return ans;
    }

    private CircleWrap encloseBasis(ArrayList<CircleWrap> B) {
        switch (B.size()) {
            case 1:
                return encloseBasis1(B.get(0));
            case 2:
                return encloseBasis2(B.get(0), B.get(1));
            case 3:
                return encloseBasis3(B.get(0), B.get(1), B.get(2));
        }
        return null;
    }

    private CircleWrap encloseBasis1(CircleWrap a) {
        return new CircleWrap(a.x, a.y, a.r);
    }

    private CircleWrap encloseBasis2(CircleWrap a, CircleWrap b) {
        double x1 = a.x;
        double y1 = a.y;
        double r1 = a.r;
        double x2 = b.x;
        double y2 = b.y;
        double r2 = b.r;
        double x21 = x2 - x1;
        double y21 = y2 - y1;
        double r21 = r2 - r1;
        double l = Math.sqrt(x21 * x21 + y21 * y21);

        //FOUND ISSUE: these are Ls not 1s !!!!!!!!!!!!!!!!!!!!!
        double x = (x1 + x2 + x21 / l * r21) / 2.f;
        double y = (y1 + y2 + y21 / l * r21) / 2.f;
        double r = (l + r1 + r2) / 2.f;


        println("encloseBasis2... " + a.toString() + " : " + b.toString());
        println("encloseBasis2: " + x + "," + y + "," + r);
        return new CircleWrap(x, y, r);
    }

    private CircleWrap encloseBasis3(CircleWrap a, CircleWrap b, CircleWrap c) {
        double x1 = a.x;
        double y1 = a.y;
        double r1 = a.r;
        double x2 = b.x;
        double y2 = b.y;
        double r2 = b.r;
        double x3 = c.x;
        double y3 = c.y;
        double r3 = c.r;
        double a2 = x1 - x2;
        double a3 = x1 - x3;
        double b2 = y1 - y2;
        double b3 = y1 - y3;
        double c2 = r2 - r1;
        double c3 = r3 - r1;
        double d1 = x1 * x1 + y1 * y1 - r1 * r1;
        double d2 = d1 - x2 * x2 - y2 * y2 + r2 * r2;
        double d3 = d1 - x3 * x3 - y3 * y3 + r3 * r3;
        double ab = a3 * b2 - a2 * b3;
        double xa = (b2 * d3 - b3 * d2) / (ab * 2) - x1;
        double xb = (b3 * c2 - b2 * c3) / ab;
        double ya = (a3 * d2 - a2 * d3) / (ab * 2) - y1;
        double yb = (a2 * c3 - a3 * c2) / ab;
        double A = xb * xb + yb * yb - 1;
        double B = 2 * (r1 + xa * xb + ya * yb);
        double C = xa * xa + ya * ya - r1 * r1;
        double r;
        if (A != 0) {
            r = -(B + Math.sqrt(B * B - 4 * A * C)) / (2 * A);
        } else {
            r = -(C / B);
        }
        double x = x1 + xa + xb * r;
        double y = y1 + ya + yb * r;

        return new CircleWrap(x, y, r);
    }

    private void place(CircleWrap a, CircleWrap b, CircleWrap c) {
        double ax = a.x;
        double ay = a.y;
        double ar = a.r;
        double br = b.r;
        double cr = c.r;

        double da = br + cr;
        double db = ar + cr;
        double dx = b.x - ax;
        double dy = b.y - ay;
        double dc = dx * dx + dy * dy;

        if (dc != 0) {
            double x = 0.5 + ((db *= db) - (da *= da)) / (2 * dc);
            double y = Math.sqrt(Math.max(0, 2 * da * (db + dc) - (db -= dc) * db - da * da)) / (2 * dc);
            double cx = ax + x * dx + y * dy;
            double cy = ay + x * dy - y * dx;
            c.x = (cx);
            c.y = (cy);
        } else {
            c.x = (ax + db);
            c.y = (ay);
        }
    }

    private boolean intersects(CircleWrap a, CircleWrap b) {
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        double dr = a.r + b.r;
        //ANOTHER ISSUE FIXED: 1e6 NOT the same as 1e-6 !!!!!!!!!!!!!!!
        return dr * dr - 1e-6 > dx * dx + dy * dy;
    }

    private double score(CircleWrap node) {
        CircleWrap a = node.wrappedCircle;
        CircleWrap b = node.next.wrappedCircle;
        double ab = a.r + b.r;
        double dx = (a.x * b.r + b.x * a.r) / ab;
        double dy = (a.y * b.r + b.y * a.r) / ab;
        return dx * dx + dy * dy;
    }

    public double packEnclose(ArrayList<CircleWrap> circles) {
        // place first circle
        CircleWrap a = circles.get(0);
        int n = circles.size();

        if (n == 0) return 0;

        a.x = 0;
        a.y = 0;

        // place second circle
        CircleWrap b = circles.get(1);
        a.x = -b.r;
        b.x = a.r;
        b.y = 0;
        if (!(n > 2)) return a.r + b.r;

        // place third circle
        CircleWrap c = circles.get(2);
        place(b, a, c);

        // front chain
        a = new CircleWrap(a);
        b = new CircleWrap(b);
        c = new CircleWrap(c);

        a.next = c.previous = b;
        b.next = a.previous = c;
        c.next = b.previous = a;


        pack:
        for (int i = 3; i < n; ++i) {
            c = circles.get(i);
            place(a.wrappedCircle, b.wrappedCircle, c);
            c = new CircleWrap(c);

            CircleWrap j = b.next;
            CircleWrap k = a.previous;
            double sj = b.wrappedCircle.r;
            double sk = a.wrappedCircle.r;

            do {
                if (sj <= sk) {
                    if (intersects(j.wrappedCircle, c.wrappedCircle)) {
                        b = j;
                        a.next = b;
                        b.previous = a;
                        --i;
                        continue pack;
                    }
                    sj += j.wrappedCircle.r;
                    j = j.next;
                } else {
                    if (intersects(k.wrappedCircle, c.wrappedCircle)) {
                        a = k;
                        a.next = b;
                        b.previous = a;
                        --i;
                        continue pack;
                    }
                    sk += k.wrappedCircle.r;
                    k = k.previous;
                }
            } while (j != k.next);

            c.previous = a;
            c.next = b;
            a.next = b.previous = b = c;

            double aa = score(a);
            while ((c = c.next) != b) {
                double ca;
                if ((ca = score(c)) < aa) {
                    a = c;
                    aa = ca;
                }
            }
            b = a.next;
        }

        // Compute the enclosing circle of the front chain.
        ArrayList<CircleWrap> arr = new ArrayList<>();
        arr.add(b.wrappedCircle);
        c = b;

        int safety = 10000;
        while ((c = c.next) != b) {
            if (--safety == 0) {
                break;
            }
            arr.add(c.wrappedCircle);
        }

//        ArrayList<CircleWrap> arr2 = new ArrayList<>();

        //THIS is bizarre, but the order is different in D3 vs Java. I can't figure out why.
        //ultimately the order doesn't matter, but it makes comparisons difficult
        //hack to reorder
//        for (int i = 5; i < arr.size(); i++) {
//            CircleWrap p = arr.get(i);
//            arr2.add(p);
//        }
//        for (int i = 0; i < 5; i++) {
//            CircleWrap p = arr.get(i);
//            arr2.add(p);
//        }

        for (CircleWrap p : arr) {
            println("n: " + p.x + "," + p.y + "," + p.r);
        }

        c = enclose(arr);

        //Translate the circles to put the enclosing circle around the origin.
        for (int i = 0; i < n; ++i) {
            a = circles.get(i);
            a.x -= c.x;
            a.y -= c.y;
        }

        return c.r;
    }

    public double packHierarchy(ArrayList<CircleWrap> circles) {
        double r = 0;
        if (circles != null && circles.size() > 0) {
            //pack the children first because the radius is determined by how the children get packed (recursive)
            for (CircleWrap circle : circles) {
                if (circle.hasChildren()) {
                    circle.r = packHierarchy(circle.children);
                }
            }
            //now that each circle has a radius set by its children, pack the circles at this level
            r = packEnclose(circles);
        }
        return r;
    }

    public double packHierarchyAndShift(ArrayList<CircleWrap> circles) {
        double r = packHierarchy(circles);
        for (CircleWrap circle : circles) {
            circle.applyPositionToChildren();
        }
        return r;
    }
}
