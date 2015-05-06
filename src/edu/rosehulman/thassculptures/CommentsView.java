package edu.rosehulman.thassculptures;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommentsView extends LinearLayout {

	TextView mAuthor;
	TextView mContent;

	public CommentsView(Context context) {
		super(context);

		LayoutInflater.from(context).inflate(R.layout.view_comments, this);
		mAuthor = (TextView) findViewById(R.id.textview_comment_author);
		mContent = (TextView) findViewById(R.id.textview_comment_content);
	}

	public void setCommentAuthor(String author) {
		mAuthor.setText(author);

	}

	public void setCommentContent(String content) {
		mContent.setText(content);

	}

}
