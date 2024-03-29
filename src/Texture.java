import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;

public class Texture {

    private final BufferedImage image;

    private final int[] rgbArray;

    private static String[] splitValues(String values){
        return values.trim().split("\\s+");
    }

    public Texture(BufferedImage image) {
        this.image = image;
        rgbArray = image.getRGB(0, 0, getWidth(), getHeight(), null, 0, getWidth());
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
        return rgbArray[y * getWidth() + x];
    }

    public int getVertexColor(double pixX, double pixY){
        double pixelXCoordinate = pixX * getWidth() - 0.5;
        double pixelYCoordinate = (1 - pixY) * getHeight() - 0.5;
        int x = (int) Math.floor(pixelXCoordinate);
        int y = (int) Math.floor(pixelYCoordinate);
        return getRGBAt(x, y);
    }

    public int getWidth(){
        return image.getWidth();
    }

    public int getHeight(){
        return image.getHeight();
    }
}
