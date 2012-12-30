package org.dyndns.warenix.lab.compat1.util;

import org.dyndns.warenix.palpal.R;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class AndroidUtil {
	public static void hideSoftwareKeyboard(Activity activityContext) {
		activityContext.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	public static void playListAnimation(View root) {
		Animation a = AnimationUtils.loadAnimation(root.getContext(),
				R.anim.pull_up);
		a.reset();
		root.clearAnimation();
		root.startAnimation(a);
	}
}
