package routing.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jim Wang on 2016/12/23.
 */
public class Popularity {
   /**data数据包的名字*/
    private String requestMsgName;
    /**请求某data的数据包到来的时间点,单位秒
     * 时间越近的在与后面，timePoints[0]最早*/
    private List<Double> timePoints;
    private static int N = 10;//限制时间点的数量，过去太久的请求就忽略了
    private double n =1;//衰减因子，在F中使用

    private Popularity(String requestMsgName){
        this.requestMsgName = requestMsgName;
        this.timePoints = new ArrayList<>();
    }
    /**静态工厂*/
    public static Popularity newInstance(String requestMsgName ){
        return new Popularity(requestMsgName);
    }

    public String getRequestMsgName() {
        return requestMsgName;
    }

    public List<Double> getTimePoints() {
        return timePoints;
    }
    /**添加时间点*/
    public boolean addTimePoint(double time ){
        while(timePoints.size()>=N){
            timePoints.remove(0);
        }
        return  timePoints.add(time);

    }

/**
     * weighing fuction,跟消息应用类型有关，不同的消息时效，具有不同的衰减性
     * @param t  time  /second
     * @return
     */
    //TODO:用一个interface实现接口，策略模式

    /**
     *  * t的单位为second
     * @param t 时间因子
     * @param n 衰减因子，n越小衰减越快，n越大衰减越慢
     * @return  热门度 p
     */

    public  double F(double t ,double n){
        if (t<0){
            t=0;
        }
        if(t>Double.MAX_VALUE){
            return 0;
        }
        //return 0.5*Math.exp(-0.5*t);
        return 0.5*Math.pow(0.5,Math.exp(-n)*t);
    }


    public  double getPopularity(String name,double time){
        double popularity = 0;
       for(double t :timePoints){
            popularity += F(time-t,n);
       }

       return popularity;
    }
    public static void main(String[] args){
        System.out.println(Math.pow(0.5,Math.exp(-0.5)));
        System.out.println(0.5*Math.pow(Math.pow(0.5,Math.exp(-1)),10));

        boolean ok =false;
        ok = ok|true;
        System.out.println(ok);

        String s = "M"+(int)(Math.random()*10);
        System.out.println(s);
    }
}
