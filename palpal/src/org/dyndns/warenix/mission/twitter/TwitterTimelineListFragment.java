package org.dyndns.warenix.mission.twitter;

import org.dyndns.warenix.lab.compat1.R;
import org.dyndns.warenix.mission.facebook.FacebookHomeAdapter;
import org.dyndns.warenix.mission.timeline.StreamAdapter;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
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
public class TwitterTimelineListFragment extends ListFragment {
	int num;

	ListViewAdapter adapter;
	boolean isRefreshing = false;

	ListView listView;

	public static TwitterTimelineListFragment newInstance(int num) {
		TwitterTimelineListFragment f = new TwitterTimelineListFragment();

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
			adapter = new FacebookHomeAdapter(getActivity(), listView);
		} else if (num == 1) {
			adapter = new TwitterMentionsAdapter(getActivity(), listView);
		} else if (num == 2) {
			adapter = new StreamAdapter(getActivity(), listView);
		} else if (num == 3) {
			adapter = new TwitterHomeAdapter(getActivity(), listView);
		}
		setListAdapter(adapter);
		// }
		refresh();
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

	public void refresh() {
		if (!isRefreshing) {
			isRefreshing = true;
			adapter.clear();

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
