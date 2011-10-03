package org.dyndns.warenix.palpal.social.twitter;

import java.util.ArrayList;

import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.social.twitter.task.LoadConversationAsyncTask;
import org.dyndns.warenix.palpal.social.twitter.task.LoadConversationAsyncTask.LoadConversationListener;
import org.dyndns.warenix.widget.WebImage;

import winterwell.jtwitter.Twitter.Status;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ConversationActivity extends Activity implements
		LoadConversationListener {

	ListView listView;
	ArrayList<Status> statusList;
	ConversationListViewAdapter adapter;

	public static final String BUNDLE_STATUS = "status";

	private static LayoutInflater mChildInflater;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupUI();
		onReady();
	}

	void setupUI() {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.twitter_conversation);

		listView = (ListView) findViewById(R.id.conversationList);
	}

	void onReady() {
		mChildInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		statusList = new ArrayList<Status>();
		adapter = new ConversationListViewAdapter(this, statusList);
		listView.setAdapter(adapter);

		loadConversation();

	}

	void loadConversation() {

		Bundle extras = this.getIntent().getExtras();
		if (extras != null) {
			setProgressBarIndeterminateVisibility(true);

			Status status = (Status) extras.get(BUNDLE_STATUS);
			new LoadConversationAsyncTask().execute(status, this);
		}
	}

	@Override
	public void onLoadConversationCompleted(ArrayList<Status> statusList) {
		setProgressBarIndeterminateVisibility(false);
	}

	@Override
	public void onConversationLoaded(final Status status) {
		runOnUiThread(new Runnable() {
			public void run() {
				statusList.add(0, status);
				adapter.notifyDataSetChanged();
			}
		});

	}

	static class MessageViewHolder {
		WebImage profileImage;
		TextView username;
		TextView postDate;
		TextView message;
	}

	public static class ConversationListViewAdapter extends BaseAdapter {
		Context context;
		ArrayList<Status> statusList;

		public ConversationListViewAdapter(Context context,
				ArrayList<Status> statusList) {
			this.context = context;
			this.statusList = statusList;
		}

		@Override
		public int getCount() {
			return statusList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Status status = statusList.get(position);

			View view = convertView;

			if (view == null) {
				view = mChildInflater.inflate(R.layout.bubble_message, null);

				MessageViewHolder viewHolder = new MessageViewHolder();
				viewHolder.username = (TextView) view
						.findViewById(R.id.username);
				viewHolder.postDate = (TextView) view
						.findViewById(R.id.postDate);
				viewHolder.profileImage = (WebImage) view
						.findViewById(R.id.profileImage);
				viewHolder.message = (TextView) view.findViewById(R.id.message);

				view.setTag(viewHolder);
			}

			MessageViewHolder viewHolder = (MessageViewHolder) view.getTag();
			viewHolder.username.setText(status.user.screenName);
			viewHolder.message.setText(status.text);
			viewHolder.postDate.setText(status.createdAt.toLocaleString());
			String normalProfileImageUrl = status.user.profileImageUrl
					.toString();
			String bigProfileImageUrl = normalProfileImageUrl.replace(
					"_normal", "");
			viewHolder.profileImage.startLoading(bigProfileImageUrl);

			return view;
		}
	}

}
