package distribution;

/**
 * @author Jim Wang
 * @create 2017-11-05 12:09
 **/
public class Exponential extends Distribute {
    private double lamda;

    public Exponential(double lamba){
        super();
        this.lamda = lamba;
    }
    @Override
    public double getDouble() {
        double x = getRandom().nextDouble();
        return (-Math.log(1-x))/lamda;
    }
}
