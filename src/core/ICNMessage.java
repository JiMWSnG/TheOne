package core;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Jim Wang on 2016/12/25.
 * not used
 */
public class ICNMessage extends Message {
    public static final int INTEREST_MESSAGE =  0;
    public static final int DATA_MESSAGE = 1;
    /** 消息类型，分为interest和data两种*/
    private int type;

    public ICNMessage(DTNHost from, DTNHost to, String id, int size,int type) {
        super(from, to, id, size);
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    /**
     * Deep copies message data from other message. If new fields are
     * introduced to this class, most likely they should be copied here too
     * (unless done in constructor).
     * @param m The message where the data is copied
     */
    protected void copyFrom(Message m) {
        super.copyFrom(m);
        if(m instanceof ICNMessage){
            ICNMessage im = (ICNMessage)m;
            this.type = im.type;
        }
    }

    /**
     * Returns a replicate of this message (identical except for the unique id)
     * @return A replicate of the message
     */
    public Message replicate() {
        DTNHost from =this.getFrom();
        DTNHost to = this.getTo();
        String id = this.getId();
        int size = this.getSize();
        Message m = new ICNMessage(from, to, id, size,type);
        m.copyFrom(this);
        return m;
    }
}
