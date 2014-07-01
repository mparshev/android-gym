package my.example.gym;

import android.app.Fragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

public class ExerciseFormFragment extends Fragment {

	Spinner mSpinnerGroup;
	EditText mEditExercise;
	Spinner mSpinnerWeightUnit;
	Spinner mSpinnerRepsUnit;
	
	Long mExerciseId = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.exercise_form, container, false);

		setHasOptionsMenu(true);
		
		mSpinnerGroup = (Spinner)view.findViewById(R.id.spinnerGroup);
		mEditExercise = (EditText)view.findViewById(R.id.editExercise);
		mSpinnerWeightUnit = (Spinner)view.findViewById(R.id.spinnerWeightUnit);
		mSpinnerRepsUnit = (Spinner)view.findViewById(R.id.spinnerRepsUnit);
		
		Bundle args = getArguments();
		
		if(args == null) { 
			// Do nothing;
		} else if(args.containsKey(GymDb.ARGS.EXERCISE_ID)) {
			mExerciseId = getArguments().getLong(GymDb.ARGS.EXERCISE_ID);
			Cursor cursor = getActivity().getContentResolver().query(
					ContentUris.withAppendedId(GymDb.EXERCISE._URI, mExerciseId),
					null, null, null, null);
			if(cursor != null) {
				if(cursor.moveToFirst()) {
					mSpinnerGroup.setSelection(GymDb.getInt(cursor, GymDb.EXERCISE.GROUP));
					mEditExercise.setText(GymDb.getString(cursor, GymDb.EXERCISE.EXERCISE));
					mSpinnerWeightUnit.setSelection(GymDb.getInt(cursor, GymDb.EXERCISE.WEIGHT_UNIT));
					mSpinnerRepsUnit.setSelection(GymDb.getInt(cursor, GymDb.EXERCISE.REPS_UNIT));
				}
				cursor.close();
			}
		} else if(args.containsKey(GymDb.ARGS.GROUP_ID)) {
			mSpinnerGroup.setSelection(args.getInt(GymDb.ARGS.GROUP_ID));
		}
		
		
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.exercise_form, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_accept:
			ContentValues values = new ContentValues();
			values.put(GymDb.EXERCISE.GROUP, mSpinnerGroup.getSelectedItemPosition());
			values.put(GymDb.EXERCISE.EXERCISE, mEditExercise.getText().toString());
			values.put(GymDb.EXERCISE.WEIGHT_UNIT, mSpinnerWeightUnit.getSelectedItemPosition());
			values.put(GymDb.EXERCISE.REPS_UNIT, mSpinnerRepsUnit.getSelectedItemPosition());
			if(mExerciseId == null) {
				mExerciseId = ContentUris.parseId(getActivity().getContentResolver()
						.insert(GymDb.EXERCISE._URI, values));
			} else {
				getActivity().getContentResolver().update(
						ContentUris.withAppendedId(GymDb.EXERCISE._URI, mExerciseId), 
						values, null, null);
			}
			Fragment fragment = new TrainingFormFragment();
			Bundle args = new Bundle();
			args.putAll(getArguments());
			args.putLong(GymDb.ARGS.EXERCISE_ID, mExerciseId);
			fragment.setArguments(args);
			getActivity().getFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment)
				.addToBackStack(null)
				.commit();
				
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	

}
