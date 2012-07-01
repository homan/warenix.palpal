package org.dyndns.warenix.lab.compat1.app.timeline;

import org.dyndns.warenix.lab.compat1.app.timeline.TimelineFactory.TimelineConfig;
import org.dyndns.warenix.mission.timeline.StreamAdapter;
import org.dyndns.warenix.mission.timeline.TimelineListFragment;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;

import android.os.Bundle;

public class StreamTimeline extends TimelineListFragment {

	public static TimelineListFragment newInstance(TimelineConfig config) {
		StreamTimeline f = new StreamTimeline();
		Bundle args = f.prepareInitArgument(config);
		f.setArguments(args);
		return f;
	}

	public ListViewAdapter getAdapter() {
		return new StreamAdapter(getActivity(), listView);
	}

	protected Bundle prepareInitArgument(TimelineConfig config) {
		Bundle b = new Bundle();
		b.putParcelable("config", config);
		return b;
	}

}
