public class Main {
    public static void main(String[] args) throws InterruptedException {
        final int SCALE = 5;

        Item3D item3D = ParseOBJ.parseObjFile("item.obj");
        if (item3D != null){
            item3D.setScale(SCALE);
            Camera camera = new Camera(1000, 1000);
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