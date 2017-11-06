/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package input;

import core.Settings;
import distribution.Distribute;
import distribution.DistributeFactory;
import util.ParetoRNG;

/**
 * Message creation -external events generator. Creates one  ICNmessage from
 * one source node (defined with {@link MessageEventGenerator#HOST_RANGE_S})
 * to none destination (defined with
 * {@link MessageEventGenerator#TO_HOST_RANGE_S}). 
 * The message size, first messages time and the intervals between creating 
 * messages can be configured like with {@link MessageEventGenerator}. End
 * time is not respected, but messages are created until every from-node has
 * created a message.
 * type:interest
 * @see MessageEventGenerator
 * 请求的数据事先初始化好，需要请求数据的name和size
 */
public class ICNMessageEventGenerator extends MessageEventGenerator {
	/** Message type range -setting id ({@value}). Can be either a interest
	 * package or a data package,which is defined by 0 and 1.
	 *.interest package :0, data package:1.
	 * Defines the message type . */
	//public static final String MESSAGE_TYPE_S = "type";
	public static final String MESSAGE_RESPONSE_SIZE_S = "responseSize";
	public static final String MESSAGE_RESPONSE_MSGNAME_S = "responseMsgName";
	/**
	 * distribution的类型有：Exponential， GenelizedExtreme，LogNormal，Normal，Pareto，
	 *Weibull
	 */
	public static final String MESSAGE_SIZE_DISTRIBUTE = "sizeDistribute";
	public static final String DISTRIBUTE_PARAM_S = "sizeParams";
	public static final String INTERVAL_DISTRIBUTE = "intervalDistribute";
	public static final String INTERVAL_PARAMS = "intervalParams";

	private Distribute msgDistribute;
	private Distribute intervalDistribute;
	private boolean isMsgConstant;
	private boolean isIntervalPeriodic ;
	private int responseSize;
	private String responseMsgName;



	public ICNMessageEventGenerator(Settings s) {
		super(s);
	//	this.type = s.getInt(MESSAGE_TYPE_S);
		this.responseSize = s.getInt(MESSAGE_RESPONSE_SIZE_S,0);
		this.responseMsgName = s.getSetting(MESSAGE_RESPONSE_MSGNAME_S);

		try{
			String distribution = s.getSetting(MESSAGE_SIZE_DISTRIBUTE);
			double[] msgParams = s.getCsvDoubles(DISTRIBUTE_PARAM_S);
			this.intervalDistribute = DistributeFactory.getInstance(distribution,msgParams);
			this.isMsgConstant = false;
		}catch(Exception e){
			this.isMsgConstant = true;
		}
		try{
			String distribution = s.getSetting(INTERVAL_DISTRIBUTE);
			double[] intParams = s.getCsvDoubles(INTERVAL_PARAMS);
			this.msgDistribute = DistributeFactory.getInstance(distribution,intParams);
			this.isIntervalPeriodic = false;
		}catch(Exception e){
			this.isIntervalPeriodic = true;
		}


	}
//	protected  int drawMessageType(){
//		return type;
//	}

	/**
	 * Returns the next message creation event
	 * @see EventQueue#nextEvent()
	 */
	public ExternalEvent nextEvent() {

		int responseSize ; /* zero stands for one way messages */
		int msgSize;
		int interval;
		int from;
		String responseMsgName;


		/* Get two *different* nodes randomly from the host ranges */
		from = drawHostAddress(this.hostRange);


		msgSize = drawMessageSize();
		interval = drawNextEventTimeDiff();
		responseSize = drawResponseMessageSize();
		//请求的数据的name,应符合zipf分布
		ParetoRNG paretoRNG = new ParetoRNG(rng,0.5,1,hostRange[1]-hostRange[0]);

		responseMsgName =this.responseMsgName+(int)Math.floor(paretoRNG.getDouble());
		/* Create event and advance to next event */
		MessageCreateEvent mce = new ICNMessageCreateEvent(from, 0, this.getID(),
				msgSize, responseSize, this.nextEventsTime,responseMsgName);
		this.nextEventsTime += interval;

		if (this.msgTime != null && this.nextEventsTime > this.msgTime[1]) {
			/* next event would be later than the end time */
			this.nextEventsTime = Double.MAX_VALUE;
		}
		return mce;
	}

	protected int drawResponseMessageSize() {
		if (isMsgConstant){
			return this.responseSize;
		}else{
			return (int) this.msgDistribute.getDouble();
		}
	}

	@Override
	protected int drawNextEventTimeDiff() {
		if (isIntervalPeriodic){
			return super.drawNextEventTimeDiff();
		}
		return (int)this.intervalDistribute.getDouble();
	}

}