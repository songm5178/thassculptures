package edu.rosehulman.thassculptures;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.appspot.thassculptures.sculptures.Sculptures;
import com.appspot.thassculptures.sculptures.Sculptures.Sculpture.List;
import com.appspot.thassculptures.sculptures.model.Sculpture;
import com.appspot.thassculptures.sculptures.model.SculptureCollection;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;

public class GalleryActivity extends Activity {

	private Sculptures mService;
	private ListView mListView;
	public static java.util.List<Sculpture> mSculptures;
	public static final String GALLERY_SCULPTURE_ID = "GALLERY_SCULPTURE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);



		mListView = (ListView) findViewById(R.id.listView_sculptures_gallery);

		Sculptures.Builder builder = new Sculptures.Builder(AndroidHttp.newCompatibleTransport(),
				new GsonFactory(), null);
		mService = builder.build();

		update();
	}

	

	private void update() {
		//
		new QueryForSculpturesTask().execute();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gallery, menu);
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

	class QueryForSculpturesTask extends AsyncTask<Void, Void, SculptureCollection> {

		@Override
		protected SculptureCollection doInBackground(Void... arg0) {
			//
			SculptureCollection sculptures = null;

			try {
				List query = mService.sculpture().list();
				// query.setOrder
				query.setLimit(50L);
				sculptures = query.execute();
			} catch (IOException e) {
				Log.e("MIN", "Failed Loading" + e);
			}
			return sculptures;
		}

		@Override
		protected void onPostExecute(SculptureCollection result) {
			//
			super.onPostExecute(result);
			if (result == null) {
				Log.d("MIN", "Failed loading, result it null");
				return;
			}
			mSculptures = result.getItems();
			
			SculptureListAdapter adapter = new SculptureListAdapter(getApplicationContext(),
					result.getItems());
			mListView.setAdapter(adapter);
			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {

					Intent intent = new Intent(getApplicationContext(), SculptureActivity.class);
					intent.putExtra(GALLERY_SCULPTURE_ID, pos);
					
					startActivityForResult(intent, MainActivity.REQUEST_CODE_CHANGE_BUTTON);

				}
			});

		}

	}

	
}
