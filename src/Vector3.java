/**
 * Vector3 implementation in Java
 */
public class Vector3 {

    public float  x;

    public float  y;

    public float  z;

    private float length = -1;

    public Vector3(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Subtracts a vector from another, this - other
     * @param other the vector to subtract with
     * @return the new vector resulting from this operation
     */
    public Vector3 sub(Vector3 other){
        return new Vector3(x - other.x, y - other.y, z - other.z);
    }

    /**
     * Multiplies a vector by a scalar
     * @param scalar the scalar to multiply with
     * @return the new vector from this multiplication
     */
    public Vector3 multiply(float scalar){
        return new Vector3(x * scalar, y * scalar, z * scalar);
    }

    /**
     * Dots two vectors together
     * @param other the vector to dot with
     * @return the dot product
     */
    public float dot(Vector3 other){
        return x * other.x + y * other.y + z * other.z;
    }

    /**
     * Returns the length of the vector
     * @return the length of the vector
     */
    public float length(){
        if (length == -1){
            length = (float) Math.sqrt(x*x + y*y + z*z);
        }
        return length;
    }

    /**
     * Gets the cross product of this X other
     * @param other the other vector
     * @return the cross product
     */
    public Vector3 cross(Vector3 other){
        return new Vector3(
                (y * other.z) - (z * other.y),
                (z * other.x) - (x * other.z),
                (x * other.y) - (y * other.x)
        );
    }

    /**
     * Returns the vector in unit distance
     * @return the unit vector
     */
    public Vector3 unit(){
        return new Vector3(x/length(), y/length(), z/length());
    }

    public float[] toArray(){
        return new float[]{x, y, z};
    }

    /**
     * For debugging prints out the Vector3
     * @return string representation of the object
     */
    @Override
    public String toString() {
        return "Vector3{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
