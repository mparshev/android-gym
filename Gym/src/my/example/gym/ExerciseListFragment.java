package my.example.gym;

import android.app.Fragment;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class ExerciseListFragment extends Fragment {
	
	ExpandableListView mExerciseList;
	ExerciseListAdapter mExerciseListAdapter;
	
	int mGroupPosition = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.exercise_list, container, false);
		
		setHasOptionsMenu(true);
		
		mExerciseList = (ExpandableListView)view;
		mExerciseListAdapter = new ExerciseListAdapter(getActivity());
		
		mExerciseList.setAdapter(mExerciseListAdapter);

		mExerciseList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Fragment fragment = new TrainingFormFragment();
				Bundle args = new Bundle();
				args.putAll(getArguments());
				args.putLong(GymDb.ARGS.EXERCISE_ID, id);
				fragment.setArguments(args);
				getActivity().getFragmentManager().beginTransaction()
					.replace(R.id.content_frame, fragment)
					.addToBackStack(null)
					.commit();
				return false;
			}
		
		});
		
		mExerciseList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				mGroupPosition = groupPosition;
				return false;
			}
		});
		getActivity().getContentResolver()
			.registerContentObserver(GymDb.CONTENT_URI, true, 
				new ContentObserver(null) {

					@Override
					public void onChange(boolean selfChange) {
						mExerciseListAdapter.notifyDataSetChanged();
						super.onChange(selfChange);
					}
				
				});
		
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.exercise_list, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_add:
			Fragment fragment = new ExerciseFormFragment();
			Bundle args = new Bundle();
			args.putAll(getArguments());
			args.putInt(GymDb.ARGS.GROUP_ID, mGroupPosition);
			fragment.setArguments(args);
			getActivity().getFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment)
				.addToBackStack(null)
				.commit();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public class ExerciseListAdapter extends BaseExpandableListAdapter {
		
		Context mContext;
		String[] groups;
		
		Cursor mCursor;
		int mGroup = -1;

		public ExerciseListAdapter(Context context) {
			mContext = context;
			groups = getResources().getStringArray(R.array.exercise_group);
		}
		
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			Cursor cursor = getChildCursor(groupPosition);
			if (cursor.moveToPosition(childPosition)) {
				return GymDb.getString(cursor, GymDb.EXERCISE.EXERCISE);
			}
			return null;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			Cursor cursor = getChildCursor(groupPosition);
			if (cursor.moveToPosition(childPosition)) {
				return GymDb.getLong(cursor, GymDb.EXERCISE._ID);
			}
			return 0;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			View view = (convertView == null) ?
					LayoutInflater.from(mContext).inflate(R.layout.exercise_child, parent, false) 
					: convertView;
					
			((TextView)view).setText("" + getChild(groupPosition, childPosition));
			return view;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return getChildCursor(groupPosition).getCount();
		}

		private Cursor getChildCursor(int groupPosition) {
			if(groupPosition == mGroup) return mCursor;
			mGroup = groupPosition;	
			mCursor = mContext.getContentResolver().query(
						GymDb.EXERCISE._URI, null,
						GymDb.EXERCISE.GROUP + " = " + "?",
						new String[] { "" + groupPosition }, null);
			return mCursor;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return groups[groupPosition];
		}

		@Override
		public int getGroupCount() {
			return groups.length;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			View view = (convertView == null) ?
					LayoutInflater.from(mContext).inflate(R.layout.exercise_group, parent, false) 
					: convertView;
			((TextView) view).setText(groups[groupPosition]);
			return view;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}
	
}
