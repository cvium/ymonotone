import ProGAL.dataStructures.*;
import ProGAL.dataStructures.Set;
import ProGAL.geom2d.*;
import ProGAL.geom2d.Point;
import ProGAL.geom2d.Polygon;
import ProGAL.geom2d.viewer.J2DScene;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by Claus on 11-02-2015.
 */
public class main {

    public static void main(String[] args) throws Exception {
        J2DScene scene = J2DScene.createJ2DSceneInFrame();

        Polygon poly = new Polygon(getCCW(example2()));
        poly.draw(scene);
        scene.centerCamera();
        Partition part = new Partition(scene, true);

        Set<LineSegment> diags = part.partition(poly);
        for (LineSegment d : diags) {
            d.toScene(scene, Color.RED);
            scene.repaint();
        }
        System.out.println(diags.getSize());
    }

    private static List<Point> example1() {
        List<Point> ps = new ArrayList<Point>();
        ps.add(new Point(0.0, 0.0));
        ps.add(new Point(1.0, 0.2));
        ps.add(new Point(1.3, 1.0));
        ps.add(new Point(0.8, 0.5));
        ps.add(new Point(0.6, 0.6));
        ps.add(new Point(0.3, 0.4));
        ps.add(new Point(-0.5, 0.51));

        return ps;
    }

    private static List<Point> example2() {
        List<Point> ps = new ArrayList<Point>();
        // Pawel eksempel 1
        ps.add(new Point(0.0, 0.0));
        ps.add(new Point(-0.1, -2.0));
        ps.add(new Point(2.0, -1.0));
        ps.add(new Point(0.5, -3.00));
        ps.add(new Point(1.55, -3.8));
        ps.add(new Point(1.35, -3.3));
        ps.add(new Point(2.5, -3.4));
        ps.add(new Point(2.3, -4.0));
        ps.add(new Point(3.11, -4.1));
        ps.add(new Point(3.66, -3.5));
        ps.add(new Point(2.88, -3.55));
        ps.add(new Point(3.15, -2.44));
        ps.add(new Point(2.25, -2.08));
        ps.add(new Point(1.86, -3.05));
        ps.add(new Point(1.55, -2.77));
        ps.add(new Point(3.1, -0.5));

        return ps;
    }

    private static List<Point> example3() {
        List<Point> ps = new ArrayList<Point>();
         // Pawel eksempel 2
        ps.add(new Point(0.0, 0.0));
        ps.add(new Point(1.0, 1.0));
        ps.add(new Point(3.0, -1.0));
        ps.add(new Point(2.5, 3.0));
        ps.add(new Point(2.0, 2.0));
        ps.add(new Point(1.5, 3.5));

        return ps;
    }

    private static List<Point> example4() {
        List<Point> ps = new ArrayList<Point>();
        ps.add(new Point(0.0, 0.0));
        ps.add(new Point(-0.5, 0.1));
        ps.add(new Point(-0.7, 0.3));
        ps.add(new Point(-1.5, -0.1));
        ps.add(new Point(-1.3, 1.3));
        ps.add(new Point(-1.1, 0.9));
        ps.add(new Point(-0.9, 1.7));
        ps.add(new Point(-0.6, 1.1));

        return ps;
    }

    private static List<Point> example5() {
        List<Point> ps = new ArrayList<Point>();
        ps.add(new Point(0, 0));
        ps.add(new Point(0.5, -0.5));
        ps.add(new Point(0.1, -1.5));
        ps.add(new Point(0.6, -1));
        ps.add(new Point(0.7, -0.8));
        ps.add(new Point(0.9, -0.9));
        ps.add(new Point(1.4, -0.4));
        ps.add(new Point(2, -1.6));
        ps.add(new Point(2.3, 0.5));

        return ps;
    }

    public static List<Point> getCCW(List<Point> pointList) {
        List<Point> points = new ArrayList<Point>();
        for (Point p : pointList) {
            points.add(p.clone());
        }
        //shoelace formula - Courtesy of http://stackoverflow.com/questions/14505565/detect-if-a-set-of-points-in-an-array-that-are-the-vertices-of-a-complex-polygon?lq=1
        double area = 0;

        for (int i = 0; i < points.size(); i++) {
            int j = (i + 1) % points.size();
            area += points.get(i).x() * points.get(j).y();
            area -= points.get(j).x() * points.get(i).y();
        }

        if (area * 0.5 < 0) {
            Collections.reverse(points);
        }
        return points;
    }
}
