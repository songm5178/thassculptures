package edu.rosehulman.thassculptures;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.appspot.thassculptures.sculptures.Sculptures;
import com.appspot.thassculptures.sculptures.Sculptures.Comment;
import com.appspot.thassculptures.sculptures.model.CommentCollection;
import com.appspot.thassculptures.sculptures.model.Sculpture;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;

public class SculptureActivity extends Activity {

	private Sculptures mService;
	private Sculpture mSculpture;
	private ListView listView;
	ImageView sculptureImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sculpture);

		Sculptures.Builder builder = new Sculptures.Builder(AndroidHttp.newCompatibleTransport(),
				new GsonFactory(), null);
		mService = builder.build();

		Intent intent = getIntent();

		int pos = intent.getIntExtra(GalleryActivity.GALLERY_SCULPTURE_ID, -1);
		if (pos != -1) {
			mSculpture = GalleryActivity.mSculptures.get(pos);
		} else {
			pos = intent.getIntExtra(ToursMapActivity.TOURS_MAP_SCULPTURE_ID, -1);
			mSculpture = ToursMapActivity.mSculptures.get(pos);
		}

		setUpSculptureCard();

		listView = (ListView) findViewById(R.id.listview_comments);

		new QueryForCommentsTask().execute();

	}

	private void setUpSculptureCard() {
		TextView sculptureName = (TextView) findViewById(R.id.textView_sculptureName);
		TextView sculptureArtist = (TextView) findViewById(R.id.textView_sculptureArtist);
		TextView sculptureAddress = (TextView) findViewById(R.id.textView_sculptureAddress);
		TextView sculptureDescription = (TextView) findViewById(R.id.textView_sculptureDescription);
		sculptureImage = (ImageView) findViewById(R.id.imageView_sculpture);

		sculptureName.setText(mSculpture.getTitle());
		sculptureArtist.setText(mSculpture.getArtist());
		sculptureAddress.setText(mSculpture.getLocation());
		sculptureDescription.setText(mSculpture.getDescription());
		String url = mSculpture.getImage();
		Bitmap bitmap = SculptureListAdapter.getBitmapFromMemCache(url);
		if (bitmap == null) {
			new DownloadImageTask().execute(url);
		} else {
			sculptureImage.setImageBitmap(SculptureListAdapter.getBitmapFromMemCache(mSculpture
					.getImage()));
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sculpture, menu);
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
		} else if (id == R.id.artist_profile_menu) {

			Intent intent = new Intent(getApplicationContext(), ArtistPageActivity.class);
			startActivityForResult(intent, MainActivity.REQUEST_CODE_CHANGE_BUTTON);
		}
		return super.onOptionsItemSelected(item);
	}

	class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

		private String mUrl;

		@Override
		protected Bitmap doInBackground(String... params) {
			//
			Bitmap bitmap = null;
			mUrl = params[0];
			try {
				InputStream in = new java.net.URL(mUrl).openStream();
				//
				bitmap = BitmapFactory.decodeStream(in);

			} catch (MalformedURLException e) {
				// Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}

			return bitmap;

		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// Auto-generated method stub
			sculptureImage.setImageBitmap(result);
			SculptureListAdapter.addBitmapToMemoryCache(mUrl, result);
		}
	}

	/****
	 * Method for Setting the Height of the ListView dynamically. Hack to fix
	 * the issue of not showing all the items of the ListView when placed inside
	 * a ScrollView
	 ****/
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null)
			return;

		int desiredWidth = MeasureSpec
				.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
		int totalHeight = 0;
		View view = null;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			view = listAdapter.getView(i, view, listView);
			if (i == 0)
				view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
						LayoutParams.WRAP_CONTENT));

			view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
			totalHeight += view.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
	}

	class QueryForCommentsTask extends AsyncTask<Void, Void, CommentCollection> {

		@Override
		protected CommentCollection doInBackground(Void... arg0) {
			CommentCollection rtn = null;
			try {
				Comment.List query = mService.comment().list();
				query.setOrder("-timestamp");
				query.setLimit(50L);
				rtn = query.execute();
			} catch (IOException e) {
				Log.e("MIN", "failed loading comments " + e);
			}
			return rtn;
		}

		@Override
		protected void onPostExecute(CommentCollection result) {
			//
			super.onPostExecute(result);
			if (result == null) {
				Log.d("MIN", "Failed loading comments");
				return;
			}
			ArrayList<com.appspot.thassculptures.sculptures.model.Comment> comments = new ArrayList<com.appspot.thassculptures.sculptures.model.Comment>();
			for (com.appspot.thassculptures.sculptures.model.Comment c : result.getItems()) {
				if (mSculpture.getEntityKey().equals(c.getSculptureKey())) {
					comments.add(c);
				}
			}
			CommentsListAdapter adapter = new CommentsListAdapter(SculptureActivity.this, comments);

			listView.setAdapter(adapter);
			listView.setOnTouchListener(new OnTouchListener() {
				// Setting on Touch Listener for handling the touch inside
				// ScrollView
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// Disallow the touch request for parent scroll on touch of
					// child view
					v.getParent().requestDisallowInterceptTouchEvent(true);
					return false;
				}
			});
			setListViewHeightBasedOnChildren(listView);
		}
	}
}
