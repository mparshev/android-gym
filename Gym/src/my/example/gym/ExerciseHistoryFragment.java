package my.example.gym;

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExerciseHistoryFragment extends ListFragment  implements LoaderCallbacks<Cursor> {

	static final int LOADER_ID = 1;

	ExerciseHistoryAdapter mAdapter;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		setHasOptionsMenu(true);
		
		setEmptyText(getResources().getString(R.string.empty_list));
		
		mAdapter = new ExerciseHistoryAdapter(getActivity(), null, getArguments());
		
		setListAdapter(mAdapter);

		getLoaderManager().initLoader(LOADER_ID, getArguments(), this);

		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.history, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), GymDb.TRAINING_TOTALS._URI, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		mAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
	}

	public class ExerciseHistoryAdapter extends CursorAdapter {
		
		String mSelection = GymDb.TRAINING.TRAINING_ID + "=" + "?";
		String[] mSelectionArgs = new String[] { "" };

		public ExerciseHistoryAdapter(Context context, Cursor c, Bundle args) {
			super(context, c, true);
			if(args != null && args.containsKey(GymDb.ARGS.EXERCISE_ID)) {
				mSelection += " and " + GymDb.TRAINING.EXERCISE_ID + "=" + "?";
				mSelectionArgs = new String[] { "", "" + args.getLong(GymDb.ARGS.EXERCISE_ID)};
			}
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return LayoutInflater.from(context).inflate(R.layout.exercise_history_item, parent, false);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			((TextView)view.findViewById(R.id.textDate)).setText(
					DateFormat.getDateFormat(context).format(
							GymDb.getLong(cursor, GymDb.TRAINING.TRAINING_ID)));
			mSelectionArgs[0] = "" + GymDb.getLong(cursor, GymDb.TRAINING.TRAINING_ID);
			populateListSets(context,
					(LinearLayout)view.findViewById(R.id.listSets),
					context.getContentResolver().query(GymDb.TRAINING_SETS._URI, 
							null, mSelection, mSelectionArgs, null));
			
		}
		
		private void populateListSets(Context context, LinearLayout list, Cursor cursor) {
			list.removeAllViews();
			if(cursor == null) return;
			while(cursor.moveToNext()) {
				View view = LayoutInflater.from(context)
						.inflate(R.layout.training_set_item, list, false);
				((TextView)view.findViewById(R.id.textExercise)).setText(
						GymDb.getString(cursor, GymDb.EXERCISE.EXERCISE));
				((TextView)view.findViewById(R.id.textSet)).setText(
						GymDb.getReps(context, GymDb.getLong(cursor, GymDb.TRAINING.SET_ID)));
				list.addView(view);
			}
			cursor.close();
		}
	}
}
