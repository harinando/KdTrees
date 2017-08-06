import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by nando on 10/7/16.
 */
public class KdTreeTest {

    private KdTree kdtree;
    private PointSET brute;
    @Before
    public void setUp() {
        int n = 100000;
        brute = new PointSET();
        kdtree = new KdTree();

        for (int i = 0; i < n; i++) {
            double x = StdRandom.uniform(0.0, 1.0);
            double y = StdRandom.uniform(0.0, 1.0);
            Point2D point = new Point2D(x, y);
            brute.insert(point);
            kdtree.insert(point);
        }
    }

    @Test
    public void testIsEmpty() throws Exception {
        kdtree = new KdTree();
        brute = new PointSET();
        assertEquals(brute.isEmpty(), kdtree.isEmpty()) ;
    }

    @Test
    public void testIsNotEmpty() throws Exception {
        assertEquals(brute.isEmpty(), kdtree.isEmpty());
    }


    @Test
    public void testSizeOnNonEmptyTree() throws Exception {
        assertEquals(brute.size(), kdtree.size());
    }

    @Test
    public void testInsert() throws Exception {
    }

    @Test
    public void testContains() throws Exception {
        kdtree = new KdTree();
        kdtree.insert(new Point2D(0.7, 0.2));
        kdtree.insert(new Point2D(0.5, 0.4));
        kdtree.insert(new Point2D(0.2, 0.3));
        kdtree.insert(new Point2D(0.4, 0.7));
        kdtree.insert(new Point2D(0.9, 0.6));
        assertEquals(true, kdtree.contains(new Point2D(0.7, 0.2)));
    }

    @Test
    public void testDoesNotContains() throws Exception {
        assertEquals(false, kdtree.contains(new Point2D(10, 20)));
    }

    @Test
    public void testRange() throws Exception {
        RectHV rect = new RectHV(0, 0, 0.25, 0.25);
        StdOut.println(brute.range(rect));
        StdOut.println(kdtree.range(rect));
    }

    @Test
    public void testNearest() throws Exception {
        Point2D p = new Point2D(0, 0);
        assertEquals(brute.nearest(p), kdtree.nearest(p));
    }
}