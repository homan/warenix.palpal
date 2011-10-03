package org.dyndns.warenix.palpal.dialog;

import java.net.URI;

import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.content.LocalFileContentProvider;
import org.dyndns.warenix.util.DownloadFileTool;
import org.dyndns.warenix.util.DownloadImageTask;
import org.dyndns.warenix.widget.WebImage;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
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
		WebView webview = (WebView) dialog.findViewById(R.id.webview);

		webview.setBackgroundColor(R.color.facebook_album_photo_background);

		FrameLayout mContentView = (FrameLayout) dialog.getWindow()
				.getDecorView().findViewById(android.R.id.content);
		final View zoom = webview.getZoomControls();
		mContentView.addView(zoom, ZOOM_PARAMS);
		zoom.setVisibility(View.GONE);

		webview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);

		// use cached image if found
		String localFile = authorizeUrl.toString();
		String contentUri = LocalFileContentProvider
				.constructUri(DownloadFileTool.getCompletePath(
						DownloadImageTask.CACHE_DIR,
						WebImage.hashUrl(localFile)));
		webview.loadUrl(contentUri);
		Log.v("warenix", String.format("after loadURL %s", authorizeUrl));

		dialog.show();

	}
}
