import java.awt.*;
import java.util.ArrayList;

public class Item3D {

    private final Vector3[] vertices;

    private final Face[] faces;

    private final Vector3[] projectedVertices;

    private Texture texture = null;

    public Item3D(Vector3[] vertices, Face[] faces){
        this.vertices = vertices;
        this.projectedVertices = new Vector3[vertices.length];
        this.faces = faces;
        for (Face face: faces){
            face.setProjectedVertices(this.projectedVertices);
        }
    }

    public void setScale(int scale) {
        for (int x = 0; x < vertices.length; x++){
            vertices[x] = vertices[x].multiply(scale);
        }
    }

    public Face[] getFaces() {
        return faces;
    }

    public Vector3[] getVertices() {
        return vertices;
    }

    public Vector3[] getProjectedVertices() {
        return projectedVertices;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }
}
