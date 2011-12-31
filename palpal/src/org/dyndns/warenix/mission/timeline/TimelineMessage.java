package org.dyndns.warenix.mission.timeline;

import java.util.Date;

public interface TimelineMessage extends Comparable<TimelineMessage> {
	public Date getDate();
}
