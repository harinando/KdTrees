import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by nando on 10/5/16.
 */
public class PointSET {

    private TreeSet<Point2D> set;

    public PointSET() {                   // construct an empty set of points
        set = new TreeSet<Point2D>();
    }

    public boolean isEmpty() {         // is the set empty?
        return set.isEmpty();
    }

    public int size() {                // number of points in the set
        return set.size();
    }

    public void insert(Point2D p) {   // add the point to the set (if it is not already in the set)
        if (!set.contains(p)) {
            set.add(p);
        }
    }

    public boolean contains(Point2D p) {      // does the set contain point p?
        return set.contains(p);
    }

    public void draw() {                      // draw all points to standard draw
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        for (Point2D point : set) {
            point.draw();
        }
    }

    public Iterable<Point2D> range(RectHV rect) {     // all points that are inside the rectangle
        if (rect == null) throw new NullPointerException();
        ArrayList<Point2D> rangeSet = new ArrayList<Point2D>();
        for (Point2D point : set) {
            if (rect.contains(point)) {
                rangeSet.add(point);
            }
        }
        return rangeSet;
    }

    public Point2D nearest(Point2D p) {               // a nearest neighbor in the set to point p; null if the set is empty
        if (p == null) throw new NullPointerException();
        if (set.isEmpty()) {
            return null;
        }
        Point2D nearestPoint = set.first();
        double minDist = p.distanceSquaredTo(nearestPoint);
        for (Point2D point : set) {
            if (p.distanceSquaredTo(point) < minDist) {
                nearestPoint = point;
                minDist = p.distanceSquaredTo(point);
            }
        }
        return nearestPoint;
    }

    public static void main(String[] args) {
        String filename = args[0];
        In in = new In(filename);
        PointSET brute = new PointSET();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            brute.insert(p);
        }
        RectHV rect = new RectHV(0.921875, 0.318359375, 0.9921875, 0.67578125);
        for (Point2D p : brute.range(rect)) {
            StdOut.println(p.toString());
        }
        Point2D nearest = brute.nearest(new Point2D(0.975528, 0.345492));
        System.out.println(nearest.toString());

    }
}

