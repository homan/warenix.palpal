package org.dyndns.warenix.palpal.twitter.directMessage;

import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.dyndns.warenix.pattern.baseListView.ListViewController;

import android.app.Activity;
import android.content.Context;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;

public class DirectMessageListController extends ListViewController {

	public DirectMessageListController(Activity context, int resourceId) {
		super(context, resourceId);
	}

	@Override
	public ListViewAdapter setupListViewAdapter(Context context) {
		((ListView) listView).setDividerHeight(0);
		// LayoutAnimationController animationController = AnimationUtils
		// .loadLayoutAnimation(context, R.anim.pump_bottom);
		// listView.setLayoutAnimation(animationController);

		DirectMessageListAdapter adapter = new DirectMessageListAdapter(context);
		return adapter;
	}

	public void stop() {
	}

	public void refresh(long statusId) {
		((DirectMessageListAdapter) listAdapter).refresh();
	}

	public void scrollTo(int position) {
		listView.setSelection(position);
	}

	public void playAnimation() {
		/* Setting up Animation */
		AnimationSet set = new AnimationSet(true);

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(400);
		set.addAnimation(animation);

		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				-1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(400);
		set.addAnimation(animation);

		LayoutAnimationController controller = new LayoutAnimationController(
				set, 0.25f);
		listView.setLayoutAnimation(controller);
		/* Animation code ends */
	}
}
