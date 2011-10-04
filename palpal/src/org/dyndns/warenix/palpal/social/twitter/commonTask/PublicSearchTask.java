package org.dyndns.warenix.palpal.social.twitter.commonTask;

import android.content.Context;

public class PublicSearchTask extends CommonTask {

	protected CommonTaskListener listener;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3336771404878694480L;
	public String keyword;

	public PublicSearchTask(String keyword) {
		this.keyword = keyword;
	}

	@Override
	public Object execute(Context context) {

		return null;
	}
}
