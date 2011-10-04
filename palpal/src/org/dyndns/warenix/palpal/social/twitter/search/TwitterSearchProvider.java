package org.dyndns.warenix.palpal.social.twitter.search;

import java.util.ArrayList;
import java.util.List;

import org.dyndns.warenix.palpal.social.twitter.activity.SearchActivity;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class TwitterSearchProvider extends SearchRecentSuggestionsProvider {

	public static String AUTHORITY = "org.dyndns.warenix.palpal.social.twitter.search.TwitterSearchProvider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/search");

	public static class Columns {
		public static final String ID = BaseColumns._ID;
		public static final String NAME = SearchManager.SUGGEST_COLUMN_TEXT_1;
		public static final String DATA = SearchManager.SUGGEST_COLUMN_INTENT_DATA;
		public static final String EXTRA_DATA = SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA;
	}

	static class SearchDomain {
		String id;
		String name;
		String data;
		String type;

		public SearchDomain(String id, String name, String data, String type) {
			this.id = id;
			this.name = name;
			this.data = data;
			this.type = type;
		}
	}

	private static final ArrayList<SearchDomain> sData = new ArrayList<SearchDomain>();

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		return "vnd.android.cursor.dir/vnd.palpal.twitterSearchProvider";
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		return null;
	}

	@Override
	public boolean onCreate() {
		sData.add(new SearchDomain("public", "Search publicly '%s'", "%s", null));
		sData.add(new SearchDomain("user", "Search User '@%s'", "%s",
				SearchActivity.SEARCH_TYPE_USER_HOME_TIMELINE));
		sData.add(new SearchDomain("local", "Search locally '%s'", "%s",
				SearchActivity.SEARCH_TYPE_LOCAL));
		sData.add(new SearchDomain("realtime", "Realtime '%s'", "%s",
				SearchActivity.SEARCH_TYPE_REALTIME));
		sData.add(new SearchDomain("realtime", "Near Hong Kong", "%s",
				SearchActivity.SEARCH_TYPE_NEAR));
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		List<String> pathSegments = uri.getPathSegments();
		int noOfPathSegments = pathSegments.size();
		if (noOfPathSegments > 1) {

			String keyword = pathSegments.get(pathSegments.size() - 1);
			final MatrixCursor c = new MatrixCursor(new String[] { Columns.ID,
					Columns.NAME, Columns.DATA,
					SearchManager.SUGGEST_COLUMN_QUERY,
					SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA,
					SearchManager.SUGGEST_COLUMN_ICON_1 });
			for (int i = 0; i < sData.size(); ++i) {
				final SearchDomain data = sData.get(i);
				c.addRow(new Object[] { new Integer(i),
						String.format(data.name, keyword),
						String.format(data.data, keyword),
						String.format(data.data, keyword), data.type,
						android.R.drawable.ic_menu_search });
			}
			return c;
		}
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

}
