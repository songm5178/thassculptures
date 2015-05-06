package edu.rosehulman.thassculptures;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	protected static final int REQUEST_CODE_CHANGE_BUTTON = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button toursButton = (Button) findViewById(R.id.button_tours);
		toursButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ToursMapActivity.class);
				startActivityForResult(intent, REQUEST_CODE_CHANGE_BUTTON);

			}
		});
		Button mapButton = (Button) findViewById(R.id.button_map);
		mapButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ToursMapActivity.class);
				startActivityForResult(intent, REQUEST_CODE_CHANGE_BUTTON);

			}
		});

		Button galleryButton = (Button) findViewById(R.id.button_gallery);
		galleryButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), GalleryActivity.class);
				startActivityForResult(intent, REQUEST_CODE_CHANGE_BUTTON);

			}
		});
	}

	@Override
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
