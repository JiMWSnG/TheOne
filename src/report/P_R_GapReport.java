/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package report;

import core.DTNHost;
import core.Message;
import core.MessageListener;
import core.SimClock;
import routing.DSRWithGapCacheRouter;
import routing.MessageRouter;
import routing.util.Popularity;

/**
 * Report information about all hit interest messages. Messages created during
 * the warm up period are ignored.
 * For output syntax, see {@link #HEADER}.
 */
public class P_R_GapReport extends Report implements MessageListener {
	public static String HEADER = "# time  " +"popularity  "+
		"density  " +"gap ";

	/**
	 * Constructor.
	 */
	public P_R_GapReport() {
		init();
	}
	
	@Override
	public void init() {
		super.init();

		write(HEADER);
	}

	/** 
	 * Returns the given messages hop count
	 * @param m The message
	 * @return hop count
	 */

	public void messageTransferred(Message m, DTNHost from, DTNHost to, 
			boolean firstDelivery) {

		//以to为当前listen所在节点
		MessageRouter router = to.getRouter();

		if(!(router instanceof DSRWithGapCacheRouter)){
			return ;
		}
		DSRWithGapCacheRouter gapRouter = (DSRWithGapCacheRouter)router;
		Popularity popularity =  (Popularity)gapRouter.getPopularities().get(m.getId());
		double p= gapRouter.getN();
		if(popularity!=null){

			p = popularity.getPopularity(m.getId(), SimClock.getTime());
		}

		double density = gapRouter.caculateDensity(to.getConnections().size());
		int  gap = (int)Math.floor(gapRouter.getN()*density*Math.pow(0.5,p)+1);
		//write(format(getSimTime())+" "+format(p)+" "+format(density)+" "+gap);
		//write(format(p));
		//write(format(density));
		write(gap+"");
	}

	public void newMessage(Message m) {

	}
	
	// nothing to implement for the rest
	public void messageDeleted(Message m, DTNHost where, boolean dropped) {}
	public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {}
	public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {}

	@Override
	public void done() {
		super.done();
	}
}
