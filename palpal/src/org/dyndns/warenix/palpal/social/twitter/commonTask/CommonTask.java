package org.dyndns.warenix.palpal.social.twitter.commonTask;

import java.io.Serializable;

import android.content.Context;

public abstract class CommonTask implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2941980748415367427L;

	public CommonTask() {

	}

	public abstract Object execute(Context context);

}