package org.dyndns.warenix.mission.timeline;

import java.util.ArrayList;

import org.dyndns.warenix.lab.compat1.R;
import org.dyndns.warenix.mission.facebook.FacebookHomeAdapter;
import org.dyndns.warenix.mission.sample.SampleListAdapter;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.dyndns.warenix.pattern.baseListView.ListViewItem;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;

/**
 * Display items in a vertical timeline view
 * 
 * @author warenix
 * 
 */
public class TimelineListFragment extends ListFragment {
	public static String ITEM_LIST = "item_list";
	int num;

	ListViewAdapter adapter;
	boolean isRefreshing = false;

	ListView listView;

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
		} else if (num == 1) {
			adapter = new NotificationsAdapter(getActivity(), listView);
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
		super.onDestroyView();
		Log.d("warenix", "timelineListFragment onDestroyView()");
		if (adapter != null) {
			adapter.clear();
			adapter = null;
			listView = null;
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d("warenix", "timelineListFragment onDetach()");
	}

	public void onSaveInstanceState(Bundle outState) {
		Log.d("warenix", "timelineListFragment onSaveInstanceState()");

		ArrayList<ListViewItem> itemListCopy = adapter.getItemList();
		outState.putSerializable(ITEM_LIST, itemListCopy);
		super.onSaveInstanceState(outState);
	}

	public void refresh() {
		if (!isRefreshing) {
			// isRefreshing = true;
			adapter.asyncRefresh();
			// adapter.refresh();
			// new RefreshTimelineAsyncTask().execute();
			// adapter.asyncRefresh();
		}
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
		ArrayList<ListViewItem> itemListCopy = null;

		if (savedInstanceState != null) {
			itemListCopy = (ArrayList<ListViewItem>) savedInstanceState
					.getSerializable(ITEM_LIST);
		}

		if (itemListCopy != null && itemListCopy.size() > 0) {
			adapter.setItemList(itemListCopy);
		} else {
			refresh();
		}

	}

	class RefreshTimelineAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			adapter.asyncRefresh();
			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			// adapter.notifyDataSetChanged();

			// listView.setSelectionAfterHeaderView();
			// /*
			// * Setting up Animation
			// */
			// AnimationSet set = new AnimationSet(true);
			//
			// Animation animation = new AlphaAnimation(0.0f, 1.0f);
			// animation.setDuration(500);
			// set.addAnimation(animation);
			//
			// animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
			// 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
			// Animation.RELATIVE_TO_SELF, 1.0f,
			// Animation.RELATIVE_TO_SELF, 0.0f);
			// animation.setDuration(500);
			// set.addAnimation(animation);
			//
			// LayoutAnimationController controller = new
			// LayoutAnimationController(
			// set, 0.25f);
			// listView.setLayoutAnimation(controller);
			isRefreshing = false;
		}

	}

}
