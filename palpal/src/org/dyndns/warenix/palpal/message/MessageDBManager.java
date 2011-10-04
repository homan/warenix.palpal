package org.dyndns.warenix.palpal.message;

import java.sql.Date;
import java.util.ArrayList;

import org.dyndns.warenix.palpal.bubbleMessage.BubbleMessage;
import org.dyndns.warenix.palpal.social.twitter.TwitterBubbleMessage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MessageDBManager {

	static final String DATABASE_NAME = "message";
	static final int DATABASE_VERSION = 2;

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	Context context;

	// table message
	static final String MESSAGE_TABLE = "message";
	static final String ROW_ID = "id";
	static final String ROW_USERNAME = "username";
	static final String ROW_MESSAGE = "message";
	static final String ROW_POSTDATE = "postDate";
	static final String ROW_PROFILE_IMAGE_URL = "profileImageUrl";
	// foriegn key
	static final String ROW_SOCIAL_NETWORK = "socialNetwork";
	static final String ROW_SOCIAL_NETWORK_MESSAGE_ID = "socialNetworkMessageId";

	static String CREATE_MESSAGE_TABLE = String
			.format("CREATE TABLE MESSAGE (%s integer primary key autoincrement not null,  %s text, %s text, %s date, %s text, %s text, %s text)",
					ROW_ID, ROW_USERNAME, ROW_MESSAGE, ROW_POSTDATE,
					ROW_PROFILE_IMAGE_URL, ROW_SOCIAL_NETWORK,
					ROW_SOCIAL_NETWORK_MESSAGE_ID);

	public MessageDBManager(Context context) {
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
			db.execSQL(CREATE_MESSAGE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("palpal", "Upgrading database from version " + oldVersion
					+ " to " + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE);
			onCreate(db);
		}
	}

	/**
	 * insert message into centralized message table
	 * 
	 * @param message
	 */
	public void insertMessage(BubbleMessage message) {
		db = DBHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(ROW_USERNAME, message.username);
		initialValues.put(ROW_MESSAGE, message.message);
		initialValues.put(ROW_POSTDATE, message.postDate.getTime());
		initialValues.put(ROW_SOCIAL_NETWORK, message.socialNetwork);
		initialValues.put(ROW_SOCIAL_NETWORK_MESSAGE_ID,
				message.socialNetworkMessageId);
		initialValues.put(ROW_PROFILE_IMAGE_URL,
				message.profileImageUrl.replace("_normal", ""));
		initialValues.put(ROW_USERNAME, message.username);
		long rowId = db.insert(MESSAGE_TABLE, null, initialValues);
		db.close();
		db = null;

		Log.d("palpal", "inserted message at row " + rowId);
	}

	/**
	 * get the latest postdate of a message of a social network. this helps
	 * update message service to add only newer message into database
	 * 
	 * @param socialNetwork
	 * @return
	 */
	public Date getLatestPostDateBySocialNetwork(String socialNetwork) {
		Date postDate = null;

		db = DBHelper.getReadableDatabase();
		String[] columns = { ROW_POSTDATE };
		String selection = String.format("%s = '%s'", ROW_SOCIAL_NETWORK,
				socialNetwork);

		Cursor cursor = db.query(MESSAGE_TABLE, columns, selection, null, null,
				"", ROW_POSTDATE + " desc");
		if (cursor.moveToNext()) {
			postDate = new java.sql.Date(cursor.getLong(0));
		}
		db.close();
		db = null;

		return postDate;
	}

	public String getLatestSocialNetworkIdBySocialNetwork(String socialNetwork) {
		String socialNetworkId = null;

		db = DBHelper.getReadableDatabase();
		String[] columns = { ROW_SOCIAL_NETWORK_MESSAGE_ID };
		String selection = String.format("%s = '%s'", ROW_SOCIAL_NETWORK,
				socialNetwork);

		Cursor cursor = db.query(MESSAGE_TABLE, columns, selection, null, null,
				"", ROW_POSTDATE + " desc");
		if (cursor.moveToNext()) {
			socialNetworkId = cursor.getString(0);
		}
		db.close();
		db = null;

		return socialNetworkId;
	}

	/**
	 * retrieve all message
	 * 
	 * @return
	 */
	public ArrayList<BubbleMessage> getMessageList() {
		return getMessageList(99999, -1);
	}

	/**
	 * 
	 * @param rowCount
	 * @param fromRow
	 *            -1 means the beginning
	 * @return
	 */
	public ArrayList<BubbleMessage> getMessageList(int rowCount, int fromRow) {
		ArrayList<BubbleMessage> messageList = new ArrayList<BubbleMessage>();

		db = DBHelper.getReadableDatabase();
		String[] columns = { ROW_POSTDATE, ROW_USERNAME, ROW_MESSAGE,
				ROW_SOCIAL_NETWORK, ROW_PROFILE_IMAGE_URL,
				ROW_SOCIAL_NETWORK_MESSAGE_ID };

		Cursor cursor = db.query(MESSAGE_TABLE, columns, null, null, null, "",
				ROW_POSTDATE + " desc");

		int toRow = fromRow + rowCount;
		cursor.moveToPosition(fromRow - 1);
		String socialNetworkType;
		BubbleMessage message;
		while (cursor.moveToNext() && rowCount-- > 0) {
			socialNetworkType = cursor.getString(3);
			if (socialNetworkType.equals("twitter")) {
				message = new TwitterBubbleMessage(cursor.getString(1),
						cursor.getString(2), cursor.getString(4),
						new java.sql.Date(Long.parseLong(cursor.getString(0))),
						cursor.getString(3), cursor.getString(5));
			} else {
				message = new BubbleMessage(cursor.getString(1),
						cursor.getString(2), cursor.getString(4),
						new java.sql.Date(Long.parseLong(cursor.getString(0))),
						cursor.getString(3), cursor.getString(5));
			}
			messageList.add(message);

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

	BubbleMessage getMessage(String socialNetwork, String socialNetworkId) {

		db = DBHelper.getReadableDatabase();
		String[] columns = { ROW_POSTDATE, ROW_USERNAME, ROW_MESSAGE,
				ROW_SOCIAL_NETWORK, ROW_PROFILE_IMAGE_URL,
				ROW_SOCIAL_NETWORK_MESSAGE_ID };

		Cursor cursor = db.query(MESSAGE_TABLE, columns, null, null, null, "",
				ROW_POSTDATE + " desc");

		BubbleMessage message = null;
		String socialNetworkType;
		if (cursor.moveToNext()) {
			socialNetworkType = cursor.getString(3);
			if (socialNetworkType.equals("twitter")) {
				message = new TwitterBubbleMessage(cursor.getString(1),
						cursor.getString(2), cursor.getString(4),
						new java.sql.Date(Long.parseLong(cursor.getString(0))),
						cursor.getString(3), cursor.getString(5));
			} else {
				message = new BubbleMessage(cursor.getString(1),
						cursor.getString(2), cursor.getString(4),
						new java.sql.Date(Long.parseLong(cursor.getString(0))),
						cursor.getString(3), cursor.getString(5));
			}
		}
		db.close();
		db = null;

		return message;
	}

	public ArrayList<BubbleMessage> getMessageListByKeyword(int rowCount,
			int fromRow, String keyword) {
		ArrayList<BubbleMessage> messageList = new ArrayList<BubbleMessage>();

		db = DBHelper.getReadableDatabase();
		String[] columns = { ROW_POSTDATE, ROW_USERNAME, ROW_MESSAGE,
				ROW_SOCIAL_NETWORK, ROW_PROFILE_IMAGE_URL,
				ROW_SOCIAL_NETWORK_MESSAGE_ID };

		Cursor cursor = db.query(MESSAGE_TABLE, columns, ROW_MESSAGE
				+ " like ?", new String[] { "%" + keyword + "%" }, null, "",
				ROW_POSTDATE + " desc");

		int toRow = fromRow + rowCount;
		String socialNetworkType;
		BubbleMessage message;
		while (cursor.moveToNext() && rowCount-- > 0) {

			socialNetworkType = cursor.getString(3);
			if (socialNetworkType.equals("twitter")) {
				message = new TwitterBubbleMessage(cursor.getString(1),
						cursor.getString(2), cursor.getString(4),
						new java.sql.Date(Long.parseLong(cursor.getString(0))),
						cursor.getString(3), cursor.getString(5));
			} else {
				message = new BubbleMessage(cursor.getString(1),
						cursor.getString(2), cursor.getString(4),
						new java.sql.Date(Long.parseLong(cursor.getString(0))),
						cursor.getString(3), cursor.getString(5));
			}
			messageList.add(message);

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
}
