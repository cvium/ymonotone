
/**
 * Created by Claus on 12-02-2015.
 *
 * Class for holding the current vertex and the closest candidate point
 */
public class NodeData {
    private Vertex v;
    private Vertex closest;

    public NodeData(Vertex v, Vertex closest) {
        this.v = v;
        this.closest = closest;
    }

    public Vertex getV() {
        return v;
    }

    public void setV(Vertex v) {
        this.v = v;
    }

    public Vertex getClosest() {
        return closest;
    }

    public void setClosest(Vertex closest) {
        this.closest = closest;
    }
}
