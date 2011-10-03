package org.dyndns.warenix.palpal.animation;

import org.dyndns.warenix.palpal.R;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class AnimationEffect {
	public static void playFetchPageAnimation(Context context, View view,
			boolean isStart) {
		if (isStart) {
			Animation fetchPageAnimation = AnimationUtils.loadAnimation(
					context, R.anim.jump_anim);
			view.setVisibility(View.VISIBLE);
			view.startAnimation(fetchPageAnimation);
		} else {
			Animation fetchPageAnimation = AnimationUtils.loadAnimation(
					context, R.anim.slide_right_out);
			view.startAnimation(fetchPageAnimation);
			view.setVisibility(View.GONE);
		}
	}
}
