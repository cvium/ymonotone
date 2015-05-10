import ProGAL.geom2d.Point;

/**
 * Created by Claus on 12-02-2015.
 *
 * Vertex class. Keeps pointers to previous and next points.
 */
public class Vertex {
    private Vertex prevPoint;
    private Vertex nextPoint;
    private Point p;

    public Vertex(Point p) {
        this.p = p;
    }

    public Vertex(Point p, Vertex prev, Vertex next) {
        this.p = p;
        this.prevPoint = prev;
        this.nextPoint = next;
    }

    public Point getP() {
        return p;
    }

    public void setP(Point p) {
        this.p = p;
    }

    public Vertex getPrevPoint() {
        return prevPoint;
    }

    public void setPrevPoint(Vertex prevPoint) {
        this.prevPoint = prevPoint;
    }

    public Vertex getNextPoint() {
        return nextPoint;
    }

    public void setNextPoint(Vertex nextPoint) {
        this.nextPoint = nextPoint;
    }
}
