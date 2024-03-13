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

    private final double[][] projectionMatrix = {
            {0, 0, 0},
            {0, 0, 0},
            {0, 0, 0},
    };

    private final double[][] identityMatrix = {
            {1, 0, 0},
            {0, 1, 0},
            {0, 0, 1}
    };

    private int invertScale = 1;

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

    private static double getDepthZ(Vector3 p1, Vector3 p2, Vector3 p3, double x, double y){
        double det = (p2.y - p3.y) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.y - p3.y);
        double l1  = ((p2.y - p3.y) * (x - p3.x) + (p3.x - p2.x) * (y - p3.y))/det;
        double l2 = ((p3.y - p1.y) * (x - p3.x) + (p1.x - p3.x) * (y - p3.y))/det;
        double l3 = 1. - l1 - l2;
        return l1 * p1.z + l2 * p2.z + l3 * p3.z;
    }

    private static boolean edgeFunction(Vector3 a, Vector3 b, double x, double y){
        return (a.x - b.x) * (y - a.y) - (a.y - b.y) * (x - a.x) >= 0;
    }

    private static Vector3 getFaceNormal(Vector3[] vertices, int[] face){
        Vector3 v = vertices[face[1]].sub(vertices[face[0]]);
        Vector3 u = vertices[face[2]].sub(vertices[face[0]]);
        return v.cross(u).unit();
    }

    private static double[] getBoundingBoxMinMax(Vector3[] vertices, int[] face){
        double minX = Integer.MAX_VALUE;
        double minY = Integer.MAX_VALUE;
        double maxX = Integer.MIN_VALUE;
        double maxY = Integer.MIN_VALUE;

        for (int j : face) {
            Vector3 vertex = vertices[j];
            if (vertex.x < minX) minX = vertex.x;
            if (vertex.y < minY) minY = vertex.y;
            if (vertex.x > maxX) maxX = vertex.x;
            if (vertex.y > maxY) maxY = vertex.y;
        }

        return new double[]{minX, minY, maxX, maxY};
    }

    private Vector3[] getProjectedVertices(Vector3[] vertices, int scale){
        int screenOriginX = width/2 - 1;
        int screenOriginY = height/2 - 1;
        Vector3 localRight = lookVector.cross(GLOBAL_UP).unit();
        Vector3 localUp = localRight.cross(lookVector).unit();
        Vector3[] projectedVertices = new Vector3[vertices.length];

        for (int x = 0; x < vertices.length; x++) {
            Vector3 vertex = vertices[x].multiply(scale);
            Vector3 planeVertex = getProjectedVector(vertex);
            projectedVertices[x] = new Vector3(screenOriginX + invertScale*localRight.dot(planeVertex), screenOriginY - localUp.dot(planeVertex), -invertScale*vertex.z);
        }

        return projectedVertices;
    }

    private Vector3 getProjectedVector(Vector3 vertex){
        return new Vector3(
                projectionMatrix[0][0] * vertex.x + projectionMatrix[0][1] * vertex.y + projectionMatrix[0][2] * vertex.z,
                projectionMatrix[1][0] * vertex.x + projectionMatrix[1][1] * vertex.y + projectionMatrix[1][2] * vertex.z,
                projectionMatrix[2][0] * vertex.x + projectionMatrix[2][1] * vertex.y + projectionMatrix[2][2] * vertex.z
        );
    }

    private void calculateProjectionMatrix(){
        double[] vector = lookVector.toArray();
        for (int x = 0; x < 3; x++){
            for (int y = 0; y < 3; y++){
                projectionMatrix[x][y] = identityMatrix[x][y] - (vector[x]*vector[y]);
            }
        }
    }

    public void setLookVector(Vector3 lookVector) {
        this.lookVector = lookVector;
    }

    public void setLightDirection(Vector3 lightDirection) {
        this.lightDirection = lightDirection;
    }

    public void setInvertScale(int invertScale) {
        this.invertScale = invertScale;
    }

    public void render(Item3D item3D){
        calculateProjectionMatrix();
        Vector3[] projectedVertices = getProjectedVertices(item3D.getVertices(), item3D.getScale());
        int[] frameBuffer = new int[width * height];
        double[] zBuffer = new double[width * height];
        Arrays.fill(zBuffer, Integer.MAX_VALUE);
        Arrays.fill(frameBuffer, Color.white.getRGB());
        for (int[] face: item3D.getFaces()){
            double[] boxMinMax = getBoundingBoxMinMax(projectedVertices, face);
            int xMin = (int) Math.max(0, Math.min(width - 1, Math.floor(boxMinMax[0])));
            int yMin = (int) Math.max(0, Math.min(height - 1, Math.floor(boxMinMax[1])));
            int xMax = (int) Math.max(0, Math.min(width - 1, Math.floor(boxMinMax[2])));
            int yMax = (int) Math.max(0, Math.min(height - 1, Math.floor(boxMinMax[3])));
            for (int y = yMin; y <= yMax; y++){
                for (int x = xMin; x <= xMax; x++){
                    boolean inside = true;
                    inside &= edgeFunction(projectedVertices[face[0]], projectedVertices[face[1]], x, y);
                    inside &= edgeFunction(projectedVertices[face[1]], projectedVertices[face[2]], x, y);
                    inside &= edgeFunction(projectedVertices[face[2]], projectedVertices[face[0]], x, y);
                    if (inside){
                        double depth = getDepthZ(
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
                }
            }
        }
        raster.setDataElements(0, 0, width, height, frameBuffer);
        display(bufferedImage);
    }
}
