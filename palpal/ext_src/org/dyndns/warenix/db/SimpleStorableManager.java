package org.dyndns.warenix.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SimpleStorableManager {

	static final String DATABASE_NAME = "simpleStorable";
	static final int DATABASE_VERSION = 1;

	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	Context context;

	// table message
	static final String SIMPLE_STORABLE_TABLE = "simpleStorable";
	static final String ROW_ID = "id";
	static final String ROW_KEY = "key";
	static final String ROW_VALUE = "value";
	static final String ROW_STORABLE_TYPE = "type";

	static String CREATE_SIMPLE_STORABLE_TABLE = String
			.format("CREATE TABLE %s (%s integer primary key autoincrement not null, %s text, %s text, %s text)",
					SIMPLE_STORABLE_TABLE, ROW_ID, ROW_KEY, ROW_VALUE,
					ROW_STORABLE_TYPE);

	public SimpleStorableManager(Context context) {
		this.context = context;
		DBHelper = new DatabaseHelper(context);
	}

	/**
	 * insert storable into database
	 * 
	 * @param storable
	 * @return rowId
	 */
	public long insertItem(SimpleStorable storable) {
		db = DBHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(ROW_KEY, storable.key);
		initialValues.put(ROW_VALUE, storable.value);
		initialValues.put(ROW_STORABLE_TYPE, storable.type);
		long rowId = db.insert(SIMPLE_STORABLE_TABLE, null, initialValues);
		db.close();
		db = null;
		return rowId;
	}

	public SimpleStorable getItemByKey(String type, String key) {
		Log.d("warenix",
				String.format("getItemByKey type[%s] key[%s]", type, key));
		db = DBHelper.getReadableDatabase();
		String[] columns = { ROW_ID, ROW_STORABLE_TYPE, ROW_KEY, ROW_VALUE };

		Cursor cursor = db.query(SIMPLE_STORABLE_TABLE, columns,
				ROW_STORABLE_TYPE + "=?" + " AND " + ROW_KEY + "=?",
				new String[] { type, key }, null, "", null);

		SimpleStorable item = null;

		while (cursor.moveToNext()) {

			item = new SimpleStorable(cursor.getInt(0), cursor.getString(1),
					cursor.getString(2), cursor.getString(3));
		}
		db.close();
		db = null;

		return item;
	}

	/**
	 * retrieve all message
	 * 
	 * @return
	 */
	public ArrayList<SimpleStorable> getSimpleStorableList(String type) {
		return getSimpleStorableList(type, 99999, -1);
	}

	/**
	 * 
	 * @param rowCount
	 * @param fromRow
	 *            -1 means the beginning
	 * @return
	 */
	public ArrayList<SimpleStorable> getSimpleStorableList(String type,
			int rowCount, int fromRow) {
		Log.d("warenix", String.format(
				"getSimpleStorableList type[%s] count[%d]", type, rowCount));
		ArrayList<SimpleStorable> messageList = new ArrayList<SimpleStorable>();

		db = DBHelper.getReadableDatabase();
		String[] columns = { ROW_ID, ROW_STORABLE_TYPE, ROW_KEY, ROW_VALUE };

		Cursor cursor = db.query(SIMPLE_STORABLE_TABLE, columns,
				ROW_STORABLE_TYPE + "=?", new String[] { type }, null, "",
				ROW_ID + " DESC");

		int toRow = fromRow + rowCount;

		while (cursor.moveToNext() && rowCount-- > 0) {

			SimpleStorable message = new SimpleStorable(cursor.getInt(0),
					cursor.getString(1), cursor.getString(2),
					cursor.getString(3));
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

	public int updateItemById(SimpleStorable storable) {
		db = DBHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(ROW_ID, storable.id);
		initialValues.put(ROW_KEY, storable.key);
		initialValues.put(ROW_VALUE, storable.value);
		initialValues.put(ROW_STORABLE_TYPE, storable.type);
		int affectedRows = db.update(SIMPLE_STORABLE_TABLE, initialValues,
				ROW_ID + "=?", new String[] { "" + storable.id });
		db.close();
		db = null;

		return affectedRows;
	}

	public int updateItemByKey(SimpleStorable storable) {
		db = DBHelper.getWritableDatabase();
		ContentValues initialValues = new ContentValues();
		initialValues.put(ROW_ID, storable.id);
		initialValues.put(ROW_KEY, storable.key);
		initialValues.put(ROW_VALUE, storable.value);
		initialValues.put(ROW_STORABLE_TYPE, storable.type);
		int affectedRows = db.update(SIMPLE_STORABLE_TABLE, initialValues,
				ROW_KEY + "=?", new String[] { "" + storable.key });
		db.close();
		db = null;

		return affectedRows;
	}

	public int deleteItemByKey(SimpleStorable storable) {
		db = DBHelper.getWritableDatabase();
		int affectedRows = db.delete(SIMPLE_STORABLE_TABLE, ROW_KEY + "=?",
				new String[] { "" + storable.key });
		db.close();
		db = null;

		return affectedRows;
	}

	public int deleteItemById(SimpleStorable storable) {
		db = DBHelper.getWritableDatabase();
		int affectedRows = db.delete(SIMPLE_STORABLE_TABLE, ROW_ID + "=?",
				new String[] { "" + storable.id });
		db.close();
		db = null;

		return affectedRows;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i("palpal", "create db " + DATABASE_NAME);
			db.execSQL(CREATE_SIMPLE_STORABLE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("palpal", "Upgrading database from version " + oldVersion
					+ " to " + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + SIMPLE_STORABLE_TABLE);
			onCreate(db);
		}
	}
}
