package bearmaps;
//
//import org.junit.Test;
//
//import static org.junit.Assert.assertEquals;

import java.util.Random;
import java.util.HashSet;

import edu.princeton.cs.algs4.Stopwatch;

public class ArrayHeapMinPQTest {
    public static void main(String[] args) {
        ArrayHeapMinPQ<String> test = new ArrayHeapMinPQ();
        test.add("b", 8);
        test.add("a", 10);
        test.add("c", 15);
        test.add("d", 3);
        test.add("e", 7);
        test.add("f", 18);
        System.out.println(test.toString());
        System.out.println(test.removeSmallest());
        System.out.println(test.toString());
        test.changePriority("b", 6);
        System.out.println(test.toString());


        System.out.println("==========");
        test.add("g", 25);
        test.add("h", 9);
        test.add("i", 2);
        System.out.println(test.toString());
        System.out.println(test.removeSmallest());
        System.out.println(test.toString());

        System.out.println("==========");
        System.out.println(test.contains("d"));
        System.out.println(test.getSmallest());

        Random r = new Random();
        HashSet<Integer> numbers = new HashSet();
        int L = 100000000;
        for (int i = 0; i < L; i += 1) {
            numbers.add(r.nextInt(L));
        }
        Stopwatch sw = new Stopwatch();
        ArrayHeapMinPQ<Integer> speedTest = new ArrayHeapMinPQ();

        for (int i : numbers) {
            speedTest.add(i, r.nextDouble() * r.nextInt(L));
        }
        System.out.println("elapsed time: " + sw.elapsedTime());

        Stopwatch sw2 = new Stopwatch();
        for (int i : numbers) {
            speedTest.changePriority(i, r.nextDouble() * r.nextInt(L));
        }
        System.out.println("elapsed time: " + sw2.elapsedTime());
    }
}
