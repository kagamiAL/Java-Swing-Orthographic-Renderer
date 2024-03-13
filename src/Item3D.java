public class Item3D {

    private int scale = 1;

    private final Vector3[] vertices;

    private final int[][] faces;

    public Item3D(Vector3[] vertices, int[][] faces){
        this.vertices = vertices;
        this.faces = faces;
    }

    public void setScale(int scale) {
        this.scale = scale;
        for (int x = 0; x < vertices.length; x++){
            vertices[x] = vertices[x].multiply(scale);
        }
    }

    public int[][] getFaces() {
        return faces;
    }

    public Vector3[] getVertices() {
        return vertices;
    }
}
