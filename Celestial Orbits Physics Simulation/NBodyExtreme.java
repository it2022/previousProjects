import java.util.ArrayList;
import java.util.Random;

public class NBodyExtreme{
    private static String imageToDraw = "images/starfield.jpg";
    public static void main (String[] args) {
        double T = Double.parseDouble(args[0]);
        double dt = Double.parseDouble(args[1]);
        double time =0;
        int e = 0;
        String filename = args[2];
        In in = new In(filename);
        int firstItemInFile = in.readInt();
        double rad = readRadius(filename);
        ArrayList<BodyExtreme> arr = readBodies(filename);
        StdDraw.setScale(-rad, rad);
        StdDraw.clear();
        StdDraw.picture(0,0,imageToDraw);
        StdDraw.show();
        StdDraw.pause(2000);
        while (e < arr.size()) {

            arr.get(e).draw();
            e++;
        }
        StdDraw.enableDoubleBuffering();
        while (time < T){
            if (StdDraw.isMousePressed()){
                Random r =  new Random();
                int x = r.nextInt(10) + 1;
                int sign = 1;
                int signn = -1;
                if (r.nextInt(2) == 0) {
                    sign = -1;
                }
                if (r.nextInt(2) == 0) {
                    signn = -1;
                }
                BodyExtreme body = new BodyExtreme(StdDraw.mouseX(), StdDraw.mouseY(), sign *4e4 ,signn *4e4,5.9740e+24, "spongebob.gif");
                arr.add(body);
            }
            ArrayList<Double>  xForces = new ArrayList<>();
            ArrayList<Double>  yForces = new ArrayList<>();
            int z = 0;
            while (z < arr.size()) {
                xForces.add(arr.get(z).calcNetForceExertedByX(arr));
                yForces.add(arr.get(z).calcNetForceExertedByY(arr));
                z++;
            }


            int y = 0;
            ArrayList<BodyExtreme> detected = new ArrayList<>();
            while (y < arr.size()) {
                BodyExtreme planet = arr.get(y).detectCollision(arr);
                if (planet != null && !detected.contains(planet)) {
                    detected.add(planet);
                    arr.get(y).collisionUpdate(planet, dt, xForces.get(y), yForces.get(y));


                }
                arr.get(y).update(dt, xForces.get(y), yForces.get(y));
                y++;
            }

            StdDraw.picture(0,0,imageToDraw);
            int x = 0;
            while (x < arr.size()) {
                arr.get(x).draw();
                x++;
            }

            StdDraw.show();
            StdDraw.pause(40);
            time = time + dt;
        }
        StdOut.printf("%d\n", arr.size());
        StdOut.printf("%.2e\n", rad);
        for (int i = 0; i < arr.size(); i++) {
            StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
                    arr.get(i).xxPos, arr.get(i).yyPos, arr.get(i).xxVel,
                    arr.get(i).yyVel, arr.get(i).mass, arr.get(i).imgFileName);
        }

    }
    public static double readRadius(String s){
        In in = new In(s);
        int firstItemInFile = in.readInt();
        double secondItemInFile = in.readDouble();
        return secondItemInFile;
    }
    public static ArrayList<BodyExtreme> readBodies(String s){
        In in = new In(s);
        int firstItemInFile = in.readInt();
        double secondItemInFile = in.readDouble();
        ArrayList<BodyExtreme> arr = new ArrayList<>();
        for (int i = 0; i < firstItemInFile; i++){
            double body1 = in.readDouble();
            double body2 = in.readDouble();
            double body3 = in.readDouble();
            double body4 = in.readDouble();
            double body5 = in.readDouble();
            String body6 = in.readString();

            BodyExtreme body = new BodyExtreme(body1, body2, body3, body4, body5, body6);
            arr.add(body);

        }
        return arr;
    }

}