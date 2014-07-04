package my.example.gym;

import java.util.Date;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

public class GymDb extends ContentProvider {

	public final static String AUTHORITY = "my.example.gym";
	public final static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	
	public static final class ARGS {
		public static final String TRAINING_ID = "training_id";
		public static final String EXERCISE_ID = "exercise_id";
		public static final String SET_ID = "set_id";
		public static final String GROUP_ID = "group_id";
		public static final String START_TIME = "start_time";
	}

	public static final class TRAINING {
		public static final String _TABLE = "training";
		
		public static final Uri _URI = Uri.withAppendedPath(CONTENT_URI, _TABLE);
		
		public static final String _ID = BaseColumns._ID;
		public static final String TRAINING_ID = "training_id";
		public static final String SET_ID = "set_id";	// Set means exercise or movement
		public static final String EXERCISE_ID = "exercise_id";
		public static final String WEIGHT = "weight";
		public static final String REPS = "reps";
		
		public static final String _CREATE_SQL = "create table " + _TABLE + "(" 
				+ _ID 			+ " integer primary key autoincrement, "
				+ TRAINING_ID	+ " integer, "
				+ SET_ID		+ " integer, "
				+ EXERCISE_ID	+ " integer, "
				+ WEIGHT 		+ " integer, "
				+ REPS 			+ " integer )";
	}
	
	public static final class EXERCISE {
		public static final String _TABLE = "exercise";

		public static final Uri _URI = Uri.withAppendedPath(CONTENT_URI, _TABLE);
		
		public static final String _ID = BaseColumns._ID;
		public static final String EXERCISE = "exercise";
		public static final String GROUP = "exercise_group";
		public static final String WORK_WEIGHT = "work_weight";
		public static final String WEIGHT_UNIT = "weight_unit";
		public static final String WORK_REPS = "work_reps";
		public static final String REPS_UNIT = "reps_unit";
		
		public static final String _CREATE_SQL = "create table " + _TABLE + " ("
				+ _ID 			+ " integer primary key autoincrement, "
				+ EXERCISE 		+ " text, "
				+ GROUP			+ " integer, "
				+ WORK_WEIGHT	+ " integer, "
				+ WEIGHT_UNIT 	+ " integer, "
				+ WORK_REPS 	+ " integer, " 
				+ REPS_UNIT		+ " integer )";

	}
	
	public static final class TRAINING_TOTALS {
		public static final String _TABLE = "training_ids";
		public static final Uri _URI = Uri.withAppendedPath(CONTENT_URI, _TABLE);
		public static final String SET_COUNT = "set_count"; 
		public static final String REP_COUNT = "rep_count"; 
	}

	public static final class TRAINING_SETS {
		public static final String _TABLE = "training_sets";
		public static final Uri _URI = Uri.withAppendedPath(CONTENT_URI, _TABLE);
	}

	public static final class TRAINING_REPS {
		public static final String _TABLE = "training_reps";
		public static final Uri _URI = Uri.withAppendedPath(CONTENT_URI, _TABLE);
	}
	
	private static final String TRAINING_VIEW = TRAINING._TABLE + " inner join " + EXERCISE._TABLE 
			+ " on " + TRAINING._TABLE + "." + TRAINING.EXERCISE_ID 
			+ " = " + EXERCISE._TABLE + "." + EXERCISE._ID;
	
	private static final UriMatcher sUriMatcher;
	
	public static final int TRAINING_QUERY = 1;
	public static final int TRAINING_ROW_QUERY = 2;
	public static final int EXERCISE_QUERY = 3;
	public static final int EXERCISE_ROW_QUERY = 4;
	
