/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package report;

import core.DTNHost;
import core.Message;
import core.MessageListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Report for the probability to obtain a cache hit
 along the path from a request node to a node caching the requested information object.
 * A new report line
 * is hit every time when  a  interest message hits.
 * Messages hit during the warm up period are ignored.
 * For output syntax, see {@link #HEADER}.
 */
public class CacheHitRatioReport extends Report implements MessageListener {
	public static String HEADER="# time  hit_cache  total_cache  hit_cache/total_cache";

	private Map<String,Double>  cacheHitRatio;


	/**
	 * Constructor.
	 */
	public CacheHitRatioReport() {
		init();
	}

	@Override
	public void init() {
		super.init();

		cacheHitRatio =new HashMap<String ,Double>();
		write(HEADER);
	}

	public void messageTransferred(Message m, DTNHost from, DTNHost to,
								   boolean firstDelivery) {
		//(isInterest,firstDelivery)
		//11 request命中
		//01 data第一次到达
		//00 data的初始化
		//10 request不是第一次命中

		boolean isInterest = m.getProperty("type")==0;
		if ( isInterest && firstDelivery && !isWarmup() && !isWarmupID(m.getId())) {
			int unHitNum =0;
			int hitNum = 0;
			Collection<Message> messages = to .getMessageCollection();
			Collection<Message> deliveryMessages = to.getRouter().getDeliveredMessages().values();
			for(Message mi :messages){
				if(mi.getProperty("type")==0) {//isInterest
					unHitNum++;
				}
			}
			for(Message mhi: deliveryMessages){
				if(mhi.getProperty("type")==0) {//isInterest
					hitNum++;
				}
			}
			double cacheHitRatioNum = (double)hitNum/(unHitNum + hitNum);
			cacheHitRatio.put(to.toString(),cacheHitRatioNum);



		}
//		if(!isInterest && !firstDelivery && !isWarmup() && !isWarmupID(m.getId())){
//
//		}

		reportValues();

	}

	public void newMessage(Message m) {
//		if (isWarmup()) {
//			addWarmupID(m.getId());
//			return;
//		}
////		boolean isData = m.getProperty("type")==1;
////		if(isData){
////			total++;
////		}
////		reportValues();
	}

	/**
	 * Writes the current values to report file
	 */
	private void reportValues() {
		double prob =0.0;
		Collection<Double> probs =  cacheHitRatio.values();
		for(double p: probs){
			prob+=p;

		}
		prob = prob/probs.size();
		write(format(getSimTime()) + " "  +
				" " + format(prob));
	}

	// nothing to implement for the rest
	public void messageDeleted(Message m, DTNHost where, boolean dropped) {}
	public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {}
	public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {}

	@Override
	public void done() {
		write("done----------------------");
		reportValues();
//		for( Map.Entry<String ,Double> entry :cacheHitRatio.entrySet()){
//			write(entry.getKey() + " "  +
//					" " + format(entry.getValue()));
//		}
		super.done();
	}
}
