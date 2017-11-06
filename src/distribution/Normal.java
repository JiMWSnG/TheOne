package distribution;

/**
 * @author Jim Wang
 * @create 2017-11-05 11:37
 **/
//正太分布
public class Normal extends Distribute {
    private double alpha;
    private double beta;

    public Normal(double alpha, double beta){
        super();
        this.alpha = alpha;
        this.beta = beta;
    }
    @Override
    public double getDouble() {
        return Math.sqrt(beta)*getRandom().nextGaussian()+alpha;
    }
}
