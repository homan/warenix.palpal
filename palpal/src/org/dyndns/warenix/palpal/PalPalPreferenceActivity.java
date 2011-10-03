package org.dyndns.warenix.palpal;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class PalPalPreferenceActivity extends PreferenceActivity {

	public void onStart() {
		super.onStart();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);

		initPrefs();
	}

	void initPrefs() {
		createPreferenceForkey(
				getResources().getString(
						R.string.KEY_FACEBOOK_NUMBER_OF_WALL_POSTS), "10");
		createPreferenceForkey(
				getResources()
						.getString(R.string.KEY_FACEBOOK_NUMBER_OF_ALBUMS),
				"10");
		createPreferenceForkey(
				getResources().getString(
						R.string.KEY_FACEBOOK_NUMBER_OF_ALBUM_PHOTOS), "5");
		createPreferenceForkey(
				getResources().getString(
						R.string.KEY_FACEBOOK_NUMBER_OF_COMMENTS), "5");
		createPreferenceForkey(
				getResources().getString(
						R.string.KEY_FACEBOOK_NUMBER_OF_RECENT_CHECKINS), "5");
		createPreferenceForkey(
				getResources().getString(R.string.KEY_TWITTER_NUMBER_OF_TWEETS),
				"20");

	}

	void createPreferenceForkey(final String key, final String defaultValue) {
		EditTextPreference urlPref = (EditTextPreference) findPreference(key);

		// load preference
		String storedValue = PalPalPreference.loadPreferenceValue(this, key,
				defaultValue);
		if (storedValue == null) {
			PalPalPreference.savePreferenceValue(PalPalPreferenceActivity.this,
					key, (String) storedValue);
		}
		urlPref.setText(storedValue);

		urlPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				PalPalPreference.savePreferenceValue(
						PalPalPreferenceActivity.this, key, (String) newValue);
				return true;
			}

		});
	}
}
