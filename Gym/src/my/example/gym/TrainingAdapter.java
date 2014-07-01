package my.example.gym;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class TrainingAdapter extends CursorAdapter {

	public TrainingAdapter(Context context, Cursor c) {
		super(context, c, true);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return LayoutInflater.from(context).inflate(R.layout.training_set_item, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		((TextView)view.findViewById(R.id.textExercise)).setText(
				GymDb.getString(cursor, GymDb.EXERCISE.EXERCISE));
		((TextView)view.findViewById(R.id.textSet)).setText(
				GymDb.getReps(context, GymDb.getLong(cursor, GymDb.TRAINING.SET_ID)));
		view.setTag(GymDb.getLong(cursor, GymDb.TRAINING.EXERCISE_ID));
		
	}

}

