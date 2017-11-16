/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package routing;

import constant.HostTypeContanst;
import constant.MessageTypeConstant;
import core.*;
import input.ICNMessageCreateEvent;
import util.Tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * DSR message router with drop-oldest buffer and only single transferring
 * connections at a time.
 */
public class DSRRouter extends ActiveRouter {

    /**
     * Constructor. Creates a new message router based on the settings in
     * the given Settings object.
     *
     * @param s The settings object
     */
    public DSRRouter(Settings s) {
        super(s);
        //TODO: read&use DSR router specific settings (if any)
    }

    /**
     * Copy constructor.
     *
     * @param r The router prototype where setting values are copied from
     */
    protected DSRRouter(DSRRouter r) {
        super(r);
        //TODO: copy DSR settings here (if any)
    }

    protected Set<String> getMessagesName() {
        return this.getMessages().keySet();
    }

    protected List<Message> getInterestMessage() {
        List<Message> interestMessage = new ArrayList<>();
        for (Message m : this.getMessageCollection()) {
            if (MessageTypeConstant.INTEREST_MESSAGE == (int)m.getProperty("type")) {
                interestMessage.add(m);
            }

        }
        if (interestMessage.size() == 0) {
            return null;
        }
        return interestMessage;
    }

    protected List<Message> getDeliveryDataMessage() {
        List<Message> dataMessage = new ArrayList<>();
        for (Message m : this.getDeliveredMessages().values()) {
            if (MessageTypeConstant.DATA_MESSAGE == (int)m.getProperty("type")) {
                dataMessage.add(m);
            }
        }
        if (dataMessage.size() == 0) {
            return null;
        }
        return dataMessage;


    }

    @Override
    public void update() {
        //更新了sendingconnection list
        super.update();
        //检查是否正在sending和是否能转发
        if (isTransferring() || !canStartTransfer()) {
            return; // transferring, don't try other connections yet
        }
        // 查找一跳能到达的message和connection，并发送，重写了requestDeliverableMessages
        // 和getMessagesForConnected函数
        if (exchangeDeliverableMessages() != null) {
            return; // started a transfer, don't try others (yet)
        }
        // then try any/all message to any/all connection
        this.tryAllMessagesToAllConnections();
    }

    @Override
    public boolean createNewMessage(Message m) {
        return super.createNewMessage(m);
    }

    @Override
    public int receiveMessage(Message m, DTNHost from) {
        Message message = m.replicate();
        //jim wang . data类型的沿路返回，含有请求包，间经过一跳减1
        removePath(message);
        int rcv = super.receiveMessage(message, from);
        if (rcv == MessageRouter.RCV_OK) {
            removePath(m);
        }
        return rcv;
    }

    @Override
    protected int startTransfer(Message m, Connection con) {
        return super.startTransfer(m, con);
    }

    @Override
    protected Message tryAllMessages(Connection con, List<Message> messages) {
        for (Message m : messages) {
            if (m.getProperty("type") != null && (int)m.getProperty("type") == MessageTypeConstant.DATA_MESSAGE) {//data
                DTNHost to = con.getOtherNode(getHost());
                List<DTNHost> path = m.getRequest().getHops();
                int hopCount = path.size();
                //沿路返回，response.requestMessage.path 经过一跳 - -
                if (hopCount > 1 && path.get(hopCount - 2) != to) {
                    continue;
                }
            }
            //intererst包flooding，data包沿路返回
            int retVal = startTransfer(m, con);
            if (retVal == RCV_OK) {
                return m;    // accepted a message, don't try others
            } else if (retVal > 0) {
                return null; // should try later -> don't bother trying others
            }
        }

        return null; // no message was accepted
    }

    //请求直接能到达的message
    // 两种情况  ：
    // 1. response data到达目的地
    // 2. 本节点是基站
       @Override
    public boolean requestDeliverableMessages(Connection con) {
        if (isTransferring()) {
            return false;
        }

        DTNHost other = con.getOtherNode(getHost());
		/* do a copy to avoid concurrent modification exceptions
		 * (startTransfer may remove messages) */
        ArrayList<Message> temp =
                new ArrayList<Message>(this.getMessageCollection());
        boolean isBaseStation = getHost().toString().startsWith(HostTypeContanst.BASESTATION);
        for (Message m : temp) {
            if (isBaseStation || other == m.getTo()  ) {
                if (startTransfer(m, con) == RCV_OK) {
                    return true;
                }
            }
        }
        return false;
    }

