package util;

/**
 * Created by Jim Wang on 2017/3/16.
 */

public class ZipfDistribution {
    private int N; // number
    private int s; // screw
    private double C;//constant

    public ZipfDistribution(int size, int skew, double c) {
        this.N = size;
        this.s = skew;
        this.C = c;
    }

    public double H(int n, int s) { // Harmonic number
        if(n == 1) {
            return C / Math.pow(1,s);
        } else {
            return ( C / Math.pow( n, s ) ) + H( n - 1, s );
        }
    }

    public double f(int k) {
     // return ( 1 / Math.pow(k, this.s) ) / H(this.N, this.s);
        return ( C / Math.pow(k, this.s) );
    }

    public double cdf(int k) {
        return H(k, this.s) / H(this.N, this.s);
//        return H(k, this.s) ;
    }

    public static void main(String[] args) {
        if(args.length != 3) {
            System.out.println("usage: ./zipf N s c");
            System.exit(-1);
        }

        int n = Integer.valueOf(args[0]);
        int s = Integer.valueOf(args[1]);
        double c= Integer.valueOf(args[2]);

        ZipfDistribution z = new ZipfDistribution(n, s,c);

        String output = "frequency\tcdf\n";
        for( int i = 1; i <= n; i++ ) {
            output += (z.f(i) + "\t" + z.cdf(i) + "\n" );
        }
        System.out.println(output);
    }
}