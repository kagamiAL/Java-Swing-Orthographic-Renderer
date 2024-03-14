public class Main {
    public static void main(String[] args) throws InterruptedException {
        Item3D item3D = ParseOBJ.parseObjFile("teapot.obj");
        if (item3D != null){
            item3D.setScale(100);
            Camera camera = new Camera(800, 800);
            Vector3 lookVector = new Vector3(0, 0, -1);
            double step = 0;
            while (true) {
                lookVector.x = (float) (Math.sin(step) * 1);
                camera.setLookVector(lookVector);
                camera.render(item3D);
                Thread.sleep(1);
                step += 0.01;
            }
        }
    }
}