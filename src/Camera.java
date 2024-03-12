import java.util.Arrays;

public class Camera {

    public static final Vector3 UP_VECTOR = new Vector3(0, 1, 0);

    public static final Vector3 RIGHT_VECTOR = new Vector3(1, 0, 0);

    private Vector3 lookVector = new Vector3(0, 0, 1);

    private int height = 512;

    private int width = 512;

    public Camera(){}

    public Camera(int height, int width){
        this.height = height;
        this.width = width;
    }

    private Vector3[] getProjectedVertices(Vector3[] vertices){
        int screenOriginX = width/2 - 1;
        int screenOriginY = height/2 - 1;
        Vector3 localRight = lookVector.cross(UP_VECTOR);
        Vector3 localUp = localRight.cross(lookVector);
        Vector3[] projectedVertices = new Vector3[vertices.length];

        for (int x = 0; x < vertices.length; x++) {
            Vector3 planeVertex = vertices[x].sub(vertices[x].project(lookVector));

        }


        return projectedVertices;
    }

    public void setLookVector(Vector3 lookVector) {
        this.lookVector = lookVector;
    }

    public void render(Item3D item3D){
        Vector3[] projectedVertices = getProjectedVertices(item3D.getVertices());
        for (int[] face: item3D.getFaces()){

        }
    }
}
