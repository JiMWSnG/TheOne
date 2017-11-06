package distribution;

/**
 * @author Jim Wang
 * @create 2017-11-05 11:49
 **/
//对数正太分布
public class LogNormal extends Distribute {
    private Normal normal;
    public LogNormal(double alpha, double beta){
        normal = new Normal(alpha, beta);
    }

    @Override
    public double getDouble() {
        double x= normal.getDouble();
        return Math.exp(x);
    }
}
