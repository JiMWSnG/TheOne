package core;

public class SpeedCoord extends Coord {
    private double speed;
    public SpeedCoord(double x, double y, double speed){
        super(x, y);
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
