package org.dyndns.warenix.mission.twitter;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.widget.FilterQueryProvider;

public class TwitterUserFilterQueryProvider implements FilterQueryProvider {

	public static class BaseColumns {
		public static final String ID = "_id";
		public static final String username = "username";
		public static final String imageUrl = "imageUrl";
	}

	public static final String[] columnNames = { BaseColumns.ID,
			BaseColumns.username, BaseColumns.imageUrl };

	@Override
	public Cursor runQuery(CharSequence constraint) {

		MatrixCursor cursor = new MatrixCursor(columnNames);
		int len = constraint.length();
		int resultSize = 6 - len;
		while (resultSize-- > 0) {
			cursor.addRow(new Object[] { 1, "", "" });
		}

		return cursor;
	}

}
