import java.util.ArrayList;

public class BodyExtreme {
    public double xxPos;
    public double yyPos;
    public double xxVel;
    public double yyVel;
    public double radius = 5e9;
    public double mass;
    public String imgFileName;



    public BodyExtreme(double xP, double yP, double xV,
                double yV, double m, String img){
        xxPos = xP;
        yyPos = yP;
        xxVel = xV;
        yyVel = yV;
        mass = m;
        imgFileName = img;
    }

    public BodyExtreme(BodyExtreme b){

        xxPos = b.xxPos;
        yyPos = b.yyPos;
        xxVel = b.xxVel;
        yyVel = b.yyVel;
        mass = b.mass;
        imgFileName = b.imgFileName;

    }

    public void draw(){

        StdDraw.picture(this.xxPos, this.yyPos, "images/" + this.imgFileName);

    }

    public double calcDistance(BodyExtreme a){
        double dx = a.xxPos - this.xxPos;
        double dy = a.yyPos - this.yyPos;
        double rsq = (dx*dx) + (dy*dy);
        double r = Math.sqrt(rsq);
        return r;
    }
    public double calcForceExertedBy(BodyExtreme c){
        double g = 6.67e-11;
        double f = ((g*this.mass*c.mass)/(calcDistance(c)*calcDistance(c)));
        return f;
    }
    public double calcForceExertedByX(BodyExtreme d){
        double dx = d.xxPos - this.xxPos;
        double fx = ((calcForceExertedBy(d)*dx)/calcDistance(d));
        return fx;

    }
    public double calcForceExertedByY(BodyExtreme e) {
        double dy = e.yyPos - this.yyPos;
        double fy = ((calcForceExertedBy(e)*dy)/calcDistance(e));
        return fy;
    }
    public double calcNetForceExertedByX(ArrayList<BodyExtreme> f){
        double netf = 0;
        for (BodyExtreme i: f) {
            if (this.equals(i)) {
                continue;
            }
            netf = netf + calcForceExertedByX(i);
        }
        return netf;

    }
    public double calcNetForceExertedByY(ArrayList<BodyExtreme> g){
        double netf = 0;
        for (BodyExtreme i: g) {
            if (this.equals(i)) {
                continue;
            }
            netf = netf + calcForceExertedByY(i);
        }
        return netf;
    }
    public void update(double dt, double fX, double fY){
        double accX = fX/this.mass;
        double accY = fY/this.mass;
        double newVelocityX = (this.xxVel + (dt*accX));
        double newVelocityY = (this.yyVel + (dt*accY));
        double newPosX = (this.xxPos + (dt*newVelocityX));
        double newPosY = (this.yyPos + (dt*newVelocityY));
        xxVel = newVelocityX;
        yyVel = newVelocityY;
        xxPos = newPosX;
        yyPos = newPosY;

    }
//
//    public static void radiusCreator (ArrayList<BodyExtreme bodies){
//        for(BodyExtreme i:bodies) {
//            i.radius = radius;
//        }
//    }
    public BodyExtreme detectCollision(ArrayList<BodyExtreme> bodies){
        for (BodyExtreme i: bodies) {
            if (this.equals(i)) {
                continue;
            }
            double dist = calcDistance(i);
            double rad1 = this.radius + i.radius;
            if (dist < rad1) {
                return i;
            }
        }
        return null;
    }
//    public void collisionCourse (BodyExtreme body, double dt, double fX, double fY) {
//            for (BodyExtreme i: bodies) {
//                collisionUpdate(i, dt, fX, fY);
//            }
//    }
    public void collisionUpdate(BodyExtreme body, double dt, double fX, double fY){

            double bodyVelX = (((body.mass - this.mass)/(body.mass + this.mass)) * body.xxVel)
                    + (((2*this.mass)/(body.mass + this.mass))*this.xxVel);
            double bodyVelY = (((body.mass - this.mass)/(body.mass + this.mass)) * body.yyVel)
                + (((2*this.mass)/(body.mass + this.mass))*this.yyVel);
            double thisVelX = (((this.mass - body.mass)/(this.mass + body.mass)) * this.xxVel)
                + (((2*body.mass)/(this.mass + body.mass))*body.xxVel);
            double thisVelY = (((this.mass - body.mass)/(this.mass + body.mass)) * this.yyVel)
                + (((2*body.mass)/(this.mass + body.mass))*body.yyVel);

            this.xxVel = thisVelX;
            this.yyVel = thisVelY;
            body.xxVel = bodyVelX;
            body.yyVel = bodyVelY;




    }
}