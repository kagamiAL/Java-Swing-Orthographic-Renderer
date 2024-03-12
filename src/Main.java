public class Main {
    public static void main(String[] args) {
        Item3D item3D = ParseOBJ.parseObjFile("icosahedron.obj");
        if (item3D != null){
            Camera camera = new Camera();
            camera.render(item3D);
        }
    }
}