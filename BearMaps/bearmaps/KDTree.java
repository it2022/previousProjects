package bearmaps;

import java.util.Collections;
import java.util.List;

public class KDTree implements PointSet {
    private Node tree;

    public KDTree(List<Point> points) {
        Collections.shuffle(points);
        tree = new Node(points, 1);
    }

    public Point nearest(double x, double y) {
        return nearestHelper(x, y, tree, tree, 1).root;
    }

    public Node nearestHelper(double x, double y, Node curr, Node best, int depth) {
        Point goal = new Point(x, y);
        Node goodSide = null;
        Node badSide = null;
        if (curr == null) {
            return best;
        }
        if (Math.sqrt(Point.distance(curr.root, goal))
                <= Math.sqrt(Point.distance(goal, best.root))) {
            best = curr;
        }
        if (depth > 0) {
            if (goal.getX() < curr.root.getX()) {
                goodSide = curr.leftChild;
                badSide = curr.rightChild;
            } else {
                goodSide = curr.rightChild;
                badSide = curr.leftChild;
            }
        } else if (depth < 0) {
            if (goal.getY() < curr.root.getY()) {
                goodSide = curr.leftChild;
                badSide = curr.rightChild;
            } else {
                goodSide = curr.rightChild;
                badSide = curr.leftChild;
            }
        }

        best = nearestHelper(x, y, goodSide, best, depth * -1);
        double bestDistance = Point.distance(goal, best.root);


        if (depth == 1) {
            if ((goal.getX() - curr.root.getX()) * (goal.getX() - curr.root.getX())
                    < bestDistance) {
                best = nearestHelper(x, y, badSide, best, depth * -1);
            }
        } else if (depth == -1) {
            if ((goal.getY() - curr.root.getY()) * (goal.getY() - curr.root.getY())
                    < bestDistance) {
                best = nearestHelper(x, y, badSide, best, depth * -1);
            }
        }

        return best;
    }

    public class Node {
        Node leftChild;
        Node rightChild;
        Point root;

        public Node() {

        }

        public Node(List<Point> points, int depth) {
            root = points.get(0);


            for (int i = 1; i < points.size(); i++) {
                Node guy = new Node();
                guy.root = points.get(i);
                add(this, guy, depth);
            }

        }

        public void add(Node rt, Node newGuy, float depth) {
            if (rt == null) {
                return;
            }
            if (depth > 0) {
                if (rt.root.getX() < newGuy.root.getX()) {
                    //go right
                    if (rt.rightChild == null) {
                        rt.rightChild = newGuy;
                    } else {
                        add(rt.rightChild, newGuy, depth * -1);
                    }
                } else {
                    //go left
                    if (rt.leftChild == null) {
                        rt.leftChild = newGuy;
                    } else {
                        add(rt.leftChild, newGuy, depth * -1);
                    }
                }
            } else if (depth < 0) {
                if (rt.root.getY() < newGuy.root.getY()) {
                    //go right
                    if (rt.rightChild == null) {
                        rt.rightChild = newGuy;
                    } else {
                        add(rt.rightChild, newGuy, depth * -1);
                    }
                } else {
                    //go left
                    if (rt.leftChild == null) {
                        rt.leftChild = newGuy;
                    } else {
                        add(rt.leftChild, newGuy, depth * -1);
                    }
                }
            }

        }
    }

}
