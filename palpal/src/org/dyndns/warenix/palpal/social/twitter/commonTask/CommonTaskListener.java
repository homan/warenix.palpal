package org.dyndns.warenix.palpal.social.twitter.commonTask;

import java.io.Serializable;

import org.dyndns.warenix.palpal.bubbleMessage.BubbleMessageListController;

public class CommonTaskListener implements Serializable {
	BubbleMessageListController controller;

	public CommonTaskListener(BubbleMessageListController controller) {
		this.controller = controller;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4541375015081981041L;

}
