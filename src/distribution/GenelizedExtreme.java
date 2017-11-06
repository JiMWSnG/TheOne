package distribution;

/**
 * @author Jim Wang
 * @create 2017-11-05 0:36
 **/
//广义极值分布
public class GenelizedExtreme extends Distribute {
    private  double alpha;
    private  double beta;
    private double sigema;

    public GenelizedExtreme(double alpha, double beta, double sigema){
        super();
        this.alpha= alpha;
        this.beta = beta;
        this.sigema = sigema;
    }
    @Override
    public double getDouble() {
        double x = getRandom().nextDouble();
        double tnp =Math.pow(-Math.log(x), -sigema)-1;
        return alpha*tnp/sigema+beta;
    }
}
