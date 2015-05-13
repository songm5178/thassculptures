package edu.rosehulman.thassculptures;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.appspot.thassculptures.sculptures.Sculptures;
import com.appspot.thassculptures.sculptures.Sculptures.Sculpture.List;
import com.appspot.thassculptures.sculptures.model.Sculpture;
import com.appspot.thassculptures.sculptures.model.SculptureCollection;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;

public class ToursMapActivity extends FragmentActivity implements OnMapReadyCallback {

	private Sculptures mService;
	private ListView listView;
	public static java.util.List<Sculpture> mSculptures;
	private double[] loc;
	public static final String TOURS_MAP_SCULPTURE_ID = "TOURS_MAP";
	public static GoogleMap m;
	private HashMap<Integer, Marker> markers;

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
		markers = new HashMap<Integer, Marker>();
		updateQuotes();
	}

	@Override
	protected void onResume() {
		loc = getGPS();
		super.onResume();
	}

	private void updateQuotes() {
		new QueryForSculpturesTask().execute();
	}

	@Override
	public void onMapReady(GoogleMap map) {
		// map.addMarker(new MarkerOptions().position(new LatLng(0,
		// 0)).title("Marker"));
		m = map;
		LatLng curr = new LatLng(loc[0], loc[1]);
		Marker currMarker = map.addMarker(new MarkerOptions().position(curr).title(
				"CurrentLocation"));
		markers.put(-1, currMarker);
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(curr, 13));
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

	public double[] getGPS() {
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		java.util.List<String> providers = lm.getProviders(true);
		System.out.println(providers);
		/*
		 * Loop over the array backwards, and if you get an accurate location,
		 * then break out the loop
		 */
		Location l = null;

		for (int i = providers.size() - 1; i >= 0; i--) {
			l = lm.getLastKnownLocation(providers.get(i));
			if (l != null)
				break;
		}

		double[] gps = new double[2];
		if (l != null) {
			gps[0] = l.getLatitude();
			gps[1] = l.getLongitude();
		}
		return gps;
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
			mSculptures = items;
			for (Sculpture s : items) {
				sculptureNames.add(s.getTitle());
			}
			SculptureListAdapter adapterTemp = new SculptureListAdapter(getApplicationContext(),
					result.getItems());// TODO: find use of this with bottom

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(ToursMapActivity.this,
					android.R.layout.simple_list_item_1, sculptureNames);
			listView.setAdapter(adapter);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {

					Intent intent = new Intent(getApplicationContext(), SculptureActivity.class);
					intent.putExtra(TOURS_MAP_SCULPTURE_ID, pos);

					startActivityForResult(intent, MainActivity.REQUEST_CODE_CHANGE_BUTTON);

				}
			});
			listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

				@Override
				public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
					return false;
				}

				@Override
				public void onDestroyActionMode(ActionMode mode) {
					for (Marker marker : markers.values()) {
						marker.remove();

					}
					markers.clear();

					onMapReady(m);

					mode = null;
				}

				@Override
				public boolean onCreateActionMode(ActionMode arg0, Menu arg1) {
					// do nothing
					return true;

				}

				@Override
				public boolean onActionItemClicked(ActionMode mode, MenuItem menu) {
					for (Marker marker : markers.values()) {
						marker.remove();

					}
					markers.clear();

					onMapReady(m);

					mode.finish();
					return false;
				}

				@Override
				public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
						boolean checked) {
					if (checked) {
						Sculpture s = mSculptures.get(position);
						String latlon = s.getLocation();
						double lat = 0;
						double lon = 0;
						String temp = "";
						for (int i = 0; i < latlon.length(); i++) {
							if (latlon.charAt(i) == ',') {
								lat = Double.parseDouble(latlon.substring(0, i));
								lon = Double.parseDouble(latlon.substring(i + 1, latlon.length()));

							}
						}

						Marker marker = m.addMarker(new MarkerOptions().position(
								new LatLng(lat, lon)).title(s.getTitle()));
						markers.put(position, marker);

					} else {
						markers.get(position).remove();
						markers.remove(position);
					}
					LatLngBounds.Builder builder = new LatLngBounds.Builder();
					for (Marker marker : markers.values()) {
						builder.include(marker.getPosition());
					}
					LatLngBounds bounds = builder.build();
					CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
					m.animateCamera(cu);
				}
			});

		}
	}

}
