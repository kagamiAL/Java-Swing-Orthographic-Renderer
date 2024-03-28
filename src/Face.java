import java.awt.*;
import java.util.ArrayList;

public class Face {

    private final int indexA;

    private final int indexB;

    private final int indexC;

    private Vector2 colorACoordinate;

    private Vector2 colorBCoordinate;

    private Vector2 colorCCoordinate;

    private final Vector3 faceNormal;

    private Vector3[] projectedVertices = null;

    private static Vector3 getFaceNormal(ArrayList<Vector3> vertices, int[] face) {
        Vector3 v = vertices.get(face[1]).sub(vertices.get(face[0]));
        Vector3 u = vertices.get(face[2]).sub(vertices.get(face[0]));
        return v.cross(u).unit();
    }

    public Face(int[] face, ArrayList<Vector3> vertices) {
        indexA = face[0];
        indexB = face[1];
        indexC = face[2];
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

    public Vector2 getColorACoordinate() {
        return colorACoordinate;
    }

    public Vector2 getColorBCoordinate() {
        return colorBCoordinate;
    }

    public Vector2 getColorCCoordinate() {
        return colorCCoordinate;
    }

    public Vector3 getFaceNormal() {
        return faceNormal;
    }

    public void setProjectedVertices(Vector3[] projectedVertices) {
        this.projectedVertices = projectedVertices;
    }

    public void setTexturePoints(ArrayList<Vector2> texturePoints2D, ArrayList<Integer> colorIndices) {
        colorACoordinate = texturePoints2D.get(colorIndices.getFirst());
        colorBCoordinate = texturePoints2D.get(colorIndices.get(1));
        colorCCoordinate = texturePoints2D.get(colorIndices.get(2));
    }
}