    //查找能直接到达目的地的messages和connections，模拟基站需要重写该func
    @Override
    protected List<Tuple<Message, Connection>> getMessagesForConnected() {
        if (getNrofMessages() == 0 || getConnections().size() == 0) {
			/* no messages -> empty list */
            return new ArrayList<Tuple<Message, Connection>>(0);
        }

        List<Tuple<Message, Connection>> forTuples =
                new ArrayList<Tuple<Message, Connection>>();
        for (Message m : getMessageCollection()) {
            for (Connection con : getConnections()) {
                DTNHost to = con.getOtherNode(getHost());
                String name = to.toString();
                //判断到达目的地
                if (name.startsWith(HostTypeContanst.BASESTATION)) {
                    forTuples.add(new Tuple<Message, Connection>(m,con));
                }
            }
        }

        return forTuples;
    }

    /**
     * interest 到达不命中和不到达终点的data存放在messages里面，命中的interest和data放在deliverymessages里面
     * 作为interest的响应data ，放在messages里面
     * 缓存的data放在deliverymessages里面
     *
     * @param id
     * @param from
     * @return
     */
    @Override
    public Message messageTransferred(String id, DTNHost from) {
        Message incoming = removeFromIncomingBuffer(id, from);
        boolean isFinalRecipient;
        boolean isFirstDelivery; // is this first delivered instance of the msg
        boolean isInterest;

        if (incoming == null) {
            throw new SimError("No message with ID " + id + " in the incoming " +
                    "buffer of " + this.getHost());
        }

        incoming.setReceiveTime(SimClock.getTime());

        // Pass the message to the application (if any) and get outgoing message
        Message outgoing = incoming;
        for (Application app : getApplications(incoming.getAppID())) {
            // Note that the order of applications is significant
            // since the next one gets the output of the previous.
            outgoing = app.handle(outgoing, this.getHost());
            if (outgoing == null) break; // Some app wanted to drop the message
        }

        Message aMessage = (outgoing == null) ? (incoming) : (outgoing);
        // If the application re-targets the message (changes 'to')
        // then the message is not considered as 'delivered' to this host.
        isInterest = (int)aMessage.getProperty("type") == 0;
        if (!isInterest) {//data
            isFinalRecipient = aMessage.getTo() == this.getHost();

        } else {//interest
            isFinalRecipient = false;
            List<Message> dataMessages = this.getDeliveryDataMessage();
//			if(dataMessages!=null){
//				dataMessages.addAll(this.getMessageCollection());
//			}else{
//				dataMessages = new ArrayList<>(this.getMessageCollection());
//			}
            //hit data
            if (dataMessages != null && dataMessages.size() != 0) {
                for (Message m : dataMessages) {
                    isFinalRecipient = isFinalRecipient | m.getId().equals(aMessage.getProperty("responseMsgName").toString());

                }
            }


        }
        isFirstDelivery = isFinalRecipient &&
                !isDeliveredMessage(aMessage);
        //debug
//		if(isFinalRecipient&&!isInterest)
//			System.out.println("11111111111111111111111111111111111111111111111111111111111");


        if (!isFinalRecipient && outgoing != null) {
            // not the final recipient and app doesn't want to drop the message
            // -> put to buffer
            /*if(isInterest){
				Map<String, Object> record = this.getHost().getRecord(aMessage.getId());
				List<String> froms = (List<String>)record.get("rquestNodeName");
				if(!froms.contains(aMessage.getFrom())){
					//String timestamp = String.valueOf(SimClock.getTime());
					//double popularity = Popularity.getPopularity(aMessage.getId(),SimClock.getTime());
					//this.getHost().addRecord(aMessage.getId(),aMessage.getFrom().toString(),popularity,timestamp);
					addToMessages(aMessage, false);
				}
			}else{

			}*/
            if (!this.getMessagesName().contains(aMessage.getId())) {
                if (!isInterest) {

                    //String timestamp = String.valueOf(SimClock.getTime());//second
                    //double popularity = Popularity.getPopularity(aMessage.getId(),SimClock.getTime());
                    //this.getHost().addRecord(aMessage.getId(),aMessage.getFrom().toString(),popularity,timestamp);
                    //TODO: cache strategy
                    if (!isDeliveredMessage(aMessage)) {
                        //put in deliveryMessageList
                        cache(aMessage, from, false);

                    }
                }
                addToMessages(aMessage, false);

            }


        } else if (isFirstDelivery) {
            this.getDeliveredMessages().put(id, aMessage);
            //create data response for interest package
            if (isInterest) {
                // create response message
                createResponseMessage(aMessage);
            }

        } else if (outgoing == null) {
            // Blacklist messages that an app wants to drop.
            // Otherwise the peer will just try to send it back again.
            this.getBlacklistedMessages().put(id, null);
        }
        //delivery event
        if (isFirstDelivery) {

            for (MessageListener ml : this.getmListeners()) {
                ml.messageTransferred(aMessage, from, this.getHost(),
                        isFirstDelivery);
            }
        }

        return aMessage;
    }

