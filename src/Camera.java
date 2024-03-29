import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;

public class Camera {

    private static final Color DEFAULT_COLOR = Color.LIGHT_GRAY;

    public static final Vector3 GLOBAL_UP = new Vector3(0, 1, 0);

    private Vector3 lookVector = new Vector3(0, 0, 1);

    private Vector3 lightDirection = new Vector3(0, 0, 1);

    private final float[] projectionMatrix = new float[9];

    private final float[] identityMatrix = { 1, 0, 0, 0, 1, 0, 0, 0, 1 };

    private final int[] frameBuffer;

    private final float[] zBuffer;

    private final int height;

    private final int width;

    private static JFrame frame;

    private static JLabel label;

    private static BufferedImage bufferedImage;

    private static WritableRaster raster;

    public Camera(int height, int width) {
        this.height = height;
        this.width = width;
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        raster = bufferedImage.getRaster();
        frameBuffer = new int[width * height];
        zBuffer = new float[frameBuffer.length];
    }

    private static void display(BufferedImage image) {
        if (frame == null) {
            frame = new JFrame();
            frame.setTitle("this_might_be_epic");
            frame.setSize(image.getWidth(), image.getHeight());
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            label = new JLabel();
            label.setIcon(new ImageIcon(image));
            frame.getContentPane().add(label, BorderLayout.CENTER);
            frame.setLocationRelativeTo(null);
            frame.pack();
            frame.setVisible(true);
        } else
            label.setIcon(new ImageIcon(image));
    }

