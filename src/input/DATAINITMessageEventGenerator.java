/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package input;

import core.Settings;
import distribution.Distribute;
import distribution.DistributeFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Message creation -external events generator. Creates one message from
 * every source node (defined with {@link MessageEventGenerator#HOST_RANGE_S})
 * to one of the destination nodes (defined with
 * {@link MessageEventGenerator#TO_HOST_RANGE_S}). 
 * The message size, first messages time and the intervals between creating 
 * messages can be configured like with {@link MessageEventGenerator}. End
 * time is not respected, but messages are created until every from-node has
 * created a message.
 * @see MessageEventGenerator
 */
//用来初始化数据
public class DATAINITMessageEventGenerator extends MessageEventGenerator {

	private List<Integer> fromIds;


	public DATAINITMessageEventGenerator(Settings s) {
		super(s);


		this.fromIds = new ArrayList<Integer>();


		for (int i = hostRange[0]; i < hostRange[1]; i++) {
			fromIds.add(i);
		}
		Collections.shuffle(fromIds, rng);
	}

	/**
	 * Returns the next message creation event
	 * @see EventQueue#nextEvent()
	 */
	public ExternalEvent nextEvent() {
		int responseSize = 0; /* no responses requested */
		int from;
		//int to;
		int msgSize;
		int interval;
		from = this.fromIds.remove(0);	
		//to = drawToAddress(toHostRange, -1);
		msgSize = drawMessageSize();
		interval = drawNextEventTimeDiff();

		MessageCreateEvent mce = new DATAINITMessageCreateEvent(from, 0, getID(),
				msgSize, responseSize, this.nextEventsTime);

		if (this.fromIds.size() == 0) {
			this.nextEventsTime = Double.MAX_VALUE; /* no messages left */
		} else {
			this.nextEventsTime += interval;
		}
		if (this.msgTime != null && this.nextEventsTime > this.msgTime[1]) {
			/* next event would be later than the end time */
			this.nextEventsTime = Double.MAX_VALUE;
		}
		return mce;
	}




}