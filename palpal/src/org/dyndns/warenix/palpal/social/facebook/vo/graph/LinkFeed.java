package org.dyndns.warenix.palpal.social.facebook.vo.graph;

import java.lang.ref.SoftReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.dialog.PalPalDialog;
import org.dyndns.warenix.palpal.social.facebook.FacebookException;
import org.dyndns.warenix.palpal.social.facebook.LinkPreview;
import org.dyndns.warenix.palpal.social.facebook.PreviewImageAdapter;
import org.dyndns.warenix.palpal.social.facebook.activity.CommentActivity;
import org.dyndns.warenix.palpal.social.facebook.activity.ShareActivity;
import org.dyndns.warenix.palpal.social.facebook.util.DialogUtil;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookAPI;
import org.dyndns.warenix.palpal.social.facebook.util.FacebookUtil;
import org.dyndns.warenix.palpal.social.facebook.vo.FacebookEditable;
import org.dyndns.warenix.palpal.social.facebook.vo.FacebookPost;
import org.dyndns.warenix.widget.WebImage;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class LinkFeed extends FacebookPost implements FacebookEditable {

	public static final String TYPE = "link";
	static final String ACTION_OPEN_BROWSER = "Open in Browser";
	static final String ACTION_SHARE = "Share";
	public static final String ACTION_ZOOM = "Zoom";

	public String link;
	public String name;
	public String description;
	public String picture;
	public String message;

	class ViewHolder {
		TextView messageText;
		WebImage pictureImage;
		TextView descriptionText;
		TextView nameText;
		TextView linkText;
		TextView statsText;
		TextView createdTimeText;
	}

	public LinkFeed() {
		super(TYPE);
	}

	public LinkFeed(Parcel in) throws FacebookException {
		super(TYPE, in);
	}

	public LinkFeed(JSONObject post) throws FacebookException {
		super(TYPE, post);
	}

	public static LinkFeed factory(JSONObject post) throws FacebookException {
		LinkFeed statusFeed = new LinkFeed(post);
		return statusFeed;
	}

	@Override
	public void parseJSONObject(JSONObject json) {
		link = FacebookUtil.getJSONString(json, "link", null);
		name = FacebookUtil.getJSONString(json, "name", null);
		description = FacebookUtil.getJSONString(json, "description", null);
		picture = FacebookUtil.getJSONString(json, "picture", null);
		picture = FacebookUtil.getLargeImage(picture);
		message = FacebookUtil.getJSONString(json, "message", null);
		try {
			likesCount = json.getJSONObject("likes").getString("count");
		} catch (JSONException e) {
		}
	}

	@Override
	public View factory(Context context, View convertView,
			HashMap<String, SoftReference<Bitmap>> imagePool) {
		if (inflater == null) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		View view = convertView;
		if (view == null
				|| (view != null && (view.getTag().equals(type) == false))) {
			view = inflater.inflate(R.layout.link_feed, null);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.nameText = (TextView) view.findViewById(R.id.name);
			viewHolder.descriptionText = (TextView) view
					.findViewById(R.id.description);
			viewHolder.pictureImage = (WebImage) view
					.findViewById(R.id.picture);
			viewHolder.messageText = (TextView) view.findViewById(R.id.message);
			viewHolder.linkText = (TextView) view.findViewById(R.id.link);
			viewHolder.statsText = (TextView) view.findViewById(R.id.stats);
			viewHolder.createdTimeText = (TextView) view
					.findViewById(R.id.createdTime);
			view.setTag(R.id.picture, viewHolder);
		}

		ViewHolder viewHolder = (ViewHolder) view.getTag(R.id.picture);

		viewHolder.nameText.setText(name);

		viewHolder.descriptionText.setText(description);

		if (picture != null) {
			viewHolder.pictureImage.setVisibility(View.VISIBLE);

			SoftReference<Bitmap> ref = imagePool.get(picture);
			if (ref == null) {
				viewHolder.pictureImage.startLoading(picture, imagePool);
			} else {
				Bitmap bm = ref.get();
				if (bm == null) {
					viewHolder.pictureImage.startLoading(picture, imagePool);
				} else {
					viewHolder.pictureImage.setImageBitmap(bm);
				}
			}

		} else {
			viewHolder.pictureImage.setVisibility(View.GONE);
		}

		viewHolder.messageText.setText(message);

		viewHolder.linkText.setText(link);

		viewHolder.statsText.setText(String.format("%s likes | %s comments",
				likesCount, commentsCount));

		String createdTimeString = FacebookUtil
				.formatFacebookTimeToLocaleString(createdTime);
		viewHolder.createdTimeText.setText(createdTimeString);

		view.setTag(type);
		return view;
	}

	public void addCustomAction() {
		actionNameList.add(ACTION_OPEN_BROWSER);
		actionLinkList.add(ACTION_OPEN_BROWSER);

		actionNameList.add(ACTION_SHARE);
		actionLinkList.add(ACTION_SHARE);

		actionNameList.add(ACTION_ZOOM);
		actionLinkList.add(ACTION_ZOOM);
	}

	@Override
	public boolean handleCustomAction(Context context, int actionIndex) {
		String actionName = actionNameList.get(actionIndex);
		String actionLink = actionLinkList.get(actionIndex);

		Log.d("palpal", String.format(
				"PhotoFeed try to handle custom action %d %s", actionIndex,
				actionName));
		if (actionName.equals(ACTION_DEFAULT)) {
			Intent intent = new Intent(context, CommentActivity.class);
			Bundle extras = new Bundle();
			extras.putString(CommentActivity.BUNDLE_POST_ID, id);
			extras.putParcelable(CommentActivity.BUNDLE_FEED, this);
			intent.putExtras(extras);
			context.startActivity(intent);
			return true;
		} else if (actionLink.equals(ACTION_OPEN_BROWSER)) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			Uri data = Uri.parse(link);
			intent.setData(data);
			context.startActivity(intent);
			return true;
		} else if (actionLink.equals(ACTION_SHARE)) {
			Intent intent = new Intent(context, ShareActivity.class);
			Bundle extras = new Bundle();
			extras.putParcelable(ShareActivity.BUNDLE_FEED, this);
			intent.putExtras(extras);
			context.startActivity(intent);
			return true;
		} else if (actionName.equals(ACTION_ZOOM)) {
			if (picture != null) {
				try {
					PalPalDialog.showImageDialog(context,
							new URI(FacebookUtil.getLargeImage(picture)));
					return true;

				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public LinkPreview fetchLinkPreview() {
		String responseString;
		try {
			responseString = FacebookAPI.linksPreview(link);

			if (responseString != null) {
				JSONObject json = new JSONObject(responseString);
				LinkPreview preview = new LinkPreview(json);
				return preview;
			}

		} catch (FacebookException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * Parcelable
	 */

	public static final Parcelable.Creator<FacebookPost> CREATOR = new Parcelable.Creator<FacebookPost>() {
		public FacebookPost createFromParcel(Parcel in) {
			try {
				return new LinkFeed(in);
			} catch (FacebookException e) {
				return null;
			}
		}

		public FacebookPost[] newArray(int size) {
			return new LinkFeed[size];
		}
	};

	@Override
	public FacebookPost factory() {
		LinkFeed clone = new LinkFeed();
		clone.description = this.description;
		clone.name = this.name;
		clone.link = this.link;
		clone.message = this.message;
		clone.picture = this.picture;
		return clone;
	}

	boolean isCancelled;

	@Override
	public View getFacebookEditable(final FacebookPost feed, final View view,
			final Context context,
			final HashMap<String, SoftReference<Bitmap>> imagePool) {

		final LinkFeed self = this;

		View postView = ((View) view);
		// add listener
		final TextView messageText = (TextView) postView
				.findViewById(R.id.message);
		messageText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogUtil.showInputDialogForTextView(context, "message",
						"What's in your mind?", messageText, self.message,
						"message", self);
			}
		});
		messageText
				.setBackgroundResource(R.drawable.facebook_editable_field_background);
		messageText.setHint("message");

		final TextView nameText = (TextView) postView.findViewById(R.id.name);
		nameText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogUtil.showInputDialogForTextView(context, "name",
						"How do you call this link?", nameText, self.name,
						"name", self);
			}
		});
		nameText.setBackgroundResource(R.drawable.facebook_editable_field_background);
		nameText.setHint("name");

		final TextView linkText = (TextView) postView.findViewById(R.id.link);
		linkText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogUtil.showInputDialogForTextView(context, "link",
						"What's the link?", linkText, self.link, "link", self);
			}
		});
		linkText.setBackgroundResource(R.drawable.facebook_editable_field_background);
		linkText.setHint("link");

		final TextView descriptionText = (TextView) (View) postView
				.findViewById(R.id.description);
		descriptionText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogUtil.showInputDialogForTextView(context, "description",
						"What's the link about?", descriptionText,
						self.description, "description", self);
			}
		});
		descriptionText
				.setBackgroundResource(R.drawable.facebook_editable_field_background);
		descriptionText.setHint("description");

		isCancelled = false;

		final WebImage picture = (WebImage) postView.findViewById(R.id.picture);
		picture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// DialogUtil.showLinkPreviewForWebImage(context, "link",
				// "pick a preview", self, picture, imagePool);

				final ProgressDialog pd = ProgressDialog.show(context,
						"Working..", "Fetching link previews", true, true);

				WindowManager.LayoutParams lp = pd.getWindow().getAttributes();
				lp.dimAmount = 0.0f;
				pd.getWindow().setAttributes(lp);
				pd.getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

				pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						isCancelled = true;
					}
				});

				final Handler messageHandler = new Handler() {

					@Override
					public void handleMessage(Message msg) {
						if (isCancelled) {
							return;
						}

						pd.dismiss();

						AlertDialog.Builder alert = new AlertDialog.Builder(
								context);

						alert.setTitle("picture");
						alert.setMessage("pick a picture");

						final LinkPreview linkPreview = (LinkPreview) msg.obj;
						if (linkPreview != null) {
							PreviewImageAdapter imageAdapter = new PreviewImageAdapter(
									context, linkPreview.imageList, imagePool);

							Gallery previewGallery = new Gallery(context);
							previewGallery.setAdapter(imageAdapter);

							previewGallery
									.setOnItemClickListener(new OnItemClickListener() {
										public void onItemClick(
												AdapterView parent, View v,
												int position, long id) {
											linkPreview.selectedImageURL = linkPreview.imageList
													.get(position);

										}
									});

							alert.setView(previewGallery);

							alert.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {

											if (linkPreview.selectedImageURL == null) {
												return;
											}

											picture.startLoading(linkPreview.selectedImageURL);

											updateAttributeByKey(
													"picture",
													linkPreview.selectedImageURL);

											descriptionText
													.setText(linkPreview.description);
											updateAttributeByKey("description",
													linkPreview.description);

											nameText.setText(linkPreview.name);
											updateAttributeByKey("name",
													linkPreview.name);

										}
									});

						} else {
							TextView tv = new TextView(context);
							tv.setText("no preview");
							alert.setView(tv);
						}

						alert.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										// Canceled.
									}
								});

						alert.show();

					}

				};

				// Here is the heavy-duty thread
				Thread t = new Thread() {

					public void run() {

						// do expensive
						final LinkPreview linkPreview = fetchLinkPreview();

						// Send update to the main thread
						Message message = Message.obtain();
						message.obj = linkPreview;
						message.what = 1;

						messageHandler.sendMessage(message);
					}
				};
				t.start();

				// new FetchLinkPreviewAsyncTask(context, alert, webImage,
				// previewGallery,
				// imagePool).execute(link);
			}

		});

		picture.setVisibility(View.VISIBLE);

		return view;
	}

	@Override
	public FacebookEditable getFacebookFeed(View postView) {
		// original post values
		LinkFeed linkFeed = (LinkFeed) this.factory();

		// update with new values
		final TextView messageText = (TextView) postView
				.findViewById(R.id.message);
		linkFeed.message = messageText.getText().toString();

		final TextView nameText = (TextView) postView.findViewById(R.id.name);
		linkFeed.name = nameText.getText().toString();

		final TextView descriptionText = (TextView) postView
				.findViewById(R.id.description);
		linkFeed.description = descriptionText.getText().toString();

		final TextView linkText = (TextView) postView.findViewById(R.id.link);
		linkFeed.link = linkText.getText().toString();

		return linkFeed;
	}

	@Override
	public void updateAttributeByKey(final String key, String value) {
		if (key.equals("message")) {
			this.message = value;
		} else if (key.equals("link")) {
			this.link = value;
		} else if (key.equals("description")) {
			this.description = value;
		} else if (key.equals("name")) {
			this.name = value;
		} else if (key.equals("picture")) {
			this.picture = value;
		}
	}
}
