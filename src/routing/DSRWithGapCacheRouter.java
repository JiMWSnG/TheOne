package routing;

import core.*;
import input.ICNMessageEventGenerator;
import report.P_R_GapReport;
import routing.util.Popularity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javafx.scene.input.KeyCode.R;

/**
 * Created by Jim Wang on 2016/12/28.
 */
public class DSRWithGapCacheRouter extends DSRRouter {
    public static final String INIT_GAP_N = "initGap";

    /**key:message name(data),value:Popularity*/
    private Map<String ,Object> popularities;
    /**setting  default = 4*/
    private  int N;

    public Map<String, Object> getPopularities() {
        return popularities;
    }

    public int getN() {
        return N;
    }

    public DSRWithGapCacheRouter(Settings s) {
        super(s);
        this.popularities = new HashMap<>();
        this.N = s.getInt(INIT_GAP_N,4);
    }

    protected DSRWithGapCacheRouter(DSRWithGapCacheRouter r) {
        super(r);
        this.popularities = r.getPopularities();
        this.N = r.getN();
    }


    @Override
    public Message messageTransferred(String id, DTNHost from) {
        Message aMessage =  super.messageTransferred(id, from);

        boolean isFinalRecipient;
       // boolean isFirstDelivery; // is this first delivered instance of the msg
        boolean isInterest;

        isInterest = aMessage.getProperty("type")==0;
        //data 的命中信号
        isFinalRecipient = aMessage.getTo() == this.getHost();
//        isFirstDelivery = isFinalRecipient &&
//                !isDeliveredMessage(aMessage);

        if(!isInterest){
            if(!isFinalRecipient){
                //cache :gap-cache
                int gap = (int)aMessage.getProperty("gc");
                if(gap <=0) {
                    if(!isDeliveredMessage(aMessage)){
                        this.getDeliveredMessages().put(aMessage.getId(),aMessage);
                        //cache listener
                        for (MessageListener ml : this.getmListeners()) {
                            ml.messageTransferred(aMessage, from, this.getHost(),
                                    false);
                        }
                    }
                    aMessage.updateProperty("gc", caculateGap(aMessage));
                    for(MessageListener ml : this.getmListeners()){
                        if(ml instanceof P_R_GapReport){
                            ml.messageTransferred(aMessage,from ,this.getHost(),false);
                        }
                    }
                }



            }
        }else{
            //add popularity
            String requestMsgName = aMessage.getProperty(ICNMessageEventGenerator.MESSAGE_RESPONSE_MSGNAME_S).toString();
            double time = SimClock.getTime();
            Popularity popularity;
           if(popularities.containsKey(requestMsgName)){
               popularity =  (Popularity)popularities.get(requestMsgName);

           }else{
               //new message popularity
               popularity = Popularity.newInstance(requestMsgName);
               popularities.put(popularity.getRequestMsgName(),popularity);
           }
            popularity.addTimePoint(time);





        }
        return aMessage;
    }

    /**
     * 计算gap
     * @param m data message
     * @return gap
     */
    private int caculateGap(Message m){

        Popularity popularity =  (Popularity)popularities.get(m.getId());
        if(popularity==null){
            return N;
        }
        double p = popularity.getPopularity(m.getId(),SimClock.getTime());
        double density = density(this.getHost().getConnections().size());
        //debug
//
//        System.out.println((int)Math.floor(N*density*Math.pow(0.5,p)+1));
//        System.out.println("popularity:"+p);
//        System.out.println("density:"+density);
//        System.out.println("N:"+N);
//        System.out.println("Pow:"+Math.pow(0.5,p));

        return (int)Math.floor(N*density*Math.pow(0.5,p)+1);
    }

    @Override
    protected boolean createResponseMessage(Message m) {
        int gap ;
        Message res = new Message(this.getHost(),m.getFrom(),
                m.getProperty("responseMsgName").toString(), m.getResponseSize());
        res.setRequest(m);
        gap = caculateGap(res);
        res.addProperty("gc",gap);
        res.addProperty("type",1);
        return this.createNewMessage(res);
    }

    @Override
    protected void removePath(Message message) {
        super.removePath(message);
        boolean isData = message.getProperty("type")==1;
        if(isData){
            int gap = (int)message.getProperty("gc");
            message.updateProperty("gc",--gap);

        }
    }

    @Override
    protected void cache(Message aMessage,DTNHost from,boolean isFirstDelivery) {
        //do nothing

    }
    public double caculateDensity(int n ){
        return density(n);
    }
    /**
     *  计算节点周围的线密度
     * @param n number of  up connections that a node has
     * @return
     */
    private double density(int n ){
        int L =1;//车道数
        double R = 0;//通信范围
        List<NetworkInterface> interfaces =this.getHost().getInterfaces();
        if(interfaces!=null&&interfaces.size()!=0){
            for(NetworkInterface niface :interfaces){
                R = niface.getTransmitRange()>R?niface.getTransmitRange():R;
            }

        }

        double p= 0;
        if(n>=L*R){
            p =  1.00;
        }
        if(n>0&&n<L*R){
            p =Math.log10((n+1)*100/R)>1.0?1.0:Math.log10((n+1)*100/R);
        }
        if(n<=0){
            p =0;
        }
//        System.out.println("n:"+n);
//        System.out.println("R:"+R);
//        System.out.println("p:"+p);
        return p;
    }

    @Override
    public DSRWithGapCacheRouter replicate() {
        return new DSRWithGapCacheRouter(this);
    }
}
