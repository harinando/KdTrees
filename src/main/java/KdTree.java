import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;

/**
 * Created by nando on 10/5/16.
 */
public class KdTree {

    private static class Node {
        private Point2D p;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node left;      // the left/bottom subtree
        private Node right;     // the right/top subtree

        private Node(Point2D p, RectHV rect) {
            this.p = p;
            this.rect = rect;
        }

        private Node(Point2D p) {
            this.p = p;
        }
    }

    private boolean orientation; // Orientation = true => Vertical
    private final RectHV UNIT_SQUARE = new RectHV(0, 0, 1, 1);

    private Node root;
    private int size;

    public KdTree() {                   // construct an empty set of points
        size = 0;
    }

    public boolean isEmpty() {         // is the set empty?
        return size == 0;
    }

    public int size() {                // number of points in the set
        return size;
    }

    public void insert(Point2D p) {   // add the point to the set (if it is not already in the set)
        if (p == null) throw new NullPointerException();
        root = insert(root, p, true, UNIT_SQUARE);
    }

    private Node insert(Node node, Point2D p, boolean vertical, RectHV rectHV) {

        if (node == null) {
            this.size++;
            return new Node(p, rectHV);
        }

        int cmp = vertical ? Point2D.X_ORDER.compare(p, node.p) : Point2D.Y_ORDER.compare(p, node.p);

        if (node.p.equals(p)) {
            return node;
        }

        if( cmp < 0 ) {
            RectHV rect = vertical ? new RectHV(node.rect.xmin(), node.rect.ymin(), node.p.x(), node.rect.ymax())
                    : new RectHV(node.rect.xmin(), node.rect.ymin(), node.rect.xmax(), node.p.y());

            node.left = insert(node.left, p, !vertical, rect);

        } else {        // In addition: Go to the right if p.x() == node.p.x()
            RectHV rect = vertical ? new RectHV(node.p.x(), node.rect.ymin(), node.rect.xmax(), node.rect.ymax())
                    : new RectHV(node.rect.xmin(), node.p.y(), node.rect.xmax(), node.rect.ymax());

            node.right = insert(node.right, p, !vertical, rect);

        }
        return node;
    }

    public boolean contains(Point2D p) {      // does the set contain point p?
        if (p == null || root == null) {
            return false;
        }
        return contains(root, p, true);
    }

    private boolean contains(Node node, Point2D p, boolean orientation) {
        if (node == null) {
            return false;
        }

        if (node.p.equals(p)) {
            return true;
        }

        int cmp = orientation ? Point2D.X_ORDER.compare(p, node.p) : Point2D.Y_ORDER.compare(p, node.p);

        if (cmp < 0) {
            return contains(node.left, p, !orientation);
        } else {
            return contains(node.right, p, !orientation);
        }
    }

    public void draw() {                      // draw all points to standard draw
        StdDraw.setScale(0.0, 1.0);
        draw(root, true);
    }

    private void draw(Node node, boolean vertical) {
        if (node == null) {
            return ;
        }

        // draw the splitting line
        if (vertical) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(node.p.x(), node.rect.ymin(), node.p.x(), node.rect.ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(node.rect.xmin(), node.p.y(), node.rect.xmax(), node.p.y());
        }

        draw(node.left, !vertical);
        draw(node.right, !vertical);

        // draw the point
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        node.p.draw();
    }

    public Iterable<Point2D> range(RectHV rect) {     // all points that are inside the rectangle

        if (rect == null) {
            throw new NullPointerException();
        }
        ArrayList<Point2D> rangeSet = new ArrayList<Point2D>();
        range(root, rect, rangeSet);
        return rangeSet;
    }

    private void range(Node node, RectHV rect, ArrayList<Point2D> rangeSet) {

        if (node == null) {
            return;
        }

        if (!node.rect.intersects(rect)) {
            return;
        }

        if(rect.contains(node.p)) {
            rangeSet.add(node.p);
        }
        range(node.left, rect, rangeSet);
        range(node.right, rect, rangeSet);
    }

    public Point2D nearest(Point2D p) {               // a nearest neighbor in the set to point p; null if the set is empty
        if (p == null) {
            throw new NullPointerException();
        }
        return nearest(root, p, Double.POSITIVE_INFINITY, root.p);
    }

    /*
     * https://www.cs.umd.edu/class/spring2008/cmsc420/L19.kd-trees.pdf
     */
    private Point2D nearest(Node node, Point2D query, double bestDistance, Point2D guess) {
        if (node == null) {
            return null;
        }

        /* If the candidate hypersphere crosses this splitting plane, look on the
         * other side of the plane by examining the other subtree.
         */
        if (node.rect.distanceSquaredTo(query) > bestDistance) {
            return null;
        }

        /*
        * If the current location is better than the best known location,
        * update the best known location.
        */
        double distance = query.distanceSquaredTo(node.p);
        if (distance < bestDistance) {
            bestDistance = distance;
            guess = node.p;
        }

        /* Recursively search the half of the tree that contains the test point. */
        Node closestGuess = node.right;
        Node farthestGuess = node.left;
        if (node.left != null && node.right != null) {
            if (node.left.rect.distanceSquaredTo(query) < node.right.rect.distanceSquaredTo(query)) {
                closestGuess = node.left;
                farthestGuess = node.right;
            }

            Point2D firstCandidate = nearest(closestGuess, query, bestDistance, guess);
            if (firstCandidate != null) {
                guess = firstCandidate;
                bestDistance = firstCandidate.distanceSquaredTo(query);
            }

            Point2D secondCandidate = nearest(farthestGuess, query, bestDistance, guess);
            if (secondCandidate != null) {
                guess = secondCandidate;
            }
        } else if (node.left != null) {
            Point2D firstCandidate = nearest(node.left, query, bestDistance, guess);
            if (firstCandidate != null) {
                guess = firstCandidate;
                bestDistance = firstCandidate.distanceSquaredTo(query);
            }
        } else {
            Point2D firstCandidate = nearest(node.right, query, bestDistance, guess);
            if (firstCandidate != null) {
                guess = firstCandidate;
                bestDistance = firstCandidate.distanceSquaredTo(query);
            }
        }
        return guess;
    }

    public static void main(String[] args) {          // unit testing of the methods (optional)
        String filename = args[0];
        In in = new In(filename);
        // initialize the two data structures with point from standard input
        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }
        Point2D p = new Point2D(0.81, 0.30);
        System.out.println(kdtree.nearest(p));
//        System.out.println(kdtree.nearest(p));
//        StdDraw.setPenRadius(0.01);
//        p.draw();
//        kdtree.draw();
//        StdDraw.show();
    }
}