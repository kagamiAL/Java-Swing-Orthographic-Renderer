public class Item3D {

    private final Vector3[] vertices;

    private final Face[] faces;

    private final Vector3[] projectedVertices;

    public Item3D(Vector3[] vertices, int[][] faces){
        this.vertices = vertices;
        this.projectedVertices = new Vector3[vertices.length];
        this.faces = new Face[faces.length];
        for (int i = 0; i < faces.length; i++) {
            this.faces[i] = new Face(faces[i], vertices, this.projectedVertices);
        }
    }

    public void setScale(int scale) {
        for (int x = 0; x < vertices.length; x++){
            vertices[x] = vertices[x].multiply(scale);
        }
    }

    public int[][] getFaces() {

    public Face[] getFaces() {
        return faces;
    }

    public Vector3[] getVertices() {
        return vertices;
    }

    public Vector3[] getProjectedVertices() {
        return projectedVertices;
    }
}
