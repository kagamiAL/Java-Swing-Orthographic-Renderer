import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;

public class Camera {

    public static final Vector3 GLOBAL_UP = new Vector3(0, 1, 0);

    public static final int GREY_MAX = 180;

    private Vector3 lookVector = new Vector3(0, 0, -1);

    private Vector3 lightDirection = new Vector3(0, 0, 1);

    private int height = 512;

    private int width = 512;

    private static JFrame frame;

    private static JLabel label;

    private static BufferedImage bufferedImage;

    private static WritableRaster raster;

    public Camera(){
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        raster = bufferedImage.getRaster();
    }

    public Camera(int height, int width){
        this.height = height;
        this.width = width;
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        raster = bufferedImage.getRaster();
    }

    private static void display(BufferedImage image){
        if(frame==null){
            frame=new JFrame();
            frame.setTitle("this_might_be_epic");
            frame.setSize(image.getWidth(), image.getHeight());
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            label=new JLabel();
            label.setIcon(new ImageIcon(image));
            frame.getContentPane().add(label,BorderLayout.CENTER);
            frame.setLocationRelativeTo(null);
            frame.pack();
            frame.setVisible(true);
        }else label.setIcon(new ImageIcon(image));
    }

    private static float getDepthZ(Vector3 p1, Vector3 p2, Vector3 p3, float x, float y){
        float det = (p2.y - p3.y) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.y - p3.y);
        float l1  = ((p2.y - p3.y) * (x - p3.x) + (p3.x - p2.x) * (y - p3.y))/det;
        float l2 = ((p3.y - p1.y) * (x - p3.x) + (p1.x - p3.x) * (y - p3.y))/det;
        float l3 = 1f - l1 - l2;
        return l1 * p1.z + l2 * p2.z + l3 * p3.z;
    }

    private static float edgeFunction(Vector3 a, Vector3 b, float x, float y){
        return (a.x - b.x) * (y - a.y) - (a.y - b.y) * (x - a.x);
    }

    private static Vector3 getFaceNormal(Vector3[] vertices, int[] face){
        Vector3 v = vertices[face[1]].sub(vertices[face[0]]);
        Vector3 u = vertices[face[2]].sub(vertices[face[0]]);
        return v.cross(u).unit();
    }

    private static float[] getBoundingBoxMinMax(Vector3[] vertices, int[] face){
        float minX = Integer.MAX_VALUE;
        float minY = Integer.MAX_VALUE;
        float maxX = Integer.MIN_VALUE;
        float maxY = Integer.MIN_VALUE;

        for (int j : face) {
            Vector3 vertex = vertices[j];
            if (vertex.x < minX) minX = vertex.x;
            if (vertex.y < minY) minY = vertex.y;
            if (vertex.x > maxX) maxX = vertex.x;
            if (vertex.y > maxY) maxY = vertex.y;
        }

        return new float[]{minX, minY, maxX, maxY};
    }

    private Vector3[] getProjectedVertices(Vector3[] vertices){
        int screenOriginX = width/2 - 1;
        int screenOriginY = height/2 - 1;
        Vector3 localRight = lookVector.cross(GLOBAL_UP).unit();
        Vector3 localUp = localRight.cross(lookVector).unit();
        Vector3[] projectedVertices = new Vector3[vertices.length];

        for (int x = 0; x < vertices.length; x++) {
            Vector3 planeVertex = vertices[x].sub(vertices[x].project(lookVector));
            projectedVertices[x] = new Vector3(screenOriginX + localRight.dot(planeVertex), screenOriginY - localUp.dot(planeVertex), -vertices[x].z);
        }

        return projectedVertices;
    }

    public void setLookVector(Vector3 lookVector) {
        this.lookVector = lookVector;
    }

    public void setLightDirection(Vector3 lightDirection) {
        this.lightDirection = lightDirection;
    }

    public void render(Item3D item3D){
        Vector3[] projectedVertices = getProjectedVertices(item3D.getVertices());
        int[] frameBuffer = new int[width * height];
        float[] zBuffer = new float[width * height];
        Arrays.fill(zBuffer, Integer.MAX_VALUE);
        Arrays.fill(frameBuffer, Color.white.getRGB());
        for (int[] face: item3D.getFaces()){
            float[] boxMinMax = getBoundingBoxMinMax(projectedVertices, face);
            int xMin = (int) Math.max(0, Math.min(width - 1, Math.floor(boxMinMax[0])));
            int yMin = (int) Math.max(0, Math.min(height - 1, Math.floor(boxMinMax[1])));
            int xMax = (int) Math.max(0, Math.min(width - 1, Math.floor(boxMinMax[2])));
            int yMax = (int) Math.max(0, Math.min(height - 1, Math.floor(boxMinMax[3])));
            Vector3 a = projectedVertices[face[0]];
            Vector3 b = projectedVertices[face[1]];
            Vector3 c = projectedVertices[face[2]];
            float w0Step = -(a.y - b.y);
            float w1Step = -(b.y - c.y);
            float w2Step = -(c.y - a.y);
            float w0YStep = (a.x - b.x);
            float w1YStep = (b.x - c.x);
            float w2YStep = (c.x - a.x);
            float w0Initial = edgeFunction(a, b, xMin, yMin);
            float w1Initial = edgeFunction(b, c, xMin, yMin);
            float w2Initial = edgeFunction(c, a, xMin, yMin);
            for (int y = yMin; y <= yMax; y++){
                float w0 = w0Initial;
                float w1 = w1Initial;
                float w2 = w2Initial;
                for (int x = xMin; x <= xMax; x++){
                    if (w0 >= 0 && w1 >= 0 && w2 >= 0){
                        float depth = getDepthZ(
                                projectedVertices[face[0]],
                                projectedVertices[face[1]],
                                projectedVertices[face[2]],
                                x,
                                y
                        );
                        if (depth < zBuffer[y * width + x]){
                            int greyScale = (int)(Math.max(0, getFaceNormal(item3D.getVertices(), face).dot(lightDirection))*GREY_MAX);
                            int rgb = greyScale;
                            rgb = (rgb << 8) + greyScale;
                            rgb = (rgb << 8) + greyScale;
                            zBuffer[y * width + x] = depth;
                            frameBuffer[y * width + x] = rgb;
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
