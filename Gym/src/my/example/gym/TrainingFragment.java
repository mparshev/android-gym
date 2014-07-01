package my.example.gym;

import android.app.Fragment;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class TrainingFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {

	static final int LOADER_ID = 1;

	TrainingAdapter mAdapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		setHasOptionsMenu(true);

		setEmptyText(getResources().getString(R.string.empty_list));
		
		mAdapter = new TrainingAdapter(getActivity(), null);
		setListAdapter(mAdapter);

		getLoaderManager().initLoader(LOADER_ID, getArguments(), this);

		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.training, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_new:
				
			Fragment fragment = new ExerciseListFragment();
			Bundle args = new Bundle();
			args.putAll(getArguments());
			args.putLong(GymDb.ARGS.SET_ID, GymDb.createId());
			fragment.setArguments(args);
			getActivity().getFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment)
				.addToBackStack(null)
				.commit();
			return true; 
		case R.id.action_history:
			fragment = new HistoryFragment();
			getActivity().getFragmentManager().beginTransaction()
			.replace(R.id.content_frame, fragment)
			.addToBackStack(null)
			.commit();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		//super.onListItemClick(l, v, position, id);
		Fragment f = new TrainingFormFragment();
		Bundle args = new Bundle();
		args.putAll(getArguments());
		args.putLong(GymDb.ARGS.SET_ID, id);
		args.putLong(GymDb.ARGS.EXERCISE_ID, Long.valueOf(""+v.getTag()));
		f.setArguments(args);
		getActivity().getFragmentManager().beginTransaction()
			.replace(R.id.content_frame, f)
			.addToBackStack(null)
			.commit();

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), 
				ContentUris.withAppendedId(GymDb.TRAINING_SETS._URI, args.getLong(GymDb.TRAINING.TRAINING_ID)), 
				null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		mAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
	}

}
