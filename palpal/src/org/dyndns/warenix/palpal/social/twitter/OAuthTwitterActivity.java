package org.dyndns.warenix.palpal.social.twitter;

import java.net.URI;

import org.dyndns.warenix.palpal.PalPal;
import org.dyndns.warenix.palpal.PalPalPreference;
import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.dialog.PalPalDialog;
import org.dyndns.warenix.util.ToastUtil;

import winterwell.jtwitter.OAuthSignpostClient;
import winterwell.jtwitter.TwitterException;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * pop out web oauth login
 * 
 * @author warenix
 */
public class OAuthTwitterActivity extends Activity {

	EditText oauthPINText;
	Button submitButton;

	OAuthSignpostClient oauthClient = new OAuthSignpostClient(
			PalPal.JTWITTER_OAUTH_KEY, PalPal.JTWITTER_OAUTH_SECRET, "oob");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		oauthPINText = (EditText) findViewById(R.id.oauth_pin);

		submitButton = (Button) findViewById(R.id.submit);
		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String pin = oauthPINText.getText().toString();
				if (pin.equals("")) {
					Log.v("warenix", "oauth pin is empty");
					ToastUtil.showQuickToast(OAuthTwitterActivity.this,
							"please enter pin");
					finish();
					return;
				}

				doOAuthTwitter(pin);

			}
		});

		showOAuthDialog();
	}

	public void showOAuthDialog() {
		// Make an oauth client (you'll want to change this bit)
		// Open the authorisation page in the user's browser
		// On Android, you'd direct the user to URI url = client.authorizeUrl();
		// On a desktop, we can do that like this:
		// oauthClient.authorizeDesktop();
		try {
			URI authorizeUrl = oauthClient.authorizeUrl();

			PalPalDialog.showTwitterOAuthDialog(this, authorizeUrl);

			// WebView myWebView = (WebView) findViewById(R.id.webview);
			// myWebView.loadUrl(url.toString());
			//
			// // get the pin
			// // String v = oauthClient
			// // .askUser("Please enter the verification PIN from Twitter");
			// String v = "5694443";
			// oauthClient.setAuthorizationCode(v);
			// // Store the authorisation token details for future use
			// String[] accessToken = oauthClient.getAccessToken();
			// // Next time we can use new OAuthSignpostClient(OAUTH_KEY,
			// OAUTH_SECRET,
			// // accessToken[0], accessToken[1]) to avoid authenticating again.
			//
			// // Make a Twitter object
			// Twitter twitter = new Twitter("warenix", oauthClient);
			// // Print Daniel Winterstein's status
			// System.out.println(twitter.getStatus("warenix"));
			// // Set my status
			// // twitter.setStatus("Messing about in Java");

			Log.v("warenix", "after showTwitterOAuthDialog");
		} catch (TwitterException e) {
			ToastUtil.showNotification(this, "fail to authenticate twitter",
					"twitter authentication error", e.getMessage(), null, 1000);
		}
	}

	private void doOAuthTwitter(final String pin) {
		Log.v("warenix", String.format("doOAuthTwitter ping [%s]", pin));

		try {
			oauthClient.setAuthorizationCode(pin);
			// Store the authorisation token details for future use
			String[] accessToken = oauthClient.getAccessToken();
			if (accessToken == null) {
				ToastUtil.showNotification(this,
						"fail to authenticate twitter", "login fail",
						"cannot get access token", null, 1000);
				finish();
			}
			PalPalPreference.writeAccessTokenPreference(this, accessToken);

			setResult(RESULT_OK);
			finish();
		} catch (TwitterException e) {
			ToastUtil.showNotification(this, "fail to authenticate twitter",
					"twitter authentication error", e.getMessage(), null, 1000);
			finish();
		}

	}
}
