package distribution;

import util.ParetoRNG;

import java.util.Random;

/**
 * @author Jim Wang
 * @create 2017-11-05 11:54
 **/
//帕累托分布,
public class Pareto extends Distribute {
    private ParetoRNG paretoRNG;

    public Pareto(double k, double minValue, double maxValue){
        paretoRNG = new ParetoRNG(new Random(), k, minValue, maxValue);
    }

    @Override
    public double getDouble() {
        return paretoRNG.getDouble();
    }
}
