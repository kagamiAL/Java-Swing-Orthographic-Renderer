import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;

public class Texture {

    private final BufferedImage image;

    private static String[] splitValues(String values){
        return values.trim().split("\\s+");
    }

    public Texture(BufferedImage image) {
        this.image = image;
    }

    public static Texture parseMTL(File mtlFile){
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(mtlFile))){
            String line;
            while ((line = bufferedReader.readLine()) != null){
                String[] values = splitValues(line);
                if (values[0].equals("map_Kd")){
                    return new Texture(ImageIO.read(new File(mtlFile.getParent(), values[1])));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public int getRGBAt(int x, int y){
        return image.getRGB(x, y);
    }

    public Color getVertexColor(double pixX, double pixY){
        double pixelXCoordinate = pixX * getWidth() - 0.5;
        double pixelYCoordinate = (1 - pixY) * getHeight() - 0.5;
        int x = (int) Math.floor(pixelXCoordinate);
        int y = (int) Math.floor(pixelYCoordinate);
        return new Color(getRGBAt(x, y), true);
    }

    public int getWidth(){
        return image.getWidth();
    }

    public int getHeight(){
        return image.getHeight();
    }
}
