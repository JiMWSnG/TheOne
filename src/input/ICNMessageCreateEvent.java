package input;

import constant.MessageTypeConstant;
import core.*;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Jim Wang on 2016/12/25.
 *
 */
public class ICNMessageCreateEvent extends MessageCreateEvent {
    public static final int INTEREST_MESSAGE =  0;
    public static final int DATA_MESSAGE = 1;
    /** 消息类型，分为interest和data两种,m默认为Interest*/
    private int type= MessageTypeConstant.INTEREST_MESSAGE;
    private String responseMsgName;
    private  static Random rng = new Random();
    public ICNMessageCreateEvent(int from, int to, String id, int size,
                                 int responseSize, double time,String responseMsgName) {
        super(from, to, id, size, responseSize, time);
        //this.type = type;
        this.responseMsgName =responseMsgName;
        /* if prefix is unique, so will be the rng's sequence */

    }
    public int getType() {
        return type;
    }



    /**
     * Creates the message this event represents.用来产生Interest
     * interest message has from ,size,responseSize,type,responseMsgName,id,but without to DTNHost
     */
    @Override
    public void processEvent(World world) {

       // DTNHost to = null;
        List<DTNHost> hosts = world.getHosts();
        DTNHost from = getNodeByAddress( hosts, this.fromAddr);
        int size = this.getSize();
//        if(type==INTEREST_MESSAGE){
//            to =null;
//        }else if(type ==DATA_MESSAGE){
//           to =  world.getNodeByAddress(this.toAddr);
//        }else{
//            System.out.println("error");
//            return ;
//        }

       // Message m = new Message(from, to, this.id, this.size);
        Message m = new Message(from, null, this.id, size);

        m.addProperty("type",this.type);
       /// if(type==INTEREST_MESSAGE){
            m.addProperty("responseMsgName",responseMsgName);
            m.setResponseSize(this.getResponseSize());
        //}

        from.createNewMessage(m);
    }

    @Override
    public String toString() {
        return super.toString() + " [" + fromAddr + "->" + toAddr + "] " +
                "size:" + this.getSize() +"type:" + type + " CREATE";
    }
    private DTNHost getNodeByAddress(List<DTNHost> hosts,int address){
        if (address < 0 || address >= hosts.size()) {
           address = rng.nextInt(hosts.size());
        }

        DTNHost node = hosts.get(address);
               return node;

    }

}
