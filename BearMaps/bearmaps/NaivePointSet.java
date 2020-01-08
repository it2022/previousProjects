package bearmaps;

import edu.princeton.cs.algs4.Stopwatch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class NaivePointSet implements PointSet {
    ArrayList<Point> naiveTree;

    public NaivePointSet(List<Point> points) {
        naiveTree = new ArrayList();
        for (Point i : points) {
            naiveTree.add(i);
        }
    }

    public static void main(String[] args) {
//        Point p1 = new Point(0, 7);
//        Point p2 = new Point(1, 2);
//        Point p3 = new Point(3, 4);
//        Point p4 = new Point(4, 5);
//        Point p5 = new Point(5, 6);
//        Point p6 = new Point(6, 4);
//        Point p7 = new Point(3, 2);
//        Point p8 = new Point(1, 3);
//
//        List<Point> hi = Arrays.asList(p1,p2,p3,p4,p5,p6,p7,p8);
//        NaivePointSet hi2 = new NaivePointSet(hi);
//        System.out.println(hi2.nearest(0,3));
//        System.out.println(hi2.nearest(7,6));
//        System.out.println(hi2.nearest(3,2));
//        System.out.println(hi2.nearest(2,1));
        Random r = new Random();
//        r.setSeed(1982);
        int R = 500;
        int L = 100000;

        int start = -500;
        int end = 500;
        HashSet<Point> hi3 = new HashSet<>();
        List<Point> hi5 = new ArrayList<>();
        Stopwatch sw = new Stopwatch();
        for (int i = 0; i < L; i += 1) {
            double ran = r.nextDouble();
            double x = start + (ran * (end - start));
            ran = r.nextDouble();
            double y = start + (ran * (end - start));
            Point temp = new Point(x, y);
            hi5.add(temp);

        }
        KDTree speedTest = new KDTree(hi5);
        NaivePointSet speedTest2 = new NaivePointSet(hi5);

//        for (int i = 0; i < 1000; i++) {
//            double ran = r.nextDouble();
//            double x2 = start + (ran *(end - start));
//            ran = r.nextDouble();
//            double y2 = start + (ran *(end - start));
//            assertEquals(speedTest2.nearest(x2, y2), speedTest.nearest(x2, y2 ));
//        }
//        assertEquals(speedTest2.nearest(r.nextInt(R + 1 - 500) + 500,
//        r.nextInt(R + 1 - 500) + 100), speedTest.nearest(427.535670, -735.656403));

        System.out.println("elapsed time1: " + sw.elapsedTime());

        int R2 = 100;
        int L2 = 10000;
        Stopwatch sw2 = new Stopwatch();
        for (int i = 0; i < L2; i += 1) {

            speedTest2.nearest(r.nextDouble(), r.nextDouble());
        }

        System.out.println("elapsed time: " + sw2.elapsedTime());
    }

    public Point nearest(double x, double y) {
        Point closest = null;
        double smallest = Double.MAX_VALUE;
        Point curr = new Point(x, y);
        for (Point i : naiveTree) {
            if (Point.distance(curr, i) < smallest) {
                smallest = Point.distance(curr, i);
                closest = i;
            }
        }
        return closest;
    }
}
