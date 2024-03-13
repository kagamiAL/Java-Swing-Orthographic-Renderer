import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;

public class Camera {

    public static final Vector3 GLOBAL_UP = new Vector3(0, 1, 0);

    public static final int GREY_MAX = 175;

    private Vector3 lookVector = new Vector3(0, 0, -1);

    private Vector3 lightDirection = new Vector3(0, 0, -1);

    private int invertScale = 1;

    private int height = 512;

    private int width = 512;

    private static JFrame frame;

    private static JLabel label;

    private static BufferedImage bufferedImage;

    public Camera(){
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public Camera(int height, int width){
        this.height = height;
        this.width = width;
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
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

    private static double getDepthZ(Vector3 p1, Vector3 p2, Vector3 p3, Vector2 pixel){
        double det = (p2.y - p3.y) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.y - p3.y);
        double l1  = ((p2.y - p3.y) * (pixel.x - p3.x) + (p3.x - p2.x) * (pixel.y - p3.y))/det;
        double l2 = ((p3.y - p1.y) * (pixel.x - p3.x) + (p1.x - p3.x) * (pixel.y - p3.y))/det;
        double l3 = 1. - l1 - l2;
        return l1 * p1.z + l2 * p2.z + l3 * p3.z;
    }

    private static boolean edgeFunction(Vector3 a, Vector3 b, Vector2 p){
        return (a.x - b.x) * (p.y - a.y) - (a.y - b.y) * (p.x - a.x) >= 0;
    }

    private static Vector3 getFaceNormal(Vector3[] vertices, int[] face){
        Vector3 v = vertices[face[1]].sub(vertices[face[0]]);
        Vector3 u = vertices[face[2]].sub(vertices[face[0]]);
        return v.cross(u).unit();
    }

    private static Vector2[] getBoundingBoxMinMax(Vector3[] vertices, int[] face){
        Vector2 boxMin = new Vector2(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Vector2 boxMax = new Vector2(Integer.MIN_VALUE, Integer.MIN_VALUE);

        for (int j : face) {
            Vector3 vertex = vertices[j];
            if (vertex.x < boxMin.x) boxMin.x = vertex.x;
            if (vertex.y < boxMin.y) boxMin.y = vertex.y;
            if (vertex.x > boxMax.x) boxMax.x = vertex.x;
            if (vertex.y > boxMax.y) boxMax.y = vertex.y;
        }

        return new Vector2[]{boxMin, boxMax};
    }

    private Vector3[] getProjectedVertices(Vector3[] vertices, int scale){
        int screenOriginX = width/2 - 1;
        int screenOriginY = height/2 - 1;
        Vector3 localRight = lookVector.cross(GLOBAL_UP).unit();
        Vector3 localUp = localRight.cross(lookVector).unit();
        Vector3[] projectedVertices = new Vector3[vertices.length];

        for (int x = 0; x < vertices.length; x++) {
            Vector3 vertex = vertices[x].multiply(scale);
            Vector3 planeVertex = vertex.sub(vertex.project(lookVector));
            projectedVertices[x] = new Vector3(screenOriginX + invertScale*localRight.dot(planeVertex), screenOriginY - localUp.dot(planeVertex), -invertScale*vertex.z);
        }

        return projectedVertices;
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
        WritableRaster raster = bufferedImage.getRaster();
        Vector3[] projectedVertices = getProjectedVertices(item3D.getVertices(), item3D.getScale());
        int[] frameBuffer = new int[width * height];
        double[] zBuffer = new double[width * height];
        Arrays.fill(zBuffer, Integer.MAX_VALUE);
        Arrays.fill(frameBuffer, Color.white.getRGB());
        for (int[] face: item3D.getFaces()){
            Vector2[] boxMinMax = getBoundingBoxMinMax(projectedVertices, face);
            int greyScale = (int)(Math.round(Math.max(0, getFaceNormal(projectedVertices, face).dot(lightDirection))*GREY_MAX));
            int xMin = (int) Math.max(0, Math.min(width - 1, Math.floor(boxMinMax[0].x)));
            int yMin = (int) Math.max(0, Math.min(height - 1, Math.floor(boxMinMax[0].y)));
            int xMax = (int) Math.max(0, Math.min(width - 1, Math.floor(boxMinMax[1].x)));
            int yMax = (int) Math.max(0, Math.min(height - 1, Math.floor(boxMinMax[1].y)));
            for (int y = yMin; y <= yMax; y++){
                for (int x = xMin; x <= xMax; x++){
                    boolean inside = true;
                    Vector2 pixel = new Vector2(x, y);
                    inside &= edgeFunction(projectedVertices[face[0]], projectedVertices[face[1]], pixel);
                    inside &= edgeFunction(projectedVertices[face[1]], projectedVertices[face[2]], pixel);
                    inside &= edgeFunction(projectedVertices[face[2]], projectedVertices[face[0]], pixel);
                    if (inside){
                        double depth = getDepthZ(
                                projectedVertices[face[0]],
                                projectedVertices[face[1]],
                                projectedVertices[face[2]],
                                pixel
                        );
                        if (depth < zBuffer[y * width + x]){
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
