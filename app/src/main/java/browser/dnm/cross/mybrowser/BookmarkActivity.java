package browser.dnm.cross.mybrowser;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Browser;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class BookmarkActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		AvLog.i("");
		setListAdapter(getAdapter());
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		HashMap<String, String> item = (HashMap<String, String>) l.getAdapter().getItem(position);
		Intent intent = new Intent();
		intent.putExtra("URL", item.get("url"));
		setResult(0, intent);

		finish();
	}

	public SimpleAdapter getAdapter() {

		ContentResolver cr = this.getContentResolver();

		Cursor cursor = cr.query(
                Browser.BOOKMARKS_URI,
                new String[] {
				Browser.BookmarkColumns.URL,
                Browser.BookmarkColumns.TITLE },
				"bookmark= 1", null, null);

		ArrayList<HashMap<String, String>> bookmarks = new ArrayList<HashMap<String, String>>();

		cursor.moveToFirst();
		do {
			String url = cursor.getString(cursor.getColumnIndex(Browser.BookmarkColumns.URL));
			String title = cursor.getString(cursor.getColumnIndex(Browser.BookmarkColumns.TITLE));

			HashMap<String, String> bookmark = new HashMap<String, String>();
			bookmark.put("title", title);
			bookmark.put("url", url);
			bookmarks.add(bookmark);

		} while (cursor.moveToNext());

		SimpleAdapter adapter = new SimpleAdapter(this, bookmarks, android.R.layout.simple_list_item_2, new String[] { "title", "url" }, new int[] { android.R.id.text1, android.R.id.text2 });

		return adapter;
	}
}
