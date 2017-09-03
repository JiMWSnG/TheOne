/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package report;

import core.DTNHost;
import core.Message;
import core.MessageListener;

import java.util.List;

/**
 * Report information about all hit interest messages. Messages created during
 * the warm up period are ignored.
 * For output syntax, see {@link #HEADER}.
 */
public class AverageHitMessagesReport extends Report implements MessageListener {
	public static String HEADER = "# deliveryTime  " +
		"hopCount";
	private double deliveryTime;
	private int  hopCount;
	private int count;
	/**
	 * Constructor.
	 */
	public AverageHitMessagesReport() {
		init();
	}
	
	@Override
	public void init() {
		super.init();
		this.deliveryTime =0;
		this.hopCount= 0;
		this.count =0;
		write(HEADER);
	}

	/** 
	 * Returns the given messages hop count
	 * @param m The message
	 * @return hop count
	 */
	private int getPathSize(Message m) {
		return m.getHops().size();
	}
	
	public void messageTransferred(Message m, DTNHost from, DTNHost to, 
			boolean firstDelivery) {
		boolean isInterest = (int)m.getProperty("type")==0;
		if (!isWarmupID(m.getId()) && firstDelivery && isInterest) {
			this.deliveryTime +=getSimTime() - m.getCreationTime();
			this.hopCount += getPathSize(m);
			this.count++;
//			int ttl = m.getTtl();
//			write(format(getSimTime()) + " " + m.getId() + " " +
//					m.getSize() + " " + m.getHopCount() + " " +
//					format(getSimTime() - m.getCreationTime()) + " " +
//					m.getFrom() + " " + m.getTo() + " " +
//					(ttl != Integer.MAX_VALUE ? ttl : "n/a") +
//					(m.isResponse() ? " Y " : " N ") + getPathSize(m));
			write(format(getSimTime() - m.getCreationTime())+" "+getPathSize(m));
		}
	}

	public void newMessage(Message m) {
		if (isWarmup()) {
			addWarmupID(m.getId());
		}
	}
	
	// nothing to implement for the rest
	public void messageDeleted(Message m, DTNHost where, boolean dropped) {}
	public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {}
	public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {}

	@Override
	public void done() {
		write("averageTime:"+deliveryTime/count+"  "+"averageHopCount:"+(double)hopCount/count);
		super.done();
	}
}
