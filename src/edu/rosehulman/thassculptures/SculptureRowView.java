package edu.rosehulman.thassculptures;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SculptureRowView extends LinearLayout {

	private ImageView image;
	private TextView title;

	public SculptureRowView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.view_gallery, this);
		image = (ImageView) findViewById(R.id.thumbnail_sculpture_image);
		title = (TextView) findViewById(R.id.thumbnail_textView_sculpture_title);

	}

	public void setImageUrl(String url) {
		Bitmap bitmap = SculptureListAdapter.getBitmapFromMemCache(url);
		if (bitmap == null) {
			new DownloadImageTask().execute(url);
		} else {
			image.setImageBitmap(bitmap);
		}
	}

	public void setTitleText(String string) {
		title.setText(string);
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

			SculptureListAdapter.addBitmapToMemoryCache(mUrl, result);
			image.setImageBitmap(result);
		}

	}

}
