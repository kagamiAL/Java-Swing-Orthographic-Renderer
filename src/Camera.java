public class Camera {

    public static final Vector3 UP_VECTOR = new Vector3(0, 1, 0);

    public static final Vector3 RIGHT_VECTOR = new Vector3(1, 0, 0);

    private Vector3 lookVector = new Vector3(0, 0, 1);

    public void setLookVector(Vector3 lookVector) {
        this.lookVector = lookVector;
    }

    public void render(Item3D item3D){

    }
}
