package com.example.try3000;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageActivity extends Activity implements PointCollecterListener {
	
	private static final String PASSWORD_SET = "PASSWORD_SET";
	private static final int POINT_CLOSENESS = 40;
	private PointCollector pointCollector = new PointCollector();
	private Database db = new Database(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		
		addTouchListener();
		
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		Boolean passpointsSet = prefs.getBoolean(PASSWORD_SET, false);
		
		if(!passpointsSet){
			showSetPasspointPrompt();
		}
		
		pointCollector.setListener(this);
	}
	private void showSetPasspointPrompt(){
		AlertDialog.Builder builder = new Builder(this);
		
		builder.setPositiveButton("OK", new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
			
		builder.setTitle("Create Your Passpoint Sequence");
		builder.setMessage("Touch four points on the image to set the passpoints sequence.");
		
			
		
		AlertDialog dlg = builder.create();
		
		dlg.show();
		
		
	}
	
	private void addTouchListener(){
		
		ImageView image = (ImageView)findViewById(R.id.touch_image );
		
		image.setOnTouchListener(pointCollector);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.image, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	private void savePasspoints(final List<Point> points){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.storing_data);
		
		final AlertDialog dlg = builder.create();
		dlg.show();
		
		
		
		AsyncTask<Void, Void, Void>task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				db.storePoints(points);
				
				Log.d(MainActivity.DEBUGTAG, "points saved: " + points.size() );
				
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				
				SharedPreferences prefs =  getPreferences(MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean(PASSWORD_SET, true);
				editor.commit();
				pointCollector.clear();
				dlg.dismiss();
			}
			
			
		};
		
		task.execute();
		
	}
	
	private void verifyPasspoints(final List<Point> touchedPoints){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Checking passpoints...");
		
		final AlertDialog dlg = builder.create();
		dlg.show();
		
		AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>(){

			@Override
			protected Boolean doInBackground(Void... params) {
				
				List<Point> savedPoints = db.getPoints();
				
				Log.d(MainActivity.DEBUGTAG, "Loaded points: " + savedPoints.size());
				
				if (savedPoints.size() != PointCollector.NUM_POINTS
						|| touchedPoints.size() != PointCollector.NUM_POINTS){
					return false;
				}
				
				for(int i=0; i < PointCollector.NUM_POINTS; i++){
					Point savedPoint = savedPoints.get(i);
					Point touchedPoint = touchedPoints.get(i);
					
					int xDiff = savedPoint.x - touchedPoint.x;
					int yDiff = savedPoint.y - touchedPoint.y;
					Log.d(MainActivity.DEBUGTAG, "xDiff : " + xDiff);
					Log.d(MainActivity.DEBUGTAG, "yDiff : " + yDiff);
					
					int distSquared = xDiff*xDiff + yDiff*yDiff;
					
					Log.d(MainActivity.DEBUGTAG, "Distance squared: " + distSquared);
					if(distSquared > POINT_CLOSENESS*POINT_CLOSENESS){
						return false;
					}
					
				}
				return true;
			}

			@Override
			protected void onPostExecute(Boolean pass) {
				
				dlg.dismiss();
				pointCollector.clear();
				if(pass == true){
					Intent i = new Intent(ImageActivity.this,MainActivity.class);
					startActivity(i);
				}
				else {
					Toast.makeText(ImageActivity.this, "Access Denied", Toast.LENGTH_LONG).show();
				}
				
			}
			
			
		};
		task.execute();
		
	}
	@Override
	public void pointsCollected(final List<Point> points) {
		//Log.d(MainActivity.DEBUGTAG, "Collected pointes: "+ points.size());
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		Boolean passpointsSet = prefs.getBoolean(PASSWORD_SET, false);
		
		if(!passpointsSet){
			Log.d(MainActivity.DEBUGTAG, "Saving passpoints...");
			savePasspoints(points);
		}
		else{
			Log.d(MainActivity.DEBUGTAG, "Verifying passpoints...");
			verifyPasspoints(points);
		}
		
		
	
		
	}
}
