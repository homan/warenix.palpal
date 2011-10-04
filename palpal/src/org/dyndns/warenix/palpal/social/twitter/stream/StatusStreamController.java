package org.dyndns.warenix.palpal.social.twitter.stream;

import java.io.IOException;
import java.util.ArrayList;

import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.bubbleMessage.BubbleMessage;
import org.dyndns.warenix.palpal.social.twitter.TwitterBubbleMessage;
import org.dyndns.warenix.palpaltwitter.R;
import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.dyndns.warenix.pattern.baseListView.ListViewController;

import twitter4j.ConnectionLifeCycleListener;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.UserStreamListener;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ListView;

public class StatusStreamController extends ListViewController {

	public StatusStreamController(Activity context, int resourceId) {
		super(context, resourceId);

		// String[] accessToken = PalPalPreference
		// .loadAccessTokenPreference(context);
		//
		// try {
		// loginTwitter(accessToken[0], accessToken[1]);
		// } catch (TwitterException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	@Override
	public ListViewAdapter setupListViewAdapter(Context context) {
		Log.d("palpal", "setupListViewAdapter");
		((ListView) listView).setDividerHeight(0);
		listAdapter = new StatusStreamListAdapter(context);
		return listAdapter;
	}

	ArrayList<BubbleMessage> bufferedTimeline = new ArrayList<BubbleMessage>();

	AccessToken accessToken;
	Twitter twitter;
	TwitterStream twitterStream;
	int streamingMode;

	void loginTwitter(String tokenKey, String tokenSecret)
			throws TwitterException, IOException {

		twitter = new TwitterFactory().getInstance();
		// twitter.setOAuthConsumer(PalPal.JTWITTER_OAUTH_KEY,
		// PalPal.JTWITTER_OAUTH_SECRET);

		accessToken = new AccessToken(tokenKey, tokenSecret);
		twitter.setOAuthAccessToken(accessToken);
		Log.d("palpal", "logged in as " + twitter.getScreenName());

		return;
	}

	void initTwitterStream(ConnectionLifeCycleListener listener)
			throws IllegalStateException, TwitterException, IOException {

		twitterStream = new TwitterStreamFactory().getInstance();
		twitterStream.setOAuthConsumer(PalPal.JTWITTER_OAUTH_KEY,
				PalPal.JTWITTER_OAUTH_SECRET);
		twitterStream.setOAuthAccessToken(PalPal.getTwitterClient()
				.getOAuthAccessToken());

		twitterStream.addConnectionLifeCycleListener(listener);
	}

	public void startStreaming(int checkedId,
			ConnectionLifeCycleListener listener,
			UserStreamListener userListener) {
		stopStreaming();

		//
		streamingMode = checkedId;

		try {
			initTwitterStream(listener);

			if (streamingMode == R.id.radio0) {
				Log.d("palpal", "start streaming user");
				twitterStream.addListener(userListener);
				twitterStream.user();
			} else if (streamingMode == R.id.radio1) {
				Log.d("palpal", "start streaming filter");
				twitterStream.addListener(userListener);

				long[] followArray = TwitterStreamMaster
						.getUserFriends(twitter);
				Log.d("palpal", "" + followArray.length);
				TwitterStreamMaster.queryStream(twitterStream, null,
						followArray, null);
			} else if (streamingMode == R.id.radio2) {
				Log.d("palpal", "start streaming sample");
				twitterStream.addListener(userListener);
				twitterStream.sample();
			} else if (streamingMode == R.id.radio3) {
				Log.d("palpal", "start streaming hk");
				twitterStream.addListener(userListener);
				double[][] locationArray = { { 113.845825, 22.202324 },
						{ 114.411621, 22.545199 } };
				TwitterStreamMaster.queryStream(twitterStream, null, null,
						locationArray);
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (TwitterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void startKeywordStreaming(String keyword,
			ConnectionLifeCycleListener listener,
			UserStreamListener userListener) {
		stopStreaming();

		//
		try {
			initTwitterStream(listener);
			twitterStream.addListener(userListener);

			TwitterStreamMaster.queryStream(twitterStream,
					new String[] { keyword }, null, null);

			// if (streamingMode == R.id.radio0) {
			// Log.d("palpal", "start streaming user");
			// twitterStream.addListener(userListener);
			// twitterStream.user();
			// } else if (streamingMode == R.id.radio1) {
			// Log.d("palpal", "start streaming filter");
			// twitterStream.addListener(userListener);
			//
			// long[] followArray = TwitterStreamMaster
			// .getUserFriends(twitter);
			// Log.d("palpal", "" + followArray.length);
			// TwitterStreamMaster.queryStream(twitterStream, null,
			// followArray, null);
			// } else if (streamingMode == R.id.radio2) {
			// Log.d("palpal", "start streaming sample");
			// twitterStream.addListener(userListener);
			// twitterStream.sample();
			// } else if (streamingMode == R.id.radio3) {
			// Log.d("palpal", "start streaming hk");
			// twitterStream.addListener(userListener);
			// double[][] locationArray = { { 113.845825, 22.202324 },
			// { 114.411621, 22.545199 } };
			// TwitterStreamMaster.queryStream(twitterStream, null, null,
			// locationArray);
			// }
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (TwitterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void startNearStreaming(String keyword,
			ConnectionLifeCycleListener listener,
			UserStreamListener userListener) {
		stopStreaming();

		//
		try {
			initTwitterStream(listener);
			twitterStream.addListener(userListener);

			// if (streamingMode == R.id.radio0) {
			// Log.d("palpal", "start streaming user");
			// twitterStream.addListener(userListener);
			// twitterStream.user();
			// } else if (streamingMode == R.id.radio1) {
			// Log.d("palpal", "start streaming filter");
			// twitterStream.addListener(userListener);
			//
			// long[] followArray = TwitterStreamMaster
			// .getUserFriends(twitter);
			// Log.d("palpal", "" + followArray.length);
			// TwitterStreamMaster.queryStream(twitterStream, null,
			// followArray, null);
			// } else if (streamingMode == R.id.radio2) {
			// Log.d("palpal", "start streaming sample");
			// twitterStream.addListener(userListener);
			// twitterStream.sample();
			// } else if (streamingMode == R.id.radio3) {
			// Log.d("palpal", "start streaming hk");
			double[][] locationArray = { { 113.845825, 22.202324 },
					{ 114.411621, 22.545199 } };
			TwitterStreamMaster.queryStream(twitterStream, null, null,
					locationArray);
			// }
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (TwitterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void stopStreaming() {
		if (twitterStream != null) {
			twitterStream.cleanUp();
			twitterStream.shutdown();
			twitterStream = null;
		}
	}

	public void addStatusToBuffer(Status tweet) {
		TwitterBubbleMessage message = new TwitterBubbleMessage(tweet.getUser()
				.getScreenName(), tweet.getText(), tweet.getUser()
				.getProfileImageURL().toString(), new java.sql.Date(tweet
				.getCreatedAt().getTime()), "twitter", tweet.getId() + "");

		bufferedTimeline.add(message);
	}

	public void showBufferredStatus() {
		((StatusStreamListAdapter) listAdapter).addAllStatus(bufferedTimeline);
		bufferedTimeline.clear();
	}

	public void clearStatus() {
		((StatusStreamListAdapter) listAdapter).clearStatus();
	}

	public int getBufferedStatusSize() {
		return bufferedTimeline.size();
	}
}