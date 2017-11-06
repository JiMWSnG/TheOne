package distribution;

/**
 * @author Jim Wang
 * @create 2017-11-05 12:13
 **/
//韦布尔分布 f(x;lamda, k) =k/lamda*(x/lamda)^(k-1)*e^(-(x/lamda)^k), x>=0
public class Weibull extends Distribute {
    private double lamda;
    private double k;

    public  Weibull(double lamda, double k){
        super();
        this.lamda = lamda;
        this.k = k;
    }

    @Override
    public double getDouble() {
        double x = getRandom().nextDouble();
        return lamda*Math.pow(-Math.log(1-x), 1/k);
    }
}
