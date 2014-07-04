package my.example.gym;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
	
	Long mTrainingId = null;
	
	Timer mTimer = null;
	String mTitle = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mTitle = "" + getTitle();
		
		if(savedInstanceState != null && savedInstanceState.containsKey(GymDb.ARGS.TRAINING_ID)) {
			//mTrainingId = savedInstanceState.getLong(GymDb.ARGS.TRAINING_ID);
			//TODO: clear fragments here
			showTraining(savedInstanceState.getLong(GymDb.ARGS.TRAINING_ID));
		} else {
			showTraining(null);
		}
		
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if(mTrainingId != null) 
			outState.putLong(GymDb.ARGS.TRAINING_ID, mTrainingId);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_start:
				if(mTrainingId == null) {
					showTraining(GymDb.createId());
				}
				return true;
			case R.id.action_finish:
				showTraining(null);
				return true;
			case R.id.action_history:
				showTraining(null);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		
		if(getFragmentManager().getBackStackEntryCount()==0 && mTrainingId != null) {
			(new AlertDialog.Builder(this))
				.setMessage(R.string.exit_message)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						showTraining(null);
						//MainActivity.super.onBackPressed();
					}
				})
				.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.create()
				.show();
				
		} else {
			super.onBackPressed();
		}
	}	

	public void showTraining(Long id) {
		Fragment fragment;

		mTrainingId = id;

		if(mTimer != null) mTimer.cancel();
		setTitle(mTitle);
		
		if(mTrainingId != null) {
			fragment = new TrainingFragment();
			Bundle args = new Bundle();
			args.putLong(GymDb.ARGS.TRAINING_ID, mTrainingId);
			fragment.setArguments(args);
			
			mTimer = new Timer();
			mTimer.schedule(new TimerTask() {
				
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							
							if(mTrainingId != null) {
								long millis = System.currentTimeMillis() - mTrainingId;
								long ss = millis / 1000;
								long mm = ss / 60; ss = ss % 60;
								long hh = mm / 60; mm = mm % 60;
								if(hh < 2) setTitle(String.format("%d:%d:%d", hh, mm, ss));
							}
							
						}
					});
				}
			}, 1000l, 1000l);
			
		} else {
			fragment = new HistoryFragment();
		}

		getFragmentManager().beginTransaction()
			.replace(R.id.content_frame, fragment)
			.disallowAddToBackStack()
			.commit();
		
	}
}
