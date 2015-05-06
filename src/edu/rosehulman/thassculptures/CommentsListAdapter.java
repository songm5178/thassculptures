package edu.rosehulman.thassculptures;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.appspot.thassculptures.sculptures.model.Comment;

public class CommentsListAdapter extends ArrayAdapter<Comment> {
	
	ArrayList<Comment> mComments;
	Context mContext;
	public CommentsListAdapter(Context context, ArrayList<Comment> comments){
		super(context, 0, comments);
		mComments = comments;
		mContext = context;
	}
	
	
	@Override
	public Comment getItem(int position) {
		
		return mComments.get(position);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CommentsView v;
		
		if(convertView == null){
			v = new CommentsView(mContext);
		}else{
			v = (CommentsView) convertView;
		}
		Comment comment = mComments.get(position);

		v.setCommentAuthor(comment.getAuthor());
		v.setCommentContent(comment.getContent());
		return v;
	}
}
