package org.dyndns.warenix.palpal.social.facebook.activity;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Set;

import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookMaster;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.Profile;
import org.dyndns.warenix.util.ToastUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.google.android.maps.MapActivity;

/**
 * Abstract activity that require facebook session
 * 
 * @author s0488
 * 
 */
public abstract class PalPalFacebookActivity extends MapActivity {

	/**
	 * the facebook session
	 */
	Facebook facebook;

	/**
	 * shared pool of images for this activity context
	 */
	static HashMap<String, SoftReference<Bitmap>> imagePool;

	/**
	 * passed extras
	 */
	Bundle extras;

	/**
	 * current page number, one-based
	 */
	protected Integer currentPageNumber;

	protected boolean hasNextPage;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getPassedExtras();
		setupUI();
		checkFacebookSession();
	}

	protected void onDestroy() {
		clearImagePool();
		super.onDestroy();
	}

	static void clearImagePool() {
		if (imagePool != null) {
			Set<String> keySet = imagePool.keySet();
			for (String key : keySet) {
				SoftReference<Bitmap> ref = imagePool.get(key);
				if (ref != null) {
					Bitmap bm = ref.get();
					if (bm != null) {
						Log.d("warenix", "recycle bm " + key);
						bm.recycle();
					}
				}
			}
			imagePool.clear();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		facebook.authorizeCallback(requestCode, resultCode, data);

	}

	final String PREFS_NAME = "facebook";

	void storeSession(Facebook facebook) {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("access_token", facebook.getAccessToken());
		editor.putLong("access_expire", facebook.getAccessExpires());
		editor.commit();
	}

	boolean restoreSession(Facebook facebook) {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		String token = settings.getString("access_token", null);
		long expire = settings.getLong("access_expire", 0);
		facebook.setAccessToken(token);
		facebook.setAccessExpires(expire);

		return facebook.isSessionValid();
	}

	void clearSession() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();
	}

	void checkFacebookSession() {
		facebook = PalPal.getFacebook();
		if (facebook == null) {
			Log.v("palpal",
					"first time opened, try to restore facebook session");
			facebook = new Facebook(PalPal.APP_ID);
		}

		if (restoreSession(facebook) == false) {
			facebook.authorize(this, PalPal.permissions,
					new AuthorizeListener());
		} else {
			onFacebookReady();
		}
	}

	void onFacebookReady() {
		PalPal.setFacebook(facebook);

		imagePool = new HashMap<String, SoftReference<Bitmap>>();

		Profile authenticatedUserProfile;
		try {
			authenticatedUserProfile = FacebookMaster.profile.getProfile("me");
			PalPal.setAuthenticatedUserProfile(authenticatedUserProfile);

			onFacebookReady(facebook, imagePool);
		} catch (FacebookException e) {
			ToastUtil.showNotification(this, e.type, e.type, e.error, null,
					1000);
		}

	}

	/**
	 * subclass should override this method to load a given page
	 * 
	 * @param pageNumber
	 */
	void loadPage(Integer pageNumber) {

	}

	// // paging
	// Integer getOffSet(Integer pageNumber, Integer entryPerPage) {
	// return currentPageNumber * entryPerPage;
	// }

	// boolean loadNextPage() {
	// if (!hasNextPage)
	// return false;
	//
	// currentPageNumber++;
	// runOnUiThread(new Runnable() {
	// public void run() {
	// ToastUtil.showQuickToast(PalPalFacebookActivity.this,
	// "loading next page " + (currentPageNumber + 1));
	// }
	// });
	//
	// loadPage(currentPageNumber);
	// return true;
	// }
	//
	// boolean loadPreviousPage() {
	// if (currentPageNumber == null) {
	// return false;
	// }
	//
	// if (!hasPreviousPage || currentPageNumber == 0) {
	// runOnUiThread(new Runnable() {
	// public void run() {
	// ToastUtil.showQuickToast(PalPalFacebookActivity.this,
	// "no previous page");
	// }
	// });
	// return false;
	// }
	//
	// currentPageNumber--;
	// runOnUiThread(new Runnable() {
	// public void run() {
	// ToastUtil.showQuickToast(PalPalFacebookActivity.this,
	// "loading previous page " + (currentPageNumber + 1));
	// }
	// });
	//
	// loadPage(currentPageNumber);
	// return true;
	//
	// }

	/**
	 * when facebook session is ready
	 * 
	 * @param facebook
	 * @param imagePool
	 */
	abstract void onFacebookReady(final Facebook facebook,
			final HashMap<String, SoftReference<Bitmap>> imagePool);

	/**
	 * setup ui
	 */
	abstract void setupUI();

	void getPassedExtras() {
		extras = this.getIntent().getExtras();
	}

	class AuthorizeListener implements Facebook.DialogListener {

		@Override
		public void onComplete(Bundle values) {
			Log.d("palpal", "authorize completed");

			storeSession(facebook);
			onFacebookReady();
		}

		@Override
		public void onFacebookError(FacebookError e) {
			Log.d("palpal", "authorize facebook error");
			ToastUtil.showNotification(PalPalFacebookActivity.this,
					"facebook error", "facebook error", e.getMessage(), null,
					1000);
			e.printStackTrace();
		}

		@Override
		public void onError(DialogError e) {
			Log.d("palpal", "authorize error");
			e.printStackTrace();

			ToastUtil.showNotification(PalPalFacebookActivity.this,
					"facebook cannnot authorize you",
					"facebook cannnot authorize you", e.getMessage(), null,
					1000);

			clearSession();
		}

		@Override
		public void onCancel() {
			Log.d("palpal", "authorize cancelled");
		}

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
