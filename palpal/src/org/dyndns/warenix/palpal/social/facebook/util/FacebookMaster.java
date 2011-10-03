package org.dyndns.warenix.palpal.social.facebook.util;

import java.util.ArrayList;

import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.vo.Checkin;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.Profile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * facebook master to provide common operations. it's a layer above facebook
 * api.
 * 
 * @author warenix
 * 
 */
public class FacebookMaster {

	public static class profile {

		/**
		 * get profile of a user or group
		 * 
		 * @param username
		 *            user id or username or group id
		 * @return
		 * @throws FacebookException
		 */
		public static Profile getProfile(String username)
				throws FacebookException {
			String responseString = FacebookAPI.getProfile(username);

			try {
				JSONObject json = new JSONObject(responseString);
				Profile profile = new Profile(json);

				return profile;

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public static class User {
		public static ArrayList<Checkin> getFriendsRecentCheckins(int limit) {
			Profile authenticatedUserProfile = PalPal
					.getAuthenticatedUserProfile();
			try {
				String response = FacebookAPI.getUserFriendsCheckinsByFQL(
						authenticatedUserProfile.id, limit);
				Log.i("palpal", response);

				// parse response

				ArrayList<Checkin> checkinList = new ArrayList<Checkin>();
				JSONArray checkinArray = new JSONArray(response);
				for (int i = 0; i < checkinArray.length(); ++i) {
					checkinList.add(new Checkin(checkinArray.getString(i)));
				}

				return checkinList;

			} catch (FacebookException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}
