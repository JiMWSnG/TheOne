package distribution;

import core.SettingsError;

/**
 * @author Jim Wang
 * @create 2017-11-06 23:00
 **/
public class DistributeFactory {
    public static Distribute getInstance(String type, double[] params) {
        if ("Exponential".equals(type)) {
            return new Exponential(params[0]);
        } else if ("GenelizedExtreme".equals(type)) {
            return new GenelizedExtreme(params[0], params[1], params[2]);
        } else if ("LogNormal".equals(type)) {
            return new LogNormal(params[0], params[1]);
        } else if ("Normal".equals(type)) {
            return new Normal(params[0], params[1]);
        } else if ("Pareto".equals(type)) {
            return new Pareto(params[0], params[1], params[2]);
        } else if ("Weibull".equals(type)) {
            return new Weibull(params[0], params[1]);
        } else {
            throw new SettingsError("Read unexpected amount (" + params.length +
                    ") of comma separated values for setting '"
                    + "sizeDistribution" + "of" + type);
        }
    }


}
