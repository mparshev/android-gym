package my.example.gym;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

public class TrainingFormFragment extends Fragment {
	
	long mTrainingId;
	long mExerciseId;
	long mSetId;

	TextView mTextGroup, mTextExercise, mTextLabelWeight, mTextLabelReps;
	NumberPicker mNumberPickerWeight, mNumberPickerReps;
	
	ListView mListReps;
	TrainingSetAdapter mAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.training_form, container, false);
		
		setHasOptionsMenu(true);
		
		mTextGroup = (TextView)view.findViewById(R.id.textGroup);
		mTextExercise = (TextView)view.findViewById(R.id.textSetExercise);
		mTextLabelWeight = (TextView)view.findViewById(R.id.textLabelWeight);
		mTextLabelReps = (TextView)view.findViewById(R.id.textLabelReps);
		
		mNumberPickerWeight = (NumberPicker)view.findViewById(R.id.numberPickerWeight);
		mNumberPickerWeight.setMinValue(0);
		mNumberPickerWeight.setMaxValue(999);
		
		mNumberPickerReps = (NumberPicker)view.findViewById(R.id.numberPickerReps);
		mNumberPickerReps.setMinValue(0);
		mNumberPickerReps.setMaxValue(999);
		
		mTrainingId = getArguments().getLong(GymDb.ARGS.TRAINING_ID);
		mExerciseId = getArguments().getLong(GymDb.ARGS.EXERCISE_ID);
		mSetId = getArguments().getLong(GymDb.ARGS.SET_ID); 

		Cursor cursor = getActivity().getContentResolver().query(
				ContentUris.withAppendedId(GymDb.EXERCISE._URI, mExerciseId),
				null, null, null, null);
		if(cursor != null) {
			if(cursor.moveToFirst()) {
				mTextGroup.setText(getResources().getStringArray(R.array.exercise_group)
						[GymDb.getInt(cursor, GymDb.EXERCISE.GROUP)]);
				mTextExercise.setText(GymDb.getString(cursor, GymDb.EXERCISE.EXERCISE));
				mTextLabelWeight.setText(getResources().getString(R.string.label_weight)
						+ " [" + getResources().getStringArray(R.array.weight_units)
						[GymDb.getInt(cursor, GymDb.EXERCISE.WEIGHT_UNIT)] + "]");
				mTextLabelReps.setText(getResources().getString(R.string.label_reps)
						+ " [" + getResources().getStringArray(R.array.reps_units)
						[GymDb.getInt(cursor, GymDb.EXERCISE.REPS_UNIT)] + "]");
				mNumberPickerWeight.setValue(GymDb.getInt(cursor, GymDb.EXERCISE.WORK_WEIGHT));
				mNumberPickerReps.setValue(GymDb.getInt(cursor, GymDb.EXERCISE.WORK_REPS));
			}
			cursor.close();
		}
		
		mListReps = (ListView)view.findViewById(R.id.listReps);

		cursor = getActivity().getContentResolver().query(ContentUris.withAppendedId(GymDb.TRAINING_REPS._URI, mSetId),null, null, null, null); 
		mAdapter = new TrainingSetAdapter(getActivity(), cursor);
		mListReps.setAdapter(mAdapter);
		
		((ImageButton)view.findViewById(R.id.ibActionNew)).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ContentValues values = new ContentValues();
				values.put(GymDb.TRAINING.TRAINING_ID, mTrainingId);
				values.put(GymDb.TRAINING.SET_ID, mSetId);
				values.put(GymDb.TRAINING.EXERCISE_ID, mExerciseId);
				values.put(GymDb.TRAINING.WEIGHT, mNumberPickerWeight.getValue());
				values.put(GymDb.TRAINING.REPS, mNumberPickerReps.getValue());
				getActivity().getContentResolver().insert(GymDb.TRAINING._URI, values);
				values.clear();
				values.put(GymDb.EXERCISE.WORK_WEIGHT, mNumberPickerWeight.getValue());
				values.put(GymDb.EXERCISE.WORK_REPS, mNumberPickerReps.getValue());
				getActivity().getContentResolver().update(
						ContentUris.withAppendedId(GymDb.EXERCISE._URI, mExerciseId), values, null, null);
				mAdapter.notifyDataSetChanged();
				
			}
		});
		
		return view;
	
	}
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.training_form, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_edit:
			Fragment fragment = new ExerciseFormFragment();
			fragment.setArguments(getArguments());
			getActivity().getFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment)
				.addToBackStack(null)
				.commit();
			return true;
		case R.id.action_accept:

			FragmentManager fm = getActivity().getFragmentManager();
			int id = fm.getBackStackEntryAt(0).getId();
			fm.popBackStackImmediate(id, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			
			// configChanges in AndroidManifest solves the problem of overlapping fragments

			return true;
		case R.id.action_excs_history:
			fragment = new ExerciseHistoryFragment();
			Bundle args = new Bundle();
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


	public class TrainingSetAdapter extends CursorAdapter {
		
		public TrainingSetAdapter(Context context, Cursor c) {
			super(context, c, true);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return LayoutInflater.from(context).inflate(R.layout.training_form_item, parent, false);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			((TextView)view.findViewById(R.id.textWeight))
				.setText("" + GymDb.getInt(cursor, GymDb.TRAINING.WEIGHT));
			((TextView)view.findViewById(R.id.textReps))
				.setText("" + GymDb.getInt(cursor, GymDb.TRAINING.REPS));
			ImageButton ibDiscard = (ImageButton)view.findViewById(R.id.ibDiscard);
			ibDiscard.setTag(GymDb.getLong(cursor, GymDb.TRAINING._ID));
			ibDiscard.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					getActivity().getContentResolver().delete(
							Uri.withAppendedPath(GymDb.TRAINING._URI, "" + v.getTag()),
							null, null);
				}
			});
		}
		
	}

}
