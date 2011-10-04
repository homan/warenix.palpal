package org.dyndns.warenix.palpal.bubbleMessage;

import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.service.UpdateMessageService;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.dyndns.warenix.pattern.baseListView.ListViewController;
import org.dyndns.warenix.util.ToastUtil;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class BubbleMessageListController extends ListViewController implements
		OnScrollListener {

	int currentScrollState;
	int currentPageNo = 0;
	private int lastSavedFirst = -1;

	public BubbleMessageListController(Activity context, int resourceId) {
		super(context, resourceId);
	}

	@Override
	public ListViewAdapter setupListViewAdapter(Context context) {
		((ListView) listView).setDividerHeight(0);

		listView.setOnScrollListener(this);

		BubbleMessageListAdapter adapter = new BubbleMessageListAdapter(context);
		return adapter;
	}

	public void refresh(Context context) {
		Log.d("warenix", "refresh message list");
		((BubbleMessageListAdapter) listAdapter).refresh();
		currentPageNo = 0;
		// ((BubbleMessageListAdapter) listAdapter)
		// .loadMessageByPage(nextPageNo++);

		int unreadCount = PalPal.unreadMessageCount;
		if (unreadCount > 0) {
			// scroll to first unread
			scrollTo(unreadCount - 1);
			ToastUtil.clearNotification(context,
					UpdateMessageService.NOTIFICATION_UNREAD_ID);
		}
	}

	public void scrollTo(int position) {
		listView.setSelection(position);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (visibleItemCount < totalItemCount
				&& (firstVisibleItem + visibleItemCount == totalItemCount)) {
			// only process first event
			if (firstVisibleItem != lastSavedFirst) {
				lastSavedFirst = firstVisibleItem;
				Log.d("palpal", "fvi: " + firstVisibleItem + ", vic: "
						+ visibleItemCount + ", tic: " + totalItemCount);
				((BubbleMessageListAdapter) listAdapter)
						.loadMessageByPage(++currentPageNo);
				scrollTo(firstVisibleItem);
			}
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		currentScrollState = scrollState;
	}

}
