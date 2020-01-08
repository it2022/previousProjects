public class Body {
    public double xxPos;
    public double yyPos;
    public double xxVel;
    public double yyVel;
    public double mass;
    public String imgFileName;


    public Body(double xP, double yP, double xV,
                double yV, double m, String img){
        xxPos = xP;
        yyPos = yP;
        xxVel = xV;
        yyVel = yV;
        mass = m;
        imgFileName = img;
    }

    public Body(Body b){

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

    public double calcDistance(Body a){
        double dx = a.xxPos - this.xxPos;
        double dy = a.yyPos - this.yyPos;
        double rsq = (dx*dx) + (dy*dy);
        double r = Math.sqrt(rsq);
        return r;
    }
    public double calcForceExertedBy(Body c){
        double g = 6.67e-11;
        double f = ((g*this.mass*c.mass)/(calcDistance(c)*calcDistance(c)));
        return f;
    }
    public double calcForceExertedByX(Body d){
        double dx = d.xxPos - this.xxPos;
        double fx = ((calcForceExertedBy(d)*dx)/calcDistance(d));
        return fx;

    }
    public double calcForceExertedByY(Body e) {
        double dy = e.yyPos - this.yyPos;
        double fy = ((calcForceExertedBy(e)*dy)/calcDistance(e));
        return fy;
    }
    public double calcNetForceExertedByX(Body[] f){
        double netf = 0;
        for (Body i: f) {
            if (this.equals(i)) {
                continue;
            }
            netf = netf + calcForceExertedByX(i);
        }
        return netf;

    }
    public double calcNetForceExertedByY(Body[] g){
        double netf = 0;
        for (Body i: g) {
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
}