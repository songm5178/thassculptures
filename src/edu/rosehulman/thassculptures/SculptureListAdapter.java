package edu.rosehulman.thassculptures;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.appspot.thassculptures.sculptures.model.Sculpture;

public class SculptureListAdapter extends ArrayAdapter<Sculpture> {

	private Context mContext;
	private List<Sculpture> mSculptures;
	public static LruCache<String, Bitmap> mMemoryCache;


	public SculptureListAdapter(Context context, List<Sculpture> sculptures) {
		super(context, 0, sculptures);
		mContext = context;
		mSculptures = sculptures;
		
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 2;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				//
				return value.getByteCount() / 1024;
			}
		};

	}

	@Override
	public void add(Sculpture object) {
		//
		mSculptures.add(object);
	}

	@Override
	public Sculpture getItem(int position) {
		//
		return mSculptures.get(position);
	}

	@Override
	public int getCount() {
		//
		return mSculptures.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SculptureRowView v;
		if (convertView == null) {
			v = new SculptureRowView(mContext);
		} else {
			v = (SculptureRowView) convertView;
		}

		Sculpture sculpture = mSculptures.get(position);


		v.setImageUrl(sculpture.getImage());
		v.setTitleText(sculpture.getTitle());
		return v;
	}
	public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	public static Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}
	
}
