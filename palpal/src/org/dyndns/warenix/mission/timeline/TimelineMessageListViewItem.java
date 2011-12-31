package org.dyndns.warenix.mission.timeline;

import java.util.Date;

import org.dyndns.warenix.pattern.baseListView.ListViewItem;

/**
 * Centalized message item view
 * 
 * @author warenix
 * 
 */
public abstract class TimelineMessageListViewItem extends ListViewItem
		implements Comparable<TimelineMessageListViewItem> {

	protected int messageType;

	public TimelineMessageListViewItem() {
		messageType = setMessageType();
	}

	public int compareTo(TimelineMessageListViewItem another) {
		// sort in descending order
		return getDate().before(another.getDate()) ? 1 : -1;
	}

	public abstract Date getDate();

	public abstract int setMessageType();

}
