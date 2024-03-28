import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;

public class Texture {

    private final int[] pixelRGBColouring;

    private static String[] splitValues(String values){
        return values.trim().split("\\s+");
    }

    public Texture(int[] pixelRGBColouring) {
        this.pixelRGBColouring = pixelRGBColouring;
    }

    public int getRGBAt(int index){
        return pixelRGBColouring[index];
    }

    public static Texture parseMTL(File mtlFile){
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(mtlFile))){
            String line;
            while ((line = bufferedReader.readLine()) != null){
                String[] values = splitValues(line);
                if (values[0].equals("map_Kd")){
                    BufferedImage bufferedImage = ImageIO.read(new File(mtlFile.getParent(), values[1]));
                    return new Texture(bufferedImage.getRGB(
                            0,
                            0,
                            bufferedImage.getWidth(),
                            bufferedImage.getHeight(),
                            null,
                            0,
                            bufferedImage.getWidth())
                    );
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
