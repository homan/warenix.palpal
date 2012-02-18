package org.dyndns.warenix.mission.twitter;

import org.dyndns.warenix.lab.compat1.R;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TwitterUserAutoCompleteAdapter extends CursorAdapter {

	public TwitterUserAutoCompleteAdapter(Context context, Cursor c) {
		super(context, c);
		// TODO Auto-generated constructor stub
	}

	public TwitterUserAutoCompleteAdapter(Context context, Cursor c,
			boolean autoRequery) {
		super(context, c, autoRequery);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
	}

	@Override
	public View newView(Context context, Cursor arg1, ViewGroup parent) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.user_auto_complete_item, null);
		return view;
	}

	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		if (getFilterQueryProvider() != null) {
			return getFilterQueryProvider().runQuery(constraint);
		}

		return null;
	}

	@Override
	public String convertToString(Cursor cursor) {
		return cursor
				.getString(cursor
						.getColumnIndex(TwitterUserFilterQueryProvider.BaseColumns.username));
	}

}
