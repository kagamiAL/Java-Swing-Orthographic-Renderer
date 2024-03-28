import java.awt.*;
import java.util.ArrayList;

public class Item3D {

    private final Vector3[] vertices;

    private final Face[] faces;

    private final Vector3[] projectedVertices;

    private Texture texture = null;

    private Color[] texturePointsColor = null;

    public Item3D(Vector3[] vertices, int[][] faces){
        this.vertices = vertices;
        this.projectedVertices = new Vector3[vertices.length];
        this.faces = new Face[faces.length];
        for (int i = 0; i < faces.length; i++) {
            this.faces[i] = new Face(faces[i], vertices, this.projectedVertices);
        }
    }

    public void setTexture(Texture texture, ArrayList<double[]> texturePoints){
        this.texture = texture;
        texturePointsColor = new Color[texturePoints.size()];
        for (int i = 0; i < texturePoints.size(); i++) {
            double[] pix = texturePoints.get(i);
            double pixelXCoordinate = pix[0] * texture.getWidth() - 0.5;
            double pixelYCoordinate = (1-pix[1]) * texture.getHeight() - 0.5;
            int x = (int) Math.floor(pixelXCoordinate);
            int y = (int) Math.floor(pixelYCoordinate);
            texturePointsColor[i] = new Color(texture.getRGBAt(x, y), true);
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

    public Color getColorAt(int index){
        if (texture != null){
            return texturePointsColor[index];
        }
        return Color.WHITE;
    }
}
