public class Face {

    private final int indexA;

    private final int indexB;

    private final int indexC;

    private final Vector3 faceNormal;

    private final Vector3[] projectedVertices;

    private static Vector3 getFaceNormal(Vector3[] vertices, int[] face) {
        Vector3 v = vertices[face[1]].sub(vertices[face[0]]);
        Vector3 u = vertices[face[2]].sub(vertices[face[0]]);
        return v.cross(u).unit();
    }

    public Face(int[] face, Vector3[] vertices, Vector3[] projectedVertices) {
        indexA = face[0];
        indexB = face[1];
        indexC = face[2];
        this.projectedVertices = projectedVertices;
        faceNormal = getFaceNormal(vertices, face);
    }

    public Vector3 getProjectedA() {
        return projectedVertices[indexA];
    }

    public Vector3 getProjectedB() {
        return projectedVertices[indexB];
    }

    public Vector3 getProjectedC() {
        return projectedVertices[indexC];
    }

    public Vector3 getFaceNormal() {
        return faceNormal;
    }

}
