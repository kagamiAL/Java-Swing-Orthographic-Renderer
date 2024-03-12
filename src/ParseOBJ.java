import java.io.*;
import java.util.ArrayList;

public class ParseOBJ {

    private static String[] splitValues(String values){
        return values.trim().split("\\s+");
    }

    private static Vector3 parseVector3(String values){
        String[] separated = splitValues(values);
        return new Vector3(
                Double.parseDouble(separated[0]),
                Double.parseDouble(separated[1]),
                Double.parseDouble(separated[2])
        );
    }

    private static int[] parseFace(String values){
        String[] separated = splitValues(values);
        int[] face = new int[3];
        for (int x = 0; x < face.length; x++){
            face[x] = Integer.parseInt(separated[x]) - 1;
        }
        return face;
    }

    public static Item3D parseObjFile(String pathName){
        File objFile = new File(pathName);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(objFile))){
            ArrayList<Vector3> vertices = new ArrayList<>();
            ArrayList<int[]> faces = new ArrayList<>();
            String line;
            while ((line = bufferedReader.readLine()) != null){
                if (!line.isEmpty()) {
                    if (line.charAt(0) == 'v') {
                        vertices.add(parseVector3(line.substring(1)));
                    } else if (line.charAt(0) == 'f') {
                        faces.add(parseFace(line.substring(1)));
                    }
                }
            }
            return new Item3D(vertices.toArray(new Vector3[0]), faces.toArray(new int[0][3]));
        } catch (IOException e){
            System.out.println("Could not parse file!");
        }
        return null;
    }

}
