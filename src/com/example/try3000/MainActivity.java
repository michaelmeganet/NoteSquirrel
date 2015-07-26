package com.example.try3000;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
	
	public static final String DEBUGTAG = "JWP";
	public static final String TEXTFILE = "notesquirrel.txt";
	public static final String FILESAVED = "FileSaved" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		addSaveButtonListener();

		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		boolean fileSaved = prefs.getBoolean(FILESAVED, false);

		if (fileSaved) {
			loadSaveFile();
		}
    }
    
    private void loadSaveFile(){
    	
    	try {
			FileInputStream fis = openFileInput(TEXTFILE);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new DataInputStream(fis)));
			
			EditText editText = (EditText) findViewById(R.id.text);
			
			String line;
			while((line = reader.readLine()) != null){
				editText.append(line);
				editText.append("\n");
			}
			
			fis.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.d(DEBUGTAG, "Unable to read file");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    private void addSaveButtonListener(){
    	Button saveBtn = (Button) findViewById(R.id.save);
    	
    	saveBtn.setOnClickListener(new View.OnClickListener() {
			
		

			@Override
			public void onClick(View v) {
				
				EditText editText = (EditText) findViewById(R.id.text);
				
				String text = editText.getText().toString();
				
				try {
				    FileOutputStream fos =	openFileOutput(TEXTFILE, Context.MODE_PRIVATE );
				    fos.write(text.getBytes());
				    fos.close();
				    
					SharedPreferences prefs = getPreferences(MODE_PRIVATE);
					SharedPreferences.Editor editor = prefs.edit();
					editor.putBoolean(FILESAVED, true);
					editor.commit();
					Toast.makeText(MainActivity.this,R.string.toast_save ,Toast.LENGTH_LONG).show();
					//toast_save
				} catch (Exception e) {
					Log.d(DEBUGTAG, "Unable to save file");
					Toast.makeText(MainActivity.this,R.string.toast_cant_save ,Toast.LENGTH_LONG).show();
				}
				

				
			}
		});
    	
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
}