    /**
     * 对命中的interst，产生相应的data message
     *
     * @param m Interest message
     * @return
     */
    protected boolean createResponseMessage(Message m) {
        Message res = new Message(this.getHost(), m.getFrom(),
                m.getProperty("responseMsgName").toString(), m.getResponseSize());
        res.setRequest(m.replicate());
        res.addProperty("type", MessageTypeConstant.DATA_MESSAGE);

        return this.createNewMessage(res);
    }

    /**
     * 更新data message 沿路返回，走过的路
     *
     * @param message data message
     */
    protected void removePath(Message message) {
        if (message.getProperty("type") != null && (int)message.getProperty("type") == MessageTypeConstant.DATA_MESSAGE) {
            List<DTNHost> path = message.getRequest().getHops();
            if (path.size() >= 1)
                path.remove(path.size() - 1);
        }

    }

    protected void cache(Message aMessage, DTNHost from, boolean isFirstDelivery) {
        this.getDeliveredMessages().put(aMessage.getId(), aMessage);
        //cache listener
        for (MessageListener ml : this.getmListeners()) {
            ml.messageTransferred(aMessage, from, this.getHost(),
                    isFirstDelivery);
        }
    }

    @Override
    public DSRRouter replicate() {
        return new DSRRouter(this);
    }

    @Override
    public int getBufferSize() {
        return super.getBufferSize();
    }

    /**
     * 将deliveryMessages 和messages一起计算
     *
     * @return
     */
    @Override
    public int getFreeBufferSize() {
        int occupancy = 0;

        if (this.getBufferSize() == Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        for (Message m : getMessageCollection()) {
            occupancy += m.getSize();
        }
        //deliveryMessages
        for (Message m : getDeliveredMessages().values()) {
            occupancy += m.getSize();
        }

        return this.getBufferSize() - occupancy;
    }

    protected DSRRouter(ActiveRouter r) {
        super(r);
    }

    /**
     * 我把deliverymessages和messages一起作为存储空间来看，后者相当于内存，前者相当于硬盘	 * @param size Size of the new message
     */


    @Override
    protected void dropExpiredMessages() {
        List<Message> messages = new ArrayList<>(this.getMessageCollection());
        //add deliveryMessages
        messages.addAll(this.getDeliveredMessages().values());
        for (Message m : messages) {
            int ttl = m.getTtl();
            if (ttl <= 0) {
                deleteMessage(m.getId(), true);
            }

        }


    }

    @Override
    protected Message getNextMessageToRemove(boolean excludeMsgBeingSent) {
        List<Message> messages = new ArrayList<>(this.getMessageCollection());
        //add deliveryMessages
        messages.addAll(this.getDeliveredMessages().values());
        Message oldest = null;
        for (Message m : messages) {

            if (excludeMsgBeingSent && isSending(m.getId())) {
                continue; // skip the message(s) that router is sending
            }

            if (oldest == null) {
                oldest = m;
            } else if (oldest.getReceiveTime() > m.getReceiveTime()) {
                oldest = m;
            }
        }

        return oldest;
    }

    @Override
    public void deleteMessage(String id, boolean drop) {
        Message removed = removeFromMessages(id);
        if (removed == null) throw new SimError("no message for id " +
                id + " to remove at " + this.getHost());

        for (MessageListener ml : this.getmListeners()) {
            ml.messageDeleted(removed, this.getHost(), drop);
        }
    }

    @Override
    protected Message removeFromMessages(String id) {
        Message m = null;
        if (this.getMessages().containsKey(id)) {
            m = this.getMessages().remove(id);
            return m;
        } else if (this.getDeliveredMessages().containsKey(id)) {
            m = this.getDeliveredMessages().remove(id);
        }
        return m;

    }
}