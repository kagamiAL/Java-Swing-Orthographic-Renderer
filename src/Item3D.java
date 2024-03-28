public class Item3D {

    private int scale = 1;

    private final Vector3[] vertices;

    private final int[][] faces;

    private final Vector3[] faceNormals;

    public Item3D(Vector3[] vertices, int[][] faces){
        this.vertices = vertices;
        this.faces = faces;
        this.faceNormals = new Vector3[faces.length];
        for (int i = 0; i < faces.length; i++) {
            faceNormals[i] = getFaceNormal(vertices, faces[i]);
        }
    }

    private static Vector3 getFaceNormal(Vector3[] vertices, int[] face) {
        Vector3 v = vertices[face[1]].sub(vertices[face[0]]);
        Vector3 u = vertices[face[2]].sub(vertices[face[0]]);
        return v.cross(u).unit();
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

    public int[] getFaceAt(int index) {
        return faces[index];
    }

    public Vector3 getFaceNormalAt(int index) {
        return faceNormals[index];
    }

    public Vector3[] getVertices() {
        return vertices;
    }
}
