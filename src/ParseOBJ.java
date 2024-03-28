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

    private static Face parseFace(String[] values, ArrayList<Vector3> vertices, ArrayList<Vector2> texturePoints2D){
        int[] vertexIndices = new int[values.length];
        int[] colorIndices = new int[values.length];
        for (int x = 0; x < values.length; x++){
            if (values[x].contains("/")){
                String[] subValues = values[x].split("/+");
                vertexIndices[x] = Integer.parseInt(subValues[0]) - 1;
                if (!subValues[1].isBlank()){
                    colorIndices[x] = (Integer.parseInt(subValues[1]) - 1);
                }
            } else {
                vertexIndices[x] = Integer.parseInt(values[x]) - 1;
            }
        }
        Face face = new Face(vertexIndices, vertices);
        if (!texturePoints2D.isEmpty()){
            face.setTexturePoints(texturePoints2D, colorIndices);
        }
        return face;
    }

    private static Vector2 parseVT(String[] values){
        return new Vector2(Float.parseFloat(values[0]), Float.parseFloat(values[1]));
    }

    public static Item3D parseObjFile(String pathName){
        File objFile = new File(pathName);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(objFile))){
            ArrayList<Vector3> vertices = new ArrayList<>();
            ArrayList<Face> faces = new ArrayList<>();
            ArrayList<Vector2> texturePoints2D = new ArrayList<>();
            Texture texture = null;
            String line;
            while ((line = bufferedReader.readLine()) != null){
                if (!line.isEmpty()) {
                    String[] values = splitValues(line);
                    switch (values[0]){
                        case "v" -> vertices.add(parseVector3(Arrays.copyOfRange(values, 1, values.length)));
                        case "f" -> faces.add(parseFace(
                                Arrays.copyOfRange(values, 1, values.length),
                                vertices,
                                texturePoints2D
                        ));
                        case "vt" -> texturePoints2D.add(parseVT(Arrays.copyOfRange(values, 1, values.length)));
                        case "mtllib" -> texture = Texture.parseMTL(new File(objFile.getParentFile(), values[1]));
                    }
                }
            }
            Item3D item3D = new Item3D(vertices.toArray(new Vector3[0]), faces.toArray(new Face[0]));
            if (texture != null){
                item3D.setTexture(texture);
            }
            return item3D;
        } catch (IOException e){
            System.out.println("Could not parse file!");
        }
        return null;
    }

}