	public static final int TRAINING_TOTALS_QUERY = 5;
	public static final int TRAINING_REPS_QUERY = 6;
	public static final int TRAINING_SETS_QUERY = 7;
	public static final int TRAINING_SETS_ROW_QUERY = 8;
	
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, TRAINING._TABLE, TRAINING_QUERY);
		sUriMatcher.addURI(AUTHORITY, TRAINING._TABLE + "/#", TRAINING_ROW_QUERY);
		sUriMatcher.addURI(AUTHORITY, EXERCISE._TABLE, EXERCISE_QUERY);
		sUriMatcher.addURI(AUTHORITY, EXERCISE._TABLE + "/#", EXERCISE_ROW_QUERY);
		sUriMatcher.addURI(AUTHORITY, TRAINING_TOTALS._TABLE, TRAINING_TOTALS_QUERY);
		sUriMatcher.addURI(AUTHORITY, TRAINING_SETS._TABLE, TRAINING_SETS_QUERY);
		sUriMatcher.addURI(AUTHORITY, TRAINING_SETS._TABLE + "/#", TRAINING_SETS_ROW_QUERY);
		sUriMatcher.addURI(AUTHORITY, TRAINING_REPS._TABLE + "/#", TRAINING_REPS_QUERY);
	}
	
	private static class DataHelper extends SQLiteOpenHelper {

		private static final String DATABASE_NAME = "gym";
		private static final int DATABASE_VERSION = 1;
		
		public DataHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(TRAINING._CREATE_SQL);
			db.execSQL(EXERCISE._CREATE_SQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("drop table if exists " + TRAINING._TABLE);
			db.execSQL("drop table if exists " + EXERCISE._TABLE);
			onCreate(db);
		}
		
	}
	
	private DataHelper mDataHelper;
	
	@Override
	public boolean onCreate() {
		mDataHelper = new DataHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		switch(sUriMatcher.match(uri)) {
		case TRAINING_QUERY: 
			Cursor cursor = mDataHelper.getReadableDatabase().query(TRAINING._TABLE, 
					projection, selection, selectionArgs, null, null, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
			return cursor;
		case TRAINING_ROW_QUERY:
			return mDataHelper.getReadableDatabase().query(TRAINING._TABLE, 
					projection, 
					BaseColumns._ID + " = " + "?", 
					new String[] { uri.getLastPathSegment() }, 
					null, null, null);
		case EXERCISE_QUERY: 
			cursor = mDataHelper.getReadableDatabase().query(EXERCISE._TABLE, 
					projection, selection, selectionArgs, null, null, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
			return cursor;
		case EXERCISE_ROW_QUERY:
			return mDataHelper.getReadableDatabase().query(EXERCISE._TABLE, 
					projection, 
					BaseColumns._ID + " = " + "?", 
					new String[] { uri.getLastPathSegment() }, 
					null, null, null);
		case TRAINING_TOTALS_QUERY:
//			cursor = mDataHelper.getReadableDatabase().query(true, 
//					TRAINING._TABLE, 
//					new String[] { 
//						TRAINING.TRAINING_ID + " as " + BaseColumns._ID,
//						TRAINING.TRAINING_ID }, 
//					null, null, null, null, TRAINING.TRAINING_ID + " DESC", null);
			cursor = mDataHelper.getReadableDatabase().query(TRAINING._TABLE, 
					new String[] { 
						TRAINING.TRAINING_ID + " as " + BaseColumns._ID,
						TRAINING.TRAINING_ID, 
						"count(distinct " + TRAINING.SET_ID + ") as " + TRAINING_TOTALS.SET_COUNT,
						"count(*) as " + TRAINING_TOTALS.REP_COUNT
						}, 
					null, 
					null, 
					TRAINING.TRAINING_ID, 
					null, TRAINING.TRAINING_ID + " DESC", null);
			cursor.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
			return cursor;
		case TRAINING_SETS_QUERY:	// For history
			//Log.d("query", "" + selection + " " + selectionArgs);
			cursor = mDataHelper.getReadableDatabase().query(true, 
					TRAINING_VIEW, 
					new String[] { 
						TRAINING.SET_ID + " as " + BaseColumns._ID,
						TRAINING.SET_ID,
						TRAINING.EXERCISE_ID, 
						EXERCISE.EXERCISE }, 
					selection, 
					selectionArgs, 
					null, null, null, null);
			cursor.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
			return cursor;
		case TRAINING_SETS_ROW_QUERY:
			//TODO Check this first, try rowQuery if it does not work!!!
			cursor = mDataHelper.getReadableDatabase().query(true, 
					TRAINING_VIEW, 
					new String[] { 
						TRAINING.SET_ID + " as " + BaseColumns._ID,
						TRAINING.SET_ID,
						TRAINING.EXERCISE_ID, 
						EXERCISE.EXERCISE }, 
					TRAINING.TRAINING_ID + "=" + "?", 
					new String[] { uri.getLastPathSegment() }, 
					null, null, null, null);
			cursor.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
			return cursor;
		case TRAINING_REPS_QUERY:
			cursor = mDataHelper.getReadableDatabase().query(true, 
					TRAINING._TABLE,
					null, 
					TRAINING.SET_ID + "=" + "?", 
					new String[] { uri.getLastPathSegment() }, 
					null, null, null, null);
			cursor.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
			return cursor;
		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		switch(sUriMatcher.match(uri)) {
		case TRAINING_QUERY:
			long id = mDataHelper.getWritableDatabase().insert(TRAINING._TABLE, null, values);
			getContext().getContentResolver().notifyChange(CONTENT_URI, null);
			return ContentUris.withAppendedId(uri, id);
		case EXERCISE_QUERY:
			id = mDataHelper.getWritableDatabase().insert(EXERCISE._TABLE, null, values);
			getContext().getContentResolver().notifyChange(CONTENT_URI, null);
			return ContentUris.withAppendedId(uri, id);
		}
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		switch(sUriMatcher.match(uri)) {
		case TRAINING_ROW_QUERY:
			int rows = mDataHelper.getWritableDatabase().update(TRAINING._TABLE, values, 
					BaseColumns._ID + " = " + uri.getLastPathSegment(), null);
			if(rows > 0) getContext().getContentResolver().notifyChange(CONTENT_URI, null);
			return rows;
		case EXERCISE_ROW_QUERY:
			rows = mDataHelper.getWritableDatabase().update(EXERCISE._TABLE, values, 
					BaseColumns._ID + " = " + uri.getLastPathSegment(), null);
			if(rows > 0) getContext().getContentResolver().notifyChange(CONTENT_URI, null);
			return rows;
		}
		return 0;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch(sUriMatcher.match(uri)) {
		case TRAINING_ROW_QUERY:
			int rows = mDataHelper.getWritableDatabase().delete(TRAINING._TABLE, 
					BaseColumns._ID + " = " + uri.getLastPathSegment(), null);
			if(rows > 0) getContext().getContentResolver().notifyChange(CONTENT_URI, null);
			return rows;
		case EXERCISE_ROW_QUERY:
			rows = mDataHelper.getWritableDatabase().delete(EXERCISE._TABLE, 
					BaseColumns._ID + " = " + uri.getLastPathSegment(), null);
			if(rows > 0) getContext().getContentResolver().notifyChange(CONTENT_URI, null);
			return rows;
		}
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	public static String getString(Cursor cursor, String columnName) {
		return cursor.getString(cursor.getColumnIndex(columnName));
	}

	public static int getInt(Cursor cursor, String columnName) {
		return cursor.getInt(cursor.getColumnIndex(columnName));
	}
	
	public static long getLong(Cursor cursor, String columnName) {
		return cursor.getLong(cursor.getColumnIndex(columnName));
	}
	
	public static long createId() {
		return new Date().getTime();
	}
	
	public static int parseInt(String string) {
		if(string == null || "".equals(string)) return 0;
		return Integer.parseInt(string);
	}

	public static String getReps(Context context, long setId) {
		String reps = "";
		Cursor cursor = context.getContentResolver().query(
				ContentUris.withAppendedId(GymDb.TRAINING_REPS._URI, setId), 
				null, null, null, null);
		if(cursor != null) {
			while(cursor.moveToNext()) {
				int weight = GymDb.getInt(cursor, GymDb.TRAINING.WEIGHT);
				reps += (weight == 0 ? " " : " " + weight + "*") + 
						GymDb.getInt(cursor, GymDb.TRAINING.REPS);
			}
			cursor.close();
		}
		return reps;
	}
	
	
}
