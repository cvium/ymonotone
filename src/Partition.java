import ProGAL.dataStructures.Set;
import ProGAL.geom2d.*;
import ProGAL.geom2d.Point;
import ProGAL.geom2d.Polygon;
import ProGAL.geom2d.viewer.J2DScene;

import java.awt.*;
import java.util.*;
import java.util.List;


/**
 * Created by Claus on 13-02-2015.
 *
 * Partition class. Partitions a simple polygon into y-monotone polygons.
 * Only returns the added diagonals, however.
 */
public class Partition {

    J2DScene scene;
    ArrayList<LineSegment> firstSweepDiags;
    ArrayList<LineSegment> secondSweepDiags;
    Set<LineSegment> diags;
    Point currentEvent;
    Boolean visualize;
    Line sweepline;

    /***
     * Constructor. Takes a scene and a boolean.
     * @param scene J2DScene to place objects.
     * @param visualize Should it visualize the process?
     */
    public Partition(J2DScene scene, Boolean visualize) {
        this.scene = scene;
        this.visualize = visualize;
    }

    /***
     * Empty constructor.
     */
    public Partition() {
        // Do nothing
    }

    /***
     * Partition helper function, which does all the heavy lifting.
     * @param sortedIndices List of indices sorted in descending y-order.
     * @param vertices List of vertices in CCW order.
     * @param edges TreeMap for active edges with special comparator.
     * @param diags List of added diagonals.
     * @param f1 Comparison function. 1st sweep uses '<' and 2nd sweep uses '>'
     * @param f2 Comparison function. 1st sweep uses '>' and 2nd sweep uses '<'
     * @param invert Whether the points have been inverted or not.
     * @throws Exception
     */
    private void partitionhelper(List<Integer> sortedIndices,
                                        List<Vertex> vertices,
                                        TreeMap<Edge, NodeData> edges,
                                        ArrayList<LineSegment> diags,
                                        Inequality f1,
                                        Inequality f2,
                                        Boolean invert) throws Exception {
        if (visualize && scene == null) {
            throw new Exception("No J2DScene has been specified.");
        } else if (visualize) {
            sweepline = new Line(0, vertices.get(sortedIndices.get(0)).getP().y());
            scene.addShape(sweepline, Color.RED);
            currentEvent = vertices.get(sortedIndices.get(0)).getP().clone();
            currentEvent.toScene(scene, 0.06, Color.BLACK);
        }
        LineSegment d;
        for (Integer i : sortedIndices) {
            Vertex v = vertices.get(i);
            if (visualize) {
                sweepline.translateTo(new Point(0, vertices.get(i).getP().y()));
                currentEvent.set(v.getP());
                scene.repaint();
            }
            Edge e = null;
            Edge left = null, right = null, leftEdge = null, rightEdge = null;
            Boolean prevEdgeAdded = false, nextEdgeAdded = false;
            // TODO clean up cases so they make more sense
            if (f1.ineq(v.getPrevPoint().getP().y(), v.getP().y())) {
                prevEdgeAdded = true;
                e = new Edge(v, v.getPrevPoint());
                leftEdge = e;
                edges.put(e, new NodeData(v, v));
                if (visualize)
                    new LineSegment(v.getP(), v.getPrevPoint().getP())
                            .toScene(scene, Color.GREEN, 0.03);
            }
            if (f1.ineq(v.getNextPoint().getP().y(), v.getP().y())) {
                nextEdgeAdded = true;
                e = new Edge(v, v.getNextPoint());
                rightEdge = e;
                edges.put(e, new NodeData(v, v));
                if (visualize)
                    new LineSegment(v.getP(), v.getNextPoint().getP())
                            .toScene(scene, Color.GREEN, 0.03);


            }
            // if both edges were added to the tree, and they do a right turn
            // then the closest point cannot be inbetween the two edges
            // if it's a left turn, then the point may only be to the left
            // of one of the edges
            if (prevEdgeAdded && nextEdgeAdded) {
                // the following should be placed somewhere else.
                // basically left and right edges should only be "created"
                // when both edges have been added this iteration
                if (f2.ineq(v.getNextPoint().getP().x(), v.getPrevPoint().getP().x())) {
                    left = new Edge(v, v.getPrevPoint());
                    right = e;
                } else if (v.getNextPoint().getP().x() == v.getPrevPoint().getP().x() &&
                        f2.ineq(v.getNextPoint().getP().y(), v.getPrevPoint().getP().y())) {
                    left = new Edge(v, v.getPrevPoint());
                    right = e;
                } else {
                    right = new Edge(v, v.getPrevPoint());
                    left = e;
                }
                leftEdge.setClosestToLeft(true);
                rightEdge.setClosestToLeft(false);
            }

            if (f1.ineq(v.getP().y(), v.getPrevPoint().getP().y())) {
                Edge e_tmp = new Edge(v.getPrevPoint(), v);
                if (nextEdgeAdded){
                    rightEdge.setClosestToLeft(false);
                }
                edges.remove(e_tmp);
                if (visualize)
                    new LineSegment(v.getPrevPoint().getP(), v.getP())
                            .toScene(scene, Color.RED, 0.04);
                // if no edges were added, use this edge to update the
                // other edges etc.
                if (e == null) {
                    e = e_tmp;
                }
            }
            if (f1.ineq(v.getP().y(), v.getNextPoint().getP().y())) {
                Edge e_tmp = new Edge(v.getNextPoint(), v);
                if (prevEdgeAdded){
                    leftEdge.setClosestToLeft(true);
                }
                edges.remove(e_tmp);
                if (visualize)
                    new LineSegment(v.getNextPoint().getP(), v.getP())
                            .toScene(scene, Color.RED, 0.04);
                // if no edges were added, use this edge to update the
                // other edges etc.
                if (e == null) {
                    e = e_tmp;
                }
            }

            // Get the predecessor and successor of current vertex
            Point prevP = v.getPrevPoint().getP();
            Point nextP = v.getNextPoint().getP();

            // See if diagonals should be added ie. add diagonal if no edges
            // coming from above to the current vertex. Vertex must also
            // point to the interior of the polygon.
            if (Point.rightTurn(prevP, v.getP(), nextP) &&
                    f2.ineq(v.getP().y(), prevP.y()) &&
                    f2.ineq(v.getP().y(), nextP.y())) {
                // find closest point to draw diag to
                NodeData closestVertex = edges.lowerEntry(left).getValue();
                Point t = closestVertex.getClosest().getP();
                d = new LineSegment(v.getP(), t);
                diags.add(d);
                if (visualize) {
                    v.getP().toScene(scene, 0.05, Color.RED);
                    new LineSegment(edges.lowerEntry(left).getKey().getV1().getP(),
                            edges.lowerEntry(left).getKey().getV2().getP()).toScene(scene, Color.BLUE, 0.04);
                    d.toScene(scene, Color.RED);
                    scene.repaint();
                }
                // USED FOR DEBUGGING. BOTH EDGES SHOULD GIVE THE SAME DIAGONAL
                closestVertex = edges.higherEntry(right).getValue();
                t = closestVertex.getClosest().getP();
                d = new LineSegment(v.getP(), t);
                if (visualize) {
                    new LineSegment(edges.higherEntry(right).getKey().getV1().getP(),
                            edges.higherEntry(right).getKey().getV2().getP()).toScene(scene, Color.BLUE, 0.04);
                    d.toScene(scene, Color.BLUE);
                    scene.repaint();
                }
            }

            // if left and right edges were added, use them for lower and higher
            // keys respectively. Otherwise the higher key for the left edge
            // is the right edge, which is pointless.
            if (left != null) {
                e = left;
            }
            Map.Entry<Edge, NodeData> lower = edges.lowerEntry(e);
            if (right != null) {
                e = right;
            }
            Map.Entry<Edge, NodeData> higher = edges.higherEntry(e);
            if (lower != null) {
                Line l1 = new Line(new LineSegment(lower.getKey().getV1().getP(), lower.getKey().getV2().getP()));
                Line l2 = new Line(0, v.getP().y());
                //find the intersection between horizontal line and line segment
                //to determine whether the new vertex is on the left or right
                //side of the edge we found in the tree
                Point inter2 = Line.getIntersection(l1, l2);
                Boolean toLeft;
                if (invert)
                    toLeft = !lower.getKey().isClosestToLeft();
                else
                    toLeft = lower.getKey().isClosestToLeft();

                // check if the candidate point needs updating
                if (lower.getKey().isClosestToLeft() != null && toLeft && inter2.x() >= v.getP().x()) {
                        lower.getValue().setClosest(v);
                } else if (lower.getKey().isClosestToLeft() != null && !toLeft && inter2.x() <= v.getP().x()) {
                        lower.getValue().setClosest(v);
                }
            }
            if (higher != null) {
                Line l1 = new Line(new LineSegment(higher.getKey().getV1().getP(), higher.getKey().getV2().getP()));
                Line l2 = new Line(0, v.getP().y());

                //find the intersection between horizontal line and line segment
                //to determine whether the new vertex is on the left or right
                //side of the edge we found in the tree
                Point inter2 = Line.getIntersection(l1, l2);
                Boolean toLeft;
                if (invert)
                    toLeft = !higher.getKey().isClosestToLeft();
                else
                    toLeft = higher.getKey().isClosestToLeft();

                // check if the candidate point needs updating
                if (higher.getKey().isClosestToLeft() != null && toLeft && inter2.x() >= v.getP().x()) {
                    higher.getValue().setClosest(v);
                } else if (higher.getKey().isClosestToLeft() != null && !toLeft && inter2.x() <= v.getP().x()) {
                    higher.getValue().setClosest(v);
                }
            }
            if (visualize) {
                scene.repaint();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        if (visualize)
            scene.removeShape(sweepline); // remove the sweep line after sweep is done
    }

    /* Function to compare two points.
    In regular order, Point p1 is smaller than p2 if its x-value is smaller
    and if x-values are equivalent, then p1 is smaller if its y-value is smaller
    */
    private int cmpPoints(Point p1, Point p2, Boolean invert) {
        int c;
        if (invert) {
            c = Double.compare(p2.x(), p1.x());
            if (c == 0)
                c = -Double.compare(p2.y(), p1.y());
        } else {
            c = Double.compare(p1.x(), p2.x());
            if (c == 0)
                c = -Double.compare(p1.y(), p2.y());
        }
        return c;
    }

    private TreeMap<Edge, NodeData> makeTree(final Boolean invert) {
        return new TreeMap<Edge, NodeData>(new Comparator<Edge>() {
            /*
            Comparator for Edge class. It takes out the starting point of the
            edge being compared to the ones in the tree.
            It then creates two lines. One horizontal line going through this
            point and another line going through both points of the 2nd edge.
            the x-value of the starting point is then compared with the x-value
            of the intersection between the two lines.
             */
            @Override
            public int compare(Edge o1, Edge o2) {
                int c = 1;
                double y, x;
                Line l1 = new Line(new LineSegment(o2.getV1().getP(), o2.getV2().getP()));

                Vertex v1 = o1.getV1();
                Vertex v2 = o1.getV2();

                y = v1.getP().y();
                x = v1.getP().x();

                Line l2 = new Line(0, y);

                Point inter = Line.getIntersection(l1, l2);

                if (cmpPoints(v1.getP(), o2.getV1().getP(), invert) == 0) {
                    c = cmpPoints(v2.getP(), o2.getV2().getP(), invert);
                } else if ((!invert && inter.x() < x) || (invert && inter.x() > x)) {
                    c = 1;
                } else if ((!invert && inter.x() > x) || (invert && inter.x() < x)) {
                    c = -1;
                } else if (cmpPoints(v1.getP(), o2.getV1().getP(), invert) == -1) {
                    c = -1;
                } else if (cmpPoints(v1.getP(), o2.getV1().getP(), invert) == 1) {
                    c = 1;
                }
                return c;
            }
        });
    }

    public Set<LineSegment> partition(Polygon poly) throws Exception {
        TreeMap<Edge, NodeData> edges = makeTree(false);
        List<Vertex> vertices = new ArrayList<Vertex>();
        this.diags = new Set<LineSegment>();
        this.firstSweepDiags = new ArrayList<LineSegment>();
        this.secondSweepDiags = new ArrayList<LineSegment>();

        // TODO implement proper sorting
        // Sort descending y-order and create Vertex objects.
        TreeMap<Double, Integer> temp = new TreeMap<Double, Integer>(Collections.reverseOrder());
        for (int i = 0; i < poly.size(); i++) {
            Vertex prev = (i == 0) ? null : vertices.get(i - 1);
            Vertex next = (i == poly.size() - 1) ? vertices.get(0) : null;
            Vertex v = new Vertex(poly.getCorner(i), prev, next);
            if (prev != null) {
                prev.setNextPoint(v);
            }
            if (next != null) {
                next.setPrevPoint(v);
            }
            vertices.add(v);
            temp.put(poly.getCorner(i).y(), i);
        }
        List<Integer> sortedIndices = new ArrayList<Integer>(temp.values());

        // sweep
        partitionhelper(sortedIndices, vertices, edges, firstSweepDiags,
                new Inequality() {
                    @Override
                    public Boolean ineq(double x, double y) {
                        return x < y;
                    }
                },
                new Inequality() {
                @Override
                public Boolean ineq(double x, double y) {
                    return x > y;
                }
        }, false);
        // reverse the list and sweep again
        Collections.reverse(sortedIndices);
        edges = makeTree(true);
        partitionhelper(sortedIndices, vertices, edges, secondSweepDiags,
                new Inequality() {
                    @Override
                    public Boolean ineq(double x, double y) {
                        return x > y;
                    }
                },
                new Inequality() {
                    @Override
                    public Boolean ineq(double x, double y) {
                        return x < y;
                    }
                }, true);

        // merge two diag lists to remove duplicates in no particular order
        int first = firstSweepDiags.size();
        int second = secondSweepDiags.size();
        int j = 0, k = second - 1;
        while (true) {
            if (k < 0 && j >= first) {
                break;
            }
            else if (j >= first) {
                diags.insert(secondSweepDiags.get(k));
                k--;
            } else if (k < 0) {
                diags.insert(firstSweepDiags.get(j));
                j++;
            } else {
                LineSegment a = firstSweepDiags.get(j);
                LineSegment b = secondSweepDiags.get(k);
                if (a.getA().equals(b.getA()) && a.getB().equals(b.getB())) {
                    diags.insert(a);
                    j++;
                    k--;
                } else if (a.getA().equals(b.getB()) && a.getB().equals(b.getA())) {
                    diags.insert(a);
                    j++;
                    k--;
                } else if (a.getB().y() >= b.getA().y()) {
                    diags.insert(a);
                    j++;
                } else if (a.getB().y() <= b.getA().y()) {
                    diags.insert(b);
                    k--;
                }
            }
        }
        return diags;
    }

    // java does not have function pointers. this interface was created to
    // keep the code DRY
    private interface Inequality {
        public Boolean ineq(double x, double y);
    }
}
