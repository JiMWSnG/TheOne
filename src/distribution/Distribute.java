package distribution;

import java.util.Random;

/**
 * @author Jim Wang
 * @create 2017-11-04 15:56
 **/
//抽象分布类
public  abstract class Distribute {
    private Random random;
    public Distribute(){
        this.random = new Random();
    }
    /**
     *
     给定一个分布的密度函数f(x)，要生成满足这一分布的一组随机数。
     输入：一组[0,1]之间的满足均匀分布的随机数U
     输出：一组满足f(x)的随机数V
     方法：1）求f(x)的分布函数F(x)
     2）求F(x)的反函数F'(x)
     3）对于U中的每一个元素u，将F'(u)加入序列V中
     * @return
     */
    public abstract double getDouble();
    public Random getRandom(){
        return this.random;
    }
}
