package edu.rosehulman.thassculptures;

import java.io.IOException;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.appspot.thassculptures.sculptures.Sculptures;
import com.appspot.thassculptures.sculptures.Sculptures.Sculpture.List;
import com.appspot.thassculptures.sculptures.model.Sculpture;
import com.appspot.thassculptures.sculptures.model.SculptureCollection;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;

public class ToursMapActivity extends FragmentActivity implements OnMapReadyCallback {

	private Sculptures mService;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tours_map);
		MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		listView = (ListView) findViewById(R.id.listview_sculptures_tours);

		Sculptures.Builder builder = new Sculptures.Builder(AndroidHttp.newCompatibleTransport(),
				new GsonFactory(), null);
		mService = builder.build();

		updateQuotes();
	}

	private void updateQuotes() {
		new QueryForSculpturesTask().execute();
	}

	@Override
	public void onMapReady(GoogleMap map) {
		map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tours_map, menu);
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
			ArrayList<String> sculptureNames = new ArrayList<String>();
			java.util.List<Sculpture> items = result.getItems();
			for (Sculpture s : items) {
				sculptureNames.add(s.getTitle());
			}

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(ToursMapActivity.this,
					android.R.layout.simple_list_item_1, sculptureNames);
			listView.setAdapter(adapter);
		}

	}

}
