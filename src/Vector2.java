public class Vector2 {

    public final float x;

    public final float y;

    public Vector2(float x, float y){
        this.x = x;
        this.y = y;
    }

    public Vector2 add(Vector2 o){
        return new Vector2(x + o.x, y + o.y);
    }

    public Vector2 sub(Vector2 o){
        return new Vector2(x - o.x, y - o.y);
    }

    public Vector2 mul(float scalar){
        return new Vector2(x * scalar, y * scalar);
    }

    @Override
    public String toString() {
        return "Vector2{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
