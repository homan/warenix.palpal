package org.dyndns.warenix.google.translate;

import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class TranslationMaster {
	static String apiKey = "";

	public static String translate(String originalText) {

		String toLanguageCode = "en";
		String url = String.format("key=%s&target=%s&", apiKey, toLanguageCode);

		try {
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(
					"http.protocol.content-charset", "UTF-8");
			String utfurl = "https://www.googleapis.com/language/translate/v2?"
					+ url + "&q=" + URLEncoder.encode(originalText, "utf-8");
			Log.d("warenix", "sending to " + utfurl);
			HttpGet httpGet = new HttpGet(utfurl);
			HttpResponse response = httpclient.execute(httpGet);

			String result = EntityUtils.toString(response.getEntity());
			Log.d("warenix", "translated to "
					+ extractTranslationResult(result));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static String extractTranslationResult(String resultHtml) {
		try {
			Log.d("warenix", "result:" + resultHtml);
			JSONObject json = new JSONObject(resultHtml);
			JSONObject data = json.getJSONObject("data");
			JSONArray translations = data.getJSONArray("translations");
			String translatedText = translations.getJSONObject(0).getString(
					"translatedText");
			return translatedText;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static DefaultHttpClient getClient() {
		DefaultHttpClient ret = null;

		// sets up parameters
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "utf-8");
		params.setBooleanParameter("http.protocol.expect-continue", false);

		// registers schemes for both http and https
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		final SSLSocketFactory sslSocketFactory = SSLSocketFactory
				.getSocketFactory();
		sslSocketFactory
				.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		registry.register(new Scheme("https", sslSocketFactory, 443));

		ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(
				params, registry);
		ret = new DefaultHttpClient(manager, params);
		return ret;
	}
}
