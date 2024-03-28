import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ParseOBJ {

    private static String[] splitValues(String values){
        return values.trim().split("\\s+");
    }

    private static Vector3 parseVector3(String[] values){
        return new Vector3(
                Float.parseFloat(values[0]),
                Float.parseFloat(values[1]),
                Float.parseFloat(values[2])
        );
    }

    private static int[] parseFace(String[] values){
        int[] face = new int[3];
        for (int x = 0; x < face.length; x++){
            if (values[x].contains("/")){
                String[] subValues = values[x].split("/+");
                face[x] = Integer.parseInt(subValues[0]) - 1;
            } else {
                face[x] = Integer.parseInt(values[x]) - 1;
            }
        }
        return face;
    }

    private static double[] parseVT(String[] values){
        return new double[]{
            Double.parseDouble(values[0]),
            Double.parseDouble(values[1])
        };
    }

    public static Item3D parseObjFile(String pathName){
        File objFile = new File(pathName);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(objFile))){
            ArrayList<Vector3> vertices = new ArrayList<>();
            ArrayList<int[]> faces = new ArrayList<>();
            ArrayList<double[]> vtPoints = new ArrayList<>();
            Texture texture = null;
            String line;
            while ((line = bufferedReader.readLine()) != null){
                if (!line.isEmpty()) {
                    String[] values = splitValues(line);
                    switch (values[0]){
                        case "v" -> vertices.add(parseVector3(Arrays.copyOfRange(values, 1, values.length)));
                        case "f" -> faces.add(parseFace(Arrays.copyOfRange(values, 1, values.length)));
                        case "vt" -> vtPoints.add(parseVT(Arrays.copyOfRange(values, 1, values.length)));
                        case "mtllib" -> texture = Texture.parseMTL(new File(objFile.getParentFile(), values[1]));
                    }
                }
            }
            Item3D item3D = new Item3D(vertices.toArray(new Vector3[0]), faces.toArray(new int[0][3]));
            if (texture != null){
                item3D.setTexture(texture, vtPoints);
            }
            return item3D;
        } catch (IOException e){
            System.out.println("Could not parse file!");
        }
        return null;
    }

}
