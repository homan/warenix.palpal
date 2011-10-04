package org.dyndns.warenix.palpal.message;

import java.util.ArrayList;

import org.dyndns.warenix.palpal.social.twitter.Friend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TwitterDBManager {

	static final String DATABASE_NAME = "twitter";
	static final int DATABASE_VERSION = 2;

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	Context context;

	// table hashtag
	static final String HASHTAG_TABLE = "hashtag";
	static final String ROW_ID = "id";
	static final String ROW_HASHTAG = "hashtag";
	static final String ROW_POSTDATE = "postDate";

	static String CREATE_HASHTAG_TABLE = String
			.format("CREATE TABLE %s (%s integer primary key autoincrement not null,  %s text, %s date)",
					HASHTAG_TABLE, ROW_ID, ROW_HASHTAG, ROW_POSTDATE);

	// table friend
	static final String FRIEND_TABLE = "friend";
	static final String ROW_USERNAME = "usernmae";
	static final String ROW_PROFILE_IMAGE_URL = "profileImageUrl";
	static final String ROW_RELATION = "relation";
	static final String ROW_USAGE_COUNT = "usageCount";

	static String CREATE_FRIEND_TABLE = String
			.format("CREATE TABLE %s (%s integer primary key autoincrement not null,  %s text, %s text, %s text, %s int)",
					FRIEND_TABLE, ROW_ID, ROW_USERNAME, ROW_PROFILE_IMAGE_URL,
					ROW_RELATION, ROW_USAGE_COUNT);

	public TwitterDBManager(Context context) {
		this.context = context;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i("palpal", "create db [message]");
			db.execSQL(CREATE_HASHTAG_TABLE);
			Log.i("palpal", "create db [friend]");
			db.execSQL(CREATE_FRIEND_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("palpal", "Upgrading database from version " + oldVersion
					+ " to " + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + HASHTAG_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + FRIEND_TABLE);
			onCreate(db);
		}
	}

	// +++ hashtag table
	/**
	 * insert message into centralized message table
	 * 
	 * @param message
	 */
	public void insertHashtag(String hashtag) {
		db = DBHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(ROW_POSTDATE, System.currentTimeMillis());
		initialValues.put(ROW_HASHTAG, hashtag);
		long rowId = db.insert(HASHTAG_TABLE, null, initialValues);
		db.close();
		db = null;

		Log.d("palpal", "inserted hashtag at row " + rowId);
	}

	public int updateHashtag(String hashtag) {
		db = DBHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(ROW_POSTDATE, System.currentTimeMillis());
		initialValues.put(ROW_HASHTAG, hashtag);
		int affectedRows = db.update(HASHTAG_TABLE, initialValues, "hashtag=?",
				new String[] { hashtag });
		db.close();
		db = null;

		Log.d("palpal", String.format("updated %d hashtag rows", affectedRows));
		return affectedRows;
	}

	/**
	 * retrieve all message
	 * 
	 * @return
	 */
	public ArrayList<String> getHashtagList() {
		return getHashtagList(99999, -1);
	}

	/**
	 * 
	 * @param rowCount
	 * @param fromRow
	 *            -1 means the beginning
	 * @return
	 */
	public ArrayList<String> getHashtagList(int rowCount, int fromRow) {
		ArrayList<String> messageList = new ArrayList<String>();

		db = DBHelper.getReadableDatabase();
		String[] columns = { ROW_HASHTAG };

		Cursor cursor = db.query(HASHTAG_TABLE, columns, null, null, null, "",
				ROW_POSTDATE + " desc");

		int toRow = fromRow + rowCount;

		while (cursor.moveToNext() && rowCount-- > 0) {

			messageList.add(cursor.getString(0));

			// paging
			++fromRow;
			if (fromRow == toRow) {
				break;
			}
		}
		db.close();
		db = null;

		return messageList;
	}

	// --- hashtag table

	// +++ friend table

	/**
	 * insert message into centralized message table
	 * 
	 * @param message
	 */
	public void insertFriend(Friend friend) {
		db = DBHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(ROW_USERNAME, friend.username);
		initialValues.put(ROW_PROFILE_IMAGE_URL, friend.profileImageUrl);
		initialValues.put(ROW_RELATION, friend.relation);
		initialValues.put(ROW_USAGE_COUNT, friend.usageCount);
		long rowId = db.insert(FRIEND_TABLE, null, initialValues);
		db.close();
		db = null;

		Log.d("palpal", "inserted friend at row " + rowId);
	}

	public int updateFriend(Friend friend) {
		db = DBHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(ROW_USERNAME, friend.username);
		initialValues.put(ROW_PROFILE_IMAGE_URL, friend.profileImageUrl);
		initialValues.put(ROW_RELATION, friend.relation);
		initialValues.put(ROW_USAGE_COUNT, friend.usageCount + 1);
		int affectedRows = db.update(FRIEND_TABLE, initialValues, ROW_USERNAME
				+ "=?", new String[] { friend.username });
		db.close();
		db = null;

		Log.d("palpal", String.format("updated %d friend rows", affectedRows));
		return affectedRows;
	}

	/**
	 * retrieve all message
	 * 
	 * @return
	 */
	public ArrayList<Friend> getFriendList(String relation) {
		return getFriendList(relation, 99999, -1);
	}

	/**
	 * 
	 * @param rowCount
	 * @param fromRow
	 *            -1 means the beginning
	 * @return
	 */
	public ArrayList<Friend> getFriendList(String relation, int rowCount,
			int fromRow) {
		ArrayList<Friend> messageList = new ArrayList<Friend>();

		db = DBHelper.getReadableDatabase();
		String[] columns = { ROW_USERNAME, ROW_PROFILE_IMAGE_URL, ROW_RELATION,
				ROW_USAGE_COUNT };

		Cursor cursor = db.query(FRIEND_TABLE, columns, null, null, null, "",
				ROW_USAGE_COUNT + " desc, " + ROW_USERNAME + " desc");

		int toRow = fromRow + rowCount;

		Friend friend;
		while (cursor.moveToNext() && rowCount-- > 0) {

			friend = new Friend(cursor.getString(0), cursor.getString(1),
					cursor.getString(2), cursor.getInt(3));
			messageList.add(friend);

			// paging
			++fromRow;
			if (fromRow == toRow) {
				break;
			}
		}
		db.close();
		db = null;

		return messageList;
	}

	// --- friend table
}
