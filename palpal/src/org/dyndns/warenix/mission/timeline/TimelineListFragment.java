package org.dyndns.warenix.mission.timeline;

import java.io.Serializable;

import org.dyndns.warenix.lab.compat1.R;
import org.dyndns.warenix.mission.facebook.FacebookHomeAdapter;
import org.dyndns.warenix.mission.sample.SampleListAdapter;
import org.dyndns.warenix.pattern.baseListView.AsyncListAdapter.AsyncRefreshListener;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.dyndns.warenix.util.WLog;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Display items in a vertical timeline view
 * 
 * @author warenix
 * 
 */
public class TimelineListFragment extends ListFragment implements
		AsyncRefreshListener {
	private static final String TAG = "TimelineListFragment";
	boolean isRefreshing = false;
	public static String ITEM_LIST = "item_list";
	int num;

	ListViewAdapter adapter;

	ListView listView;
	View mProgressBar;
	TextView mProgressText;

	public static TimelineListFragment newInstance(int num) {
		TimelineListFragment f = new TimelineListFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);
		f.num = num;

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("warenix", "onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("warenix", "onCreateView");
		View v = inflater.inflate(R.layout.message_timeline, container, false);
		mProgressBar = v.findViewById(R.id.progress_bar);
		mProgressText = (TextView) v.findViewById(R.id.progress_text);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("warenix", "onActivityCreated");
		// hide keyboard until user click textfield
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		listView = getListView();
		// adapter = new TimelineListAdapter(listView, new
		// StreamDataProvider());
		// if (adapter == null) {
		if (num == 0) {
			adapter = new StreamAdapter(getActivity(), listView);
			((StreamAdapter) adapter).setAsyncRefreshListener(this);
		} else if (num == 1) {
			adapter = new NotificationsAdapter(getActivity(), listView);
			((NotificationsAdapter) adapter).setAsyncRefreshListener(this);
		} else if (num == 2) {
			adapter = new SampleListAdapter(getActivity(), listView);
		} else if (num == 3) {
			adapter = new FacebookHomeAdapter(getActivity(), listView);
		}
		setListAdapter(adapter);
		// }

		restoreOrRefreshItemList(savedInstanceState);

	}

	public void onDestroyView() {
		Log.d("warenix", "timelineListFragment onDestroyView()");
		if (adapter != null) {
			adapter.clear();
			adapter = null;
			listView = null;
		}
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		Log.d("warenix", "timelineListFragment onDetach()");
		// adapter.cancelAsyncRefresh();
		super.onDetach();
	}

	public void onSaveInstanceState(Bundle outState) {
		Log.d("warenix", "timelineListFragment onSaveInstanceState()");

		Serializable itemListCopy = adapter.getItemList();
		outState.putSerializable(ITEM_LIST, itemListCopy);
		super.onSaveInstanceState(outState);
	}

	public void refresh() {
		if (!isRefreshing) {
			mProgressBar.setVisibility(View.VISIBLE);
			mProgressText.setVisibility(View.VISIBLE);
			mProgressText.setText("We're preparing to load messages.");

			adapter.asyncRefresh();
		}
		// adapter.refresh();
		// new RefreshTimelineAsyncTask().execute();
		// adapter.asyncRefresh();
		// if (adapter == null) {
		// adapter = new TimelineListAdapter(getListView());
		// setListAdapter(adapter);
		// }
		//
		// adapter.notifyDataSetChanged();
		// getListView().setSelectionAfterHeaderView();/* Setting up Animation
		// */
		// AnimationSet set = new AnimationSet(true);
		//
		// Animation animation = new AlphaAnimation(0.0f, 1.0f);
		// animation.setDuration(500);
		// set.addAnimation(animation);
		//
		// animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
		// Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
		// 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		// animation.setDuration(500);
		// set.addAnimation(animation);
		//
		// LayoutAnimationController controller = new LayoutAnimationController(
		// set, 0.25f);
		// getListView().setLayoutAnimation(controller);

	}

	/**
	 * try to restore saved item list if the list contains items
	 * 
	 * @param savedInstanceState
	 */
	private void restoreOrRefreshItemList(Bundle savedInstanceState) {
		Serializable itemListCopy = null;

		if (savedInstanceState != null) {
			itemListCopy = (Serializable) savedInstanceState
					.getSerializable(ITEM_LIST);
		}

		if (itemListCopy != null) {
			adapter.setItemList(itemListCopy);
		} else {
			refresh();
		}

	}

	@Override
	public void onAysncRefreshStarted() {
		WLog.i(TAG, "onAysncRefreshStarted");
		getView().post(new Runnable() {

			@Override
			public void run() {
				mProgressBar.setVisibility(View.VISIBLE);
				mProgressText.setVisibility(View.VISIBLE);
				mProgressText
						.setText("We're loading messages from social networks.");
			}
		});
	}

	@Override
	public void onAysncRefreshEnded() {
		WLog.i(TAG, "onAysncRefreshEnded");

		getView().post(new Runnable() {

			@Override
			public void run() {
				mProgressBar.setVisibility(View.GONE);
				if (adapter.getCount() > 0) {
					mProgressText.setVisibility(View.GONE);
				} else {
					mProgressText.setText("No message is found.");
				}
			}
		});

		isRefreshing = false;
	}

}
