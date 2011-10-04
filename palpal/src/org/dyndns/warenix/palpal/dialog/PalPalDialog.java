package org.dyndns.warenix.palpal.dialog;

import java.io.File;
import java.net.URI;

import org.dyndns.warenix.palpal.content.LocalFileContentProvider;
import org.dyndns.warenix.palpaltwitter.R;
import org.dyndns.warenix.util.DownloadFileTool;
import org.dyndns.warenix.util.DownloadImageTask;
import org.dyndns.warenix.util.DownloadImageTask.DownloadImageTaskCallback;
import org.dyndns.warenix.widget.WebImage;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

public class PalPalDialog {
	public static void showTwitterOAuthDialog(Context context, URI authorizeUrl) {
		final Dialog dialog = new Dialog(context);

		dialog.setTitle("OAUTH");
		dialog.setContentView(R.layout.webviewdialog);

		dialog.setCancelable(true);
		dialog.setOnCancelListener(new OnCancelListener() {

			public void onCancel(DialogInterface arg0) {
				dialog.dismiss();
			}

		});

		// set ui
		WebView webview = (WebView) dialog.findViewById(R.id.webview);
		webview.loadUrl(authorizeUrl.toString());
		Log.v("warenix", String.format("after loadURL %s", authorizeUrl));

		dialog.show();

	}

	private static final FrameLayout.LayoutParams ZOOM_PARAMS = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);

	public static void showImageDialog(Context context, URI authorizeUrl) {
		final Dialog dialog = new Dialog(context);

		dialog.setTitle("Image");
		dialog.setContentView(R.layout.webviewdialog);

		dialog.setCancelable(true);
		dialog.setOnCancelListener(new OnCancelListener() {

			public void onCancel(DialogInterface arg0) {
				dialog.dismiss();
			}

		});

		// set ui
		final WebView webview = (WebView) dialog.findViewById(R.id.webview);

		webview.setBackgroundColor(R.color.activityTitleColor);

		FrameLayout mContentView = (FrameLayout) dialog.getWindow()
				.getDecorView().findViewById(android.R.id.content);
		final View zoom = webview.getZoomControls();
		mContentView.addView(zoom, ZOOM_PARAMS);
		zoom.setVisibility(View.GONE);

		webview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);

		// use cached image if found

		final String fileUrl = authorizeUrl.toString();
		final String completePath = DownloadFileTool.getCompletePath(
				DownloadImageTask.CACHE_DIR, WebImage.hashUrl(fileUrl));

		if (new File(completePath).exists()) {
			String contentUri = LocalFileContentProvider
					.constructUri(completePath);
			webview.loadUrl(contentUri);
		} else {
			DownloadImageTask task = new DownloadImageTask(
					new DownloadImageTaskCallback() {

						@Override
						public void onDownloadComplete(String url, Bitmap bitmap) {
							String contentUri = LocalFileContentProvider
									.constructUri(completePath);
							webview.loadUrl(contentUri);
						}

					}, fileUrl);
			task.execute(WebImage.hashUrl(fileUrl));
		}

		Log.v("warenix", String.format("after loadURL %s", authorizeUrl));

		dialog.show();

	}

}
