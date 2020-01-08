public class NBody{
    private static String imageToDraw = "images/starfield.jpg";
    public static void main (String[] args) {
        double T = Double.parseDouble(args[0]);
        double dt = Double.parseDouble(args[1]);
        double time =0;
        int e = 0;
        String filename = args[2];
        In in = new In(filename);
        int firstItemInFile = in.readInt();
        double[]  xForces = new double[firstItemInFile];
        double[]  yForces = new double[firstItemInFile];
        double rad = readRadius(filename);
        Body[] arr = readBodies(filename);
        StdDraw.setScale(-rad, rad);
        StdDraw.clear();
        StdDraw.picture(0,0,imageToDraw);
        StdDraw.show();
        StdDraw.pause(2000);
        while (e < arr.length) {

            arr[e].draw();
            e++;
        }
        StdDraw.enableDoubleBuffering();
        while (time < T){
            int z = 0;
            while (z < arr.length) {
                xForces[z] = arr[z].calcNetForceExertedByX(arr);
                yForces[z] = arr[z].calcNetForceExertedByY(arr);
                z++;
            }
            int y = 0;
            while (y < arr.length) {

                arr[y].update(dt, xForces[y], yForces[y]);
                y++;
            }
            StdDraw.picture(0,0,imageToDraw);
            int x = 0;
            while (x < arr.length) {
                arr[x].draw();
                x++;
            }

            StdDraw.show();
            StdDraw.pause(10);
            time = time + dt;
        }
        StdOut.printf("%d\n", arr.length);
        StdOut.printf("%.2e\n", rad);
        for (int i = 0; i < arr.length; i++) {
            StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
                    arr[i].xxPos, arr[i].yyPos, arr[i].xxVel,
                    arr[i].yyVel, arr[i].mass, arr[i].imgFileName);
        }

    }
    public static double readRadius(String s){
        In in = new In(s);
        int firstItemInFile = in.readInt();
        double secondItemInFile = in.readDouble();
        return secondItemInFile;
    }
    public static Body[] readBodies(String s){
        In in = new In(s);
        int firstItemInFile = in.readInt();
        double secondItemInFile = in.readDouble();
        Body[] arr = new Body[firstItemInFile];
        for (int i = 0; i < firstItemInFile; i++){
            double body1 = in.readDouble();
            double body2 = in.readDouble();
            double body3 = in.readDouble();
            double body4 = in.readDouble();
            double body5 = in.readDouble();
            String body6 = in.readString();

            Body body = new Body(body1, body2, body3, body4, body5, body6);
            arr[i] = body;

        }
        return arr;
    }

}