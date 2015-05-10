/**
 * Created by Claus on 15-02-2015.
 *
 * Edge class. Keeps information about start and end points as well as whether
 * points can occur to the left of the edge.
 */
public class Edge {
    private Vertex v1;
    private Vertex v2;
    // is the closest point only allowed to be on the left?
    private Boolean closestToLeft = null;

    public Boolean isClosestToLeft() {
        return closestToLeft;
    }

    public void setClosestToLeft(Boolean closestToLeft) {
        this.closestToLeft = closestToLeft;
    }

    public Vertex getV1() {
        return v1;
    }

    public void setV1(Vertex v1) {
        this.v1 = v1;
    }

    public Vertex getV2() {
        return v2;
    }

    public void setV2(Vertex v2) {
        this.v2 = v2;
    }

    public Edge(Vertex v1, Vertex v2) {
        this.v1 = v1;
        this.v2 = v2;
        this.closestToLeft = null;
    }
}
