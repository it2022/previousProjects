package bearmaps;


import edu.princeton.cs.algs4.Stopwatch;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

//import org.junit.Test;
//import org.junit.inte;

//@Source PseudoWalkthrough
public class KDTreeTest {
    @Test
    public void kdtreeTester() {
        Point p1 = new Point(0, 1);
        Point p2 = new Point(3, 6);
        Point p4 = new Point(277.5435652488, -450.5567989151);
        Point p3 = new Point(187.2792022774, -695.8081816334);
        List<Point> hi = Arrays.asList(p1, p2, p3, p4);
        KDTree hi2 = new KDTree(hi);
        System.out.println(hi2.nearest(0, 2));
        System.out.println(hi2.nearest(3, 3));
        for (int i = 0; i < 100; i++) {
            Point p6 = new Point(0, 1);
            Point p7 = new Point(3, 6);
            Point p8 = new Point(277.5435652488, -450.5567989151);
            Point p9 = new Point(187.2792022774, -695.8081816334);
            Point p99 = new Point(427.535670, -735.656403);
            List<Point> hi3 = Arrays.asList(p1, p2, p3, p4);
            KDTree hi4 = new KDTree(hi3);
            System.out.println(hi4.nearest(427.535670, -735.656403));
        }
        Random r = new Random();
//        r.setSeed(1982);
        int R = 500;
        int L = 100000;

        int start = -500;
        int end = 500;
//        HashSet<Point> hi3 = new HashSet<>();
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
//        r.nextInt(R + 1 - 500) + 100),
//        speedTest.nearest(427.535670, -735.656403));

        System.out.println("elapsed time1: " + sw.elapsedTime());

        int R2 = 100;
        int L2 = 10000;
        Stopwatch sw2 = new Stopwatch();
        for (int i = 0; i < L2; i += 1) {

            speedTest.nearest(r.nextDouble(), r.nextDouble());
        }

        System.out.println("elapsed time: " + sw2.elapsedTime());


    }


}
