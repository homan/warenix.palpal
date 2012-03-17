package org.dyndns.warenix.mission.sample;

import org.dyndns.warenix.lab.compat1.R;
import org.dyndns.warenix.util.WLog;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;

/**
 * Display items in a vertical timeline view
 * 
 * @author warenix
 * 
 */
public class TimelineListFragment extends ListFragment {
	private static final String TAG = "TimelineListFragment";
	SampleListAdapter adapter;
	boolean isRefreshing = false;

	ListView listView;

	public static TimelineListFragment newInstance(int num) {
		TimelineListFragment f = new TimelineListFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putInt("num", num);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WLog.d(TAG, "onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		WLog.d(TAG, "onCreateView");
		View v = inflater.inflate(R.layout.message_timeline, container, false);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		WLog.d(TAG, "onActivityCreated");
		// hide keyboard until user click textfield
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		listView = getListView();
		// adapter = new TimelineListAdapter(listView, new
		// StreamDataProvider());
		if (adapter == null) {
			adapter = new SampleListAdapter(getActivity(), listView);
			setListAdapter(adapter);
		}
		refresh();
	}

	@Override
	public void onStop() {
		WLog.d(TAG, "timelineListFragment onStop()");
		if (adapter != null) {
			// adapter.cleanup();
		}
		super.onStop();
	}

	public void refresh() {
		if (!isRefreshing) {
			isRefreshing = true;
			// adapter.refresh();
			new RefreshTimelineAsyncTask().execute();
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
			/*
			 * Setting up Animation
			 */
			AnimationSet set = new AnimationSet(true);

			Animation animation = new AlphaAnimation(0.0f, 1.0f);
			animation.setDuration(500);
			set.addAnimation(animation);

			animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
					0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 1.0f,
					Animation.RELATIVE_TO_SELF, 0.0f);
			animation.setDuration(500);
			set.addAnimation(animation);

			LayoutAnimationController controller = new LayoutAnimationController(
					set, 0.25f);
			listView.setLayoutAnimation(controller);
			isRefreshing = false;
		}

	}

}
