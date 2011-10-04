package org.dyndns.warenix.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

/**
 * download web content. it may be an image or a string from web server.
 * 
 * @author warenix
 * 
 */
public class WebContent {

	private static final String LOG_TAG = "WebContent";

	/**
	 * read web string stream usually from web api
	 * 
	 * @param url
	 *            a url to the api
	 * @return inputstream of string
	 */
	public static InputStream getInputStreamFromUrl(String url) {
		InputStream content = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			HttpClient httpclient = new DefaultHttpClient();
			// Execute HTTP Get Request
			HttpResponse response = httpclient.execute(httpGet);
			content = response.getEntity().getContent();
		} catch (Exception e) {
			// handle the exception !
			e.printStackTrace();
		}
		return content;
	}

	/**
	 * convert an input stream to a string
	 * 
	 * @param inputStream
	 * @return a string contructed from the input stream
	 */
	public static String inputStreamToString(InputStream inputStream) {
		BufferedReader rd;
		try {
			rd = new BufferedReader(
					new InputStreamReader(inputStream, "UTF-8"), 4096);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return "";
		}

		String line;
		StringBuilder sb = new StringBuilder();
		try {
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				rd.close();
			} catch (IOException e) {
			}
		}

		String contentOfMyInputStream = sb.toString();
		return contentOfMyInputStream;
	}

	public static String getContent(String url) throws Exception {
		StringBuilder sb = new StringBuilder();

		HttpClient client = new DefaultHttpClient();
		HttpParams httpParams = client.getParams();

		HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
		HttpConnectionParams.setSoTimeout(httpParams, 50000);
		HttpResponse response = client.execute(new HttpGet(url));
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					entity.getContent(), "UTF-8"), 8192);

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			reader.close();
		}
		return sb.toString();
	}

	public static Bitmap getImageBitmap(String url) {
		Bitmap bm = null;
		try {
			URL aURL = new URL(url);
			URLConnection conn = aURL.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is, 8096);
			bm = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
		} catch (FileNotFoundException fnfe) {

		} catch (IOException e) {
			Log.e(LOG_TAG, "Error getting bitmap", e);
		}
		return bm;
	}

	static int IO_BUFFER_SIZE = 1024;

	/**
	 * load photo from url, save it in a directory and return a bitmap of it.
	 * 
	 * @param url
	 * @param saveInDir
	 * @param saveAsFilename
	 * @return
	 */
	public static Bitmap loadPhotoBitmap(URL url, String saveInDir,
			String saveAsFilename) {

		String completePath = isExistInSaveInDir(saveInDir, saveAsFilename);
		if (completePath == null) {
			Log.v("warenix", "load thumbnail cache " + completePath);
			completePath = (new DownloadFileTool()).downloadFile(url,
					saveInDir, saveAsFilename);
		}

		Bitmap bitmap = null;
		try {
			// Only decode image size. Not whole image
			BitmapFactory.Options option = new BitmapFactory.Options();
			option.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(completePath, option);

			// The new size to decode to
			final int NEW_SIZE = 800;

			// Now we have image width and height. We should find the
			// correct scale value. (power of 2)
			int width = option.outWidth;
			int height = option.outHeight;
			int scale = 1;

			while (true) {
				if (width / 2 < NEW_SIZE || height / 2 < NEW_SIZE)
					break;
				width /= 2;
				height /= 2;
				scale++;
			}

			// Decode again with inSampleSize
			option = new BitmapFactory.Options();
			option.inSampleSize = scale;
			option.inPurgeable = true;
			option.inInputShareable = true;
			// byte[] byteArrayForBitmap = new byte[16 * 1024];
			// option.inTempStorage = byteArrayForBitmap;

			// System.gc();
			bitmap = BitmapFactory.decodeFile(completePath, option);
			// System.gc();
			// byteArrayForBitmap = null;
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	/**
	 * create a transparent bitmap from an existing bitmap by replacing certain
	 * color with transparent
	 * 
	 * @param bitmap
	 *            the original bitmap with a color you want to replace
	 * @return a replaced color immutable bitmap
	 */
	public static Bitmap createTransparentBitmapFromBitmap(Bitmap bitmap,
			int replaceThisColor) {
		if (bitmap != null) {
			int picw = bitmap.getWidth();
			int pich = bitmap.getHeight();
			int[] pix = new int[picw * pich];
			bitmap.getPixels(pix, 0, picw, 0, 0, picw, pich);

			for (int y = 0; y < pich; y++) {
				// from left to right
				for (int x = 0; x < picw; x++) {
					int index = y * picw + x;
					int r = (pix[index] >> 16) & 0xff;
					int g = (pix[index] >> 8) & 0xff;
					int b = pix[index] & 0xff;

					if (pix[index] == replaceThisColor) {
						pix[index] = Color.TRANSPARENT;
					} else {
						break;
					}
				}

				// from right to left
				for (int x = picw - 1; x >= 0; x--) {
					int index = y * picw + x;
					int r = (pix[index] >> 16) & 0xff;
					int g = (pix[index] >> 8) & 0xff;
					int b = pix[index] & 0xff;

					if (pix[index] == replaceThisColor) {
						pix[index] = Color.TRANSPARENT;
					} else {
						break;
					}
				}
			}

			Bitmap bm = Bitmap.createBitmap(pix, picw, pich,
					Bitmap.Config.ARGB_4444);

			return bm;
		}
		return null;
	}

	static String sdDrive = Environment.getExternalStorageDirectory()
			.getAbsolutePath();

	/**
	 * check if the file exists or not
	 * 
	 * @param saveInDir
	 * @param saveAsFilename
	 * @return full local dir path
	 */
	static String isExistInSaveInDir(String saveInDir, String saveAsFilename) {

		String fullLocalDirPath = String.format("%s/%s/%s", sdDrive, saveInDir,
				saveAsFilename);
		File localFile = new File(fullLocalDirPath);
		Log.i(LOG_TAG, "on cache directory file size:" + localFile.length());
		// FIXME file size less than 700 is considered not yet finished download
		if (localFile.exists() && localFile.length() > 700) {
			return fullLocalDirPath;
		}
		return null;
	}

	class CacheFilenameFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			// return (name.endsWith(".jpg") || name.endsWith(".jpeg"));
			return true;
		}
	}
}
