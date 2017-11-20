/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package input;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
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
 */
public class ICNMessageEventGenerator extends MessageEventGenerator {
	/** Message type range -setting id ({@value}). Can be either a interest
	 * package or a data package,which is defined by 0 and 1.
	 *.interest package :0, data package:1.
	 * Defines the message type . */
	public static final String MESSAGE_RESPONSE_SIZE_S = "responseSize";
	public static final String MESSAGE_RESPONSE_MSGNAME_S = "responseMsgName";
	/**
	 * distribute的类型有：Exponential， GenelizedExtreme，LogNormal，Normal，Pareto，
	 *Weibull
	 */
	public static final String MESSAGE_SIZE_DISTRIBUTE = "sizeDistribute";
	public static final String DISTRIBUTE_PARAM_S = "sizeParams";
	public static final String UNIT_LOCATOR = "unitLocator";
	public static final String INTERVAL_DISTRIBUTE = "intervalDistribute";
	public static final String INTERVAL_PARAMS = "intervalParams";
	/*消息种类*/
	public static final String TYPE_SIZE = "typeSize";
	public static final String ISMSGCONSTANT ="isMsgConstant";
	public static final String ISINTERVALPERIODIC ="isIntervalPeriodic";

	private Distribute msgDistribute;
	private Distribute intervalDistribute;
	private boolean isMsgConstant;
	private boolean isIntervalPeriodic ;
	private int responseSize;
	private String responseMsgName;
	private int typeSize;
	private int locator;



	public ICNMessageEventGenerator(Settings s) {
		super(s);
	//	this.type = s.getInt(MESSAGE_TYPE_S);
		this.responseSize = s.getInt(MESSAGE_RESPONSE_SIZE_S,0);
		this.responseMsgName = s.getSetting(MESSAGE_RESPONSE_MSGNAME_S);
		this.typeSize = s.getInt(TYPE_SIZE, 100);
		this.isMsgConstant = s.getBoolean(ISMSGCONSTANT, true);
		this.isIntervalPeriodic = s.getBoolean(ISINTERVALPERIODIC, true);
		this.locator =s.getInt(UNIT_LOCATOR, 1);
		if (!isMsgConstant){
			String distribution = s.getSetting(MESSAGE_SIZE_DISTRIBUTE);
			double[] msgParams = s.getCsvDoubles(DISTRIBUTE_PARAM_S);
			this.msgDistribute = DistributeFactory.getInstance(distribution,msgParams);
		}
		if (!isIntervalPeriodic){
			String distribution = s.getSetting(INTERVAL_DISTRIBUTE);
			double[] intParams = s.getCsvDoubles(INTERVAL_PARAMS);
			this.intervalDistribute = DistributeFactory.getInstance(distribution,intParams);
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

		//在哪个节点产生，随机
		from = drawHostAddress(this.hostRange);


		msgSize = drawMessageSize();
		interval = drawNextEventTimeDiff();
		responseSize = locator*drawResponseMessageSize();
		//请求的数据的种类,应符合zipf分布
		ParetoRNG paretoRNG = new ParetoRNG(rng,0.5,1,typeSize);

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