package input;

import constant.MessageTypeConstant;
import core.DTNHost;
import core.Message;
import core.World;

/**
 * Created by Jim Wang on 2016/12/25.
 *
 */
public class DATAINITMessageCreateEvent extends MessageCreateEvent {
    public static final int INTEREST_MESSAGE =  0;
    public static final int DATA_MESSAGE = 1;
    /** 消息类型，分为interest和data两种,m默认为Interest*/
    private int type= MessageTypeConstant.DATA_MESSAGE;

    public DATAINITMessageCreateEvent(int from, int to, String id, int size,
                                      int responseSize, double time) {
        super(from, to, id, size, responseSize, time);


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


        DTNHost from = world.getNodeByAddress(this.fromAddr);
        int size = this.getSize();
        Message m = new Message(from, null, this.id, size);
        m.addProperty("type",this.type);
        m.setResponseSize(this.getResponseSize());
        from.initData(m);
    }

    @Override
    public String toString() {
        return super.toString() + " [" + fromAddr + "->" + toAddr + "] " +
                "size:" + this.getSize() +"type:" + type + " CREATE";
    }
}