    private static float getDepthZ(Vector3 p1, Vector3 p2, Vector3 p3, float x, float y) {
        float det = (p2.y - p3.y) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.y - p3.y);
        float l1 = ((p2.y - p3.y) * (x - p3.x) + (p3.x - p2.x) * (y - p3.y)) / det;
        float l2 = ((p3.y - p1.y) * (x - p3.x) + (p1.x - p3.x) * (y - p3.y)) / det;
        float l3 = 1f - l1 - l2;
        return l1 * p1.z + l2 * p2.z + l3 * p3.z;
    }

    private static float edgeFunction(Vector3 a, Vector3 b, float x, float y) {
        return (a.x - b.x) * (y - a.y) - (a.y - b.y) * (x - a.x);
    }

    private static float[] getBoundingBoxMinMax(Face face) {
        float minX = Integer.MAX_VALUE;
        float minY = Integer.MAX_VALUE;
        float maxX = Integer.MIN_VALUE;
        float maxY = Integer.MIN_VALUE;
        Vector3[] vertices = {face.getProjectedA(), face.getProjectedB(), face.getProjectedC()};

        for (Vector3 vertex: vertices) {
            if (vertex.x < minX)
                minX = vertex.x;
            if (vertex.y < minY)
                minY = vertex.y;
            if (vertex.x > maxX)
                maxX = vertex.x;
            if (vertex.y > maxY)
                maxY = vertex.y;
        }

        return new float[] { minX, minY, maxX, maxY };
    }

    private void calculateProjectedVertices(Vector3[] vertices, Vector3[] projectedVertices) {
        int screenOriginX = width / 2 - 1;
        int screenOriginY = height / 2 - 1;
        Vector3 localRight = lookVector.cross(GLOBAL_UP).unit();
        Vector3 localUp = localRight.cross(lookVector).unit();
        for (int x = 0; x < vertices.length; x++) {
            Vector3 planeVertex = getProjectedVector(vertices[x]);
            projectedVertices[x] = new Vector3(screenOriginX + localRight.dot(planeVertex),
                    screenOriginY - localUp.dot(planeVertex), -vertices[x].z);
        }
    }

    private Vector3 getProjectedVector(Vector3 vertex) {
        return new Vector3(
                projectionMatrix[0] * vertex.x + projectionMatrix[3] * vertex.y + projectionMatrix[6] * vertex.z,
                projectionMatrix[1] * vertex.x + projectionMatrix[4] * vertex.y + projectionMatrix[7] * vertex.z,
                projectionMatrix[2] * vertex.x + projectionMatrix[5] * vertex.y + projectionMatrix[8] * vertex.z);
    }

    private void calculateProjectionMatrix() {
        float[] vector = lookVector.toArray();
        int arrayMin = 0;
        int arrayMax = 3;
        for (int x = arrayMin; x < arrayMax; x++) {
            for (int y = arrayMin; y < arrayMax; y++) {
                projectionMatrix[x + y * arrayMax] = identityMatrix[x + y * arrayMax] - (vector[x] * vector[y]);
            }
        }
    }

    private float getBackFaceSign(Face face) {
        Vector3 ab = face.getProjectedB().sub(face.getProjectedA());
        Vector3 ac = face.getProjectedC().sub(face.getProjectedA());
        return ab.x * ac.y - ac.x * ab.y;
    }

    private int getPixelColor(Item3D item3D, Face face, float w0, float w1, float w2){
        Texture texture = item3D.getTexture();
        if (texture != null) {
            Vector2 coordinates = face.getColorACoordinate().mul(w0).add(face.getColorBCoordinate().mul(w1)).add(face.getColorCCoordinate().mul(w2));
            return texture.getVertexColor(coordinates.x, coordinates.y);
        }
        return DEFAULT_COLOR.getRGB();
    }

    public void setLookVector(Vector3 lookVector) {
        this.lookVector = lookVector;
    }

    public int interpolateColor(int color, float t){
        int r = (color>>16)&0xFF;
        int g = (color>>8)&0xFF;
        int b = (color)&0xFF;
        int a = (color>>24)&0xFF;
        return new Color(
                (int)(r - (r*t)),
                (int)(g - (g*t)),
                (int)(b - (b*t)),
                a
        ).getRGB();
    }

    public void setLightDirection(Vector3 lightDirection) {
        this.lightDirection = lightDirection;
    }

    public void render(Item3D item3D) {
        calculateProjectionMatrix();
        calculateProjectedVertices(item3D.getVertices(), item3D.getProjectedVertices());
        Arrays.fill(zBuffer, Integer.MAX_VALUE);
        Arrays.fill(frameBuffer, Color.white.getRGB());
        for (Face face: item3D.getFaces()) {
            if (getBackFaceSign(face) >= 0) {
                continue;
            }
            float[] boxMinMax = getBoundingBoxMinMax(face);
            int xMin = (int) Math.max(0, Math.min(width - 1, Math.floor(boxMinMax[0])));
            int yMin = (int) Math.max(0, Math.min(height - 1, Math.floor(boxMinMax[1])));
            int xMax = (int) Math.max(0, Math.min(width - 1, Math.floor(boxMinMax[2])));
            int yMax = (int) Math.max(0, Math.min(height - 1, Math.floor(boxMinMax[3])));
            Vector3 a = face.getProjectedA();
            Vector3 b = face.getProjectedB();
            Vector3 c = face.getProjectedC();
            float area = edgeFunction(a, b, c.x, c.y); // Area of the triangle multiplied by 2
            float w0Step = -(a.y - b.y);
            float w1Step = -(b.y - c.y);
            float w2Step = -(c.y - a.y);
            float w0YStep = (a.x - b.x);
            float w1YStep = (b.x - c.x);
            float w2YStep = (c.x - a.x);
            float w0Initial = edgeFunction(a, b, xMin, yMin);
            float w1Initial = edgeFunction(b, c, xMin, yMin);
            float w2Initial = edgeFunction(c, a, xMin, yMin);

            for (int y = yMin; y <= yMax; y++) {
                float w0 = w0Initial;
                float w1 = w1Initial;
                float w2 = w2Initial;
                for (int x = xMin; x <= xMax; x++) {
                    if (w0 >= 0 && w1 >= 0 && w2 >= 0) {
                        float depth = getDepthZ(
                                a,
                                b,
                                c,
                                x,
                                y);
                        if (depth < zBuffer[y * width + x]) {
                            zBuffer[y * width + x] = depth;
                            frameBuffer[y * width + x] = interpolateColor(
                                    getPixelColor(
                                            item3D,
                                            face,
                                            w0/area,
                                            w1/area,
                                            w2/area
                                    ),
                                    Math.clamp(1 - face.getFaceNormal().dot(lightDirection), 0, 1)
                            );
                        }
                    }
                    w0 += w0Step;
                    w1 += w1Step;
                    w2 += w2Step;
                }
                w0Initial += w0YStep;
                w1Initial += w1YStep;
                w2Initial += w2YStep;
            }
        }
        raster.setDataElements(0, 0, width, height, frameBuffer);
        display(bufferedImage);
    }
}
